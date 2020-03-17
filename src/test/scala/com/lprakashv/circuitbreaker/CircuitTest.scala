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

  test("test success") {
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

  test("failures 5 times") {
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

  test("success with default answer after failure 5 times") {
    val sampleCircuit =
      new Circuit[Int]("sample-circuit", 5, 5.seconds, 1, -1)

    val resultsF = Future.sequence(
      (1 to 12).map(_ => Future { sampleCircuit.execute(1 / 0) })
    )

    val results = Await.result(resultsF, 1.minutes).toList

    assert(results.count {
      case CircuitSuccess(-1) => true
      case _                  => false
    } == 7, s"got $results")
  }

  test(
    "failure 5 times and then success and again failure after timeout (5 sec)"
  ) {
    val sampleCircuit =
      new Circuit[Int]("sample-circuit", 5, 5.seconds, 1, -1)

    // 5 failures and then 5 successes
    (1 to 10).foreach(_ => Future { sampleCircuit.execute(1 / 0) })

    Thread.sleep(5100)

    assert(sampleCircuit.execute(1 / 0) match {
      case CircuitFailure(_) => true
      case _                 => false
    })
  }

  test(
    "failure 5 times and then successes then failure after timeout (5 sec) and then sucesses again"
  ) {
    val sampleCircuit =
      new Circuit[Int]("sample-circuit", 5, 5.seconds, 1, -1)

    // 5 failures and then 5 successes
    (1 to 10).foreach(_ => Future { sampleCircuit.execute(1 / 0) })

    Thread.sleep(5100)

    sampleCircuit.execute(1 / 0)

    assert(sampleCircuit.execute(1 / 0) match {
      case CircuitSuccess(-1) => true
      case _                  => false
    })
  }
}
