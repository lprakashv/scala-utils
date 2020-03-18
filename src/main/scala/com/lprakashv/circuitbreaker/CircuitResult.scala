package com.lprakashv.circuitbreaker

import com.lprakashv.circuitbreaker.CircuitResult.{
  CircuitFailure,
  CircuitSuccess
}

trait CircuitResult[T] extends IterableOnce[T] with Product with Serializable {
  def isFailed: Boolean = this.toOption.isEmpty

  def isSuccess: Boolean = this.toOption.isDefined

  def toOption: Option[T] = this match {
    case CircuitSuccess(value) => Some(value)
    case _                     => None
  }

  def toEither: Either[Throwable, T] = this match {
    case CircuitSuccess(value: T)  => Right(value)
    case CircuitFailure(exception) => Left(exception)
  }
}

object CircuitResult {
  case class CircuitSuccess[T](value: T) extends CircuitResult[T] {
    override def iterator: Iterator[T] = Iterator.apply(value)
  }
  case class CircuitFailure[T](exception: Throwable) extends CircuitResult[T] {
    override def iterator: Iterator[T] = Iterator.empty[T]
  }
}
