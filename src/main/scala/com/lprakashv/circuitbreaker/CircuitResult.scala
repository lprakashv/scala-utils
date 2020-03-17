package com.lprakashv.circuitbreaker

import com.lprakashv.circuitbreaker.CircuitResult.{
  CircuitFailure,
  CircuitSuccess
}

trait CircuitResult[T] {
  def getOrElse(f: T): T = this match {
    case CircuitSuccess(value) => value
    case CircuitFailure(_)     => f
  }

  def map[R](f: T => R): Option[R] = this match {
    case CircuitSuccess(value) => Some(f(value))
    case CircuitFailure(_)     => None
  }

  def toEither: Either[Throwable, T] = this match {
    case CircuitSuccess(value: T)  => Right(value)
    case CircuitFailure(exception) => Left(exception)
  }
}

object CircuitResult {
  case class CircuitSuccess[T](value: T) extends CircuitResult[T]
  case class CircuitFailure[T](exception: Throwable) extends CircuitResult[T]
}
