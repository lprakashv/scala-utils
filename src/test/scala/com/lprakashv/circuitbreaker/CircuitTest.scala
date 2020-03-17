package com.lprakashv.circuitbreaker

import com.lprakashv.circuitbreaker.CircuitResult.{
  CircuitFailure,
  CircuitSuccess
}
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class CircuitTest extends FunSuite {

  test("test successes") {
    val sampleCircuit =
      new Circuit[Int]("sample-circuit", 5, 5.seconds, 1, -1)

    val resultsF = Future.sequence(
      (1 to 500).map(_ => Future { sampleCircuit.execute(2 + 2) })
    )

    val results = Await.result(resultsF, 10.minutes).toList

    assert(results.count {
      case CircuitSuccess(4) => true
      case _                 => false
    } == 500)
  }

  test(
    "[on continuous invalid execution] " +
      "failures 5 times"
  ) {
    val sampleCircuit =
      new Circuit[Int]("sample-circuit", 5, 5.seconds, 1, -1)

    val resultsF = Future.sequence(
      for {
        _ <- 1 to 5
      } yield
        Future {
          sampleCircuit.execute {
            1 / 0
          }
        }
    )

    val results = Await.result(resultsF, 1.minutes).toList

    assert(results.forall {
      case CircuitFailure(_) => true
      case _                 => false
    }, s"got $results")
  }

  test(
    "[on continuous invalid execution] " +
      "success with default answer after failure 5 times"
  ) {
    val sampleCircuit =
      new Circuit[Int]("sample-circuit", 5, 5.seconds, 1, -1)

    val resultsF = Future.sequence(
      (1 to 12).map(_ => Future { sampleCircuit.execute(1 / 0) })
    )

    val results = Await.result(resultsF, 1.minutes).toList

    assert(
      results.count {
        case CircuitSuccess(-1) => true
        case _                  => false
      } == 7,
      "failed to verify 7 successes (default case in open circuit) after 5 failures on 12 invalid executions"
    )
  }

  test(
    "[on continuous invalid execution] " +
      "failure 5 times and then successes then failure after timeout (5 sec) and then successes again"
  ) {
    val sampleCircuit =
      new Circuit[Int]("sample-circuit", 5, 5.seconds, 1, -1)

    // 5 failures and then 5 successes
    val resultsF = Future.sequence(
      (1 to 10).map(_ => Future { sampleCircuit.execute(1 / 0) })
    )
    val results = Await.result(resultsF, 10.minutes)

    assert(
      results.count {
        case CircuitSuccess(-1) => true
        case _                  => false
      } == 5,
      "failed to verify - 5 failures and 5 successes for 10 invalid executions"
    )

    Thread.sleep(5100)

    assert(sampleCircuit.execute(1 / 0) match {
      case CircuitFailure(_) => true
      case _                 => false
    }, "failed to verify failure after timeout (showing half-open try)")

    assert(
      sampleCircuit.execute(1 / 0) match {
        case CircuitSuccess(-1) => true
        case _                  => false
      },
      "failed to verify success showing open (default case) after half-open failure"
    )

    Thread.sleep(5100)

    val resultsF2 = Future.sequence(
      (1 to 5).map(_ => Future { sampleCircuit.execute(7 * 7) })
    )
    val results2 = Await.result(resultsF2, 10.minutes)

    assert(
      results2.forall {
        case CircuitSuccess(49) => true
        case _                  => false
      },
      "failed to verify successes with valid results after timeout showing closed after half-open success"
    )
  }

  test(
    "failure 5 times and then default successes even after having valid execution"
  ) {
    val sampleCircuit =
      new Circuit[Int]("sample-circuit", 5, 5.seconds, 1, -1)

    // 5 failures and then 5 successes
    Await.result(
      Future.sequence(
        (1 to 5).map(_ => Future { sampleCircuit.execute(1 / 0) })
      ),
      10.minutes
    )

    val resultsF = Future.sequence(
      (1 to 5).map(_ => Future { sampleCircuit.execute(7 * 7) })
    )
    val results = Await.result(resultsF, 10.minutes)

    assert(results.forall {
      case CircuitSuccess(-1) => true
      case _                  => false
    })
  }
}
