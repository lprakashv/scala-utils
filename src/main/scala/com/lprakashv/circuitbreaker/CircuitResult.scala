package com.lprakashv.circuitbreaker

trait CircuitResult[T]

object CircuitResult {
  case class CircuitSuccess[T](value: T) extends CircuitResult[T]
  case class CircuitFailure[T](exception: Throwable) extends CircuitResult[T]
}
