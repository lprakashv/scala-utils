package com.lprakashv.circuitbreaker

import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong, AtomicReference}
import java.util.{Timer, TimerTask}

import com.lprakashv.circuitbreaker.CircuitResult.{
  CircuitFailure,
  CircuitSuccess
}
import com.lprakashv.circuitbreaker.CircuitState._

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class Circuit[R](name: String,
                 private final val threshold: Int,
                 private final val timeout: Duration,
                 private final val maxAllowedHalfOpen: Int,
                 defaultAction: => R) /*(implicit ec: ExecutionContext)*/ {
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
            state.set(HalfOpen)
          case _ => ()
        }
      },
      0L,
      10L
    )

  val semaphore = new Semaphore(threshold)

  private def openCircuit: Unit = {
    state.set(Open)
    lastOpenTime.set(System.currentTimeMillis())
  }

  private def closeCircuit: Unit = {
    state.set(Closed)
    lastOpenTime.set(Long.MaxValue)
  }

  private def handleClosed(block: => R): CircuitResult[R] = {
    def normalClosedFlow: CircuitResult[R] = Try(block) match {
      case Success(value) =>
        failureCount.set(0)
        CircuitSuccess(value)
      case Failure(exception) =>
        failureCount.incrementAndGet()
        CircuitFailure[R](exception)
    }

    semaphore.acquire(1 + failureCount.get()) // reason
    val permitted = 1 + failureCount.get()
    val result = if (failureCount.get() < threshold) {
      normalClosedFlow
    } else {
      openCircuit
      execute(block)
    }
    semaphore.release(permitted)
    result
  }

  private def handleHalfOpen(block: => R): CircuitResult[R] = {
    Try(block) match {
      case Success(value) =>
        atomicMaxAllowedHalfOpen.set(maxAllowedHalfOpen)
        closeCircuit
        CircuitSuccess(value)
      case Failure(exception) =>
        if (atomicMaxAllowedHalfOpen.decrementAndGet() <= 0) {
          openCircuit
        }
        CircuitFailure[R](exception)
    }
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
