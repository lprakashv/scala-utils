package com.lprakashv.circuitbreaker

import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong, AtomicReference}
import java.util.{Date, Timer, TimerTask}

import com.lprakashv.circuitbreaker.CircuitResult.{
  CircuitFailure,
  CircuitSuccess
}
import com.lprakashv.circuitbreaker.CircuitState._

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class Circuit[R](
  name: String,
  private final val threshold: Int,
  private final val timeout: Duration,
  private final val maxAllowedHalfOpen: Int,
  defaultAction: => R,
  private val logger: String => Unit
) /*(implicit ec: ExecutionContext)*/ {
  private def circuitLogger(msg: String): Unit =
    logger(s"[$name] [${new Date()}] $msg")

  private val failureCount = new AtomicInteger(0)
  private val lastOpenTime = new AtomicLong(Long.MaxValue)
  private val state = new AtomicReference[CircuitState](CircuitState.Closed)
  private val atomicMaxAllowedHalfOpen = new AtomicInteger(maxAllowedHalfOpen)
  private val circuitOpenerTimer =
    new Timer(s"$name-circuit-opener").scheduleAtFixedRate(
      new TimerTask {
        override def run(): Unit = state.get() match {
          case Open
              if (System
                .currentTimeMillis() - lastOpenTime.get() > timeout.toMillis) =>
            circuitLogger("Max open timeout reached.")
            halfOpenCircuit
          case _ => ()
        }
      },
      0L,
      10L
    )

  val semaphore = new Semaphore(threshold)

  private def openCircuit: Unit = synchronized {
    circuitLogger("Opening circuit...")
    state.set(Open)
    lastOpenTime.set(System.currentTimeMillis())
    circuitLogger("Circuit is open.")
  }

  private def closeCircuit: Unit = synchronized {
    circuitLogger("Closing circuit...")
    state.set(Closed)
    lastOpenTime.set(Long.MaxValue)
    failureCount.set(0)
    circuitLogger("Circuit is closed.")
  }

  private def halfOpenCircuit: Unit = synchronized {
    circuitLogger("Half-opening circuit...")
    state.set(HalfOpen)
    atomicMaxAllowedHalfOpen.set(maxAllowedHalfOpen)
    circuitLogger("Circuit is half-open.")
  }

  private def handleClosed(block: => R): CircuitResult[R] = {
    Try(block) match {
      case Success(value) =>
        failureCount.set(0)
        CircuitSuccess(value)
      case Failure(exception) =>
        val currentFailureCount = failureCount.incrementAndGet()
        circuitLogger(s"[error-count = $currentFailureCount] $exception")
        if (currentFailureCount > threshold) {
          openCircuit
          execute(block)
        } else CircuitFailure(exception)
    }
  }

  private def handleHalfOpen(block: => R): CircuitResult[R] = synchronized {
    if (state.get() == HalfOpen) {
      Try(block) match {
        case Success(value) =>
          closeCircuit
          CircuitSuccess(value)
        case Failure(exception) =>
          if (atomicMaxAllowedHalfOpen.decrementAndGet() <= 0) {
            circuitLogger("Max half-open limit reached.")
            openCircuit
          }
          CircuitFailure[R](exception)
      }
    } else execute(block)
  }

  private def handleOpen: CircuitResult[R] = {
    CircuitSuccess(defaultAction)
  }

  def execute(block: => R): CircuitResult[R] = {
    state.get() match {
      case Closed   => handleClosed(block)
      case HalfOpen => handleHalfOpen(block)
      case Open     => handleOpen
    }
  }
}
