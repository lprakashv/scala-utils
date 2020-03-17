package com.lprakashv.circuitbreaker

import com.lprakashv.circuitbreaker.CircuitResult.{
  CircuitFailure,
  CircuitSuccess
}

import scala.concurrent.{ExecutionContext, Future}

object CircuitImplicits {
  implicit class BlockExtensions[R](block: => R)(implicit c: Circuit[R]) {
    def execute: CircuitResult[R] = c.execute(block)

    def executeAsync(implicit ex: ExecutionContext): Future[CircuitResult[R]] =
      Future { c.execute(block) }
  }

  implicit class CircuitResultExtensions[R](circuitResult: CircuitResult[R]) {
    def toEither: Either[Throwable, R] = circuitResult match {
      case CircuitSuccess(value: R)  => Right(value)
      case CircuitFailure(exception) => Left(exception)
    }
  }
}
