package com.lprakashv.commons

import scala.math.BigDecimal

object ArithmeticUtils {
  class FloatingNumber[T](value: T) {
    val bdval: BigDecimal = value match {
      case y: Double     => BigDecimal(y)
      case y: BigDecimal => y
      case y: Float      => BigDecimal(y)
    }

    final def ~=(y: Any, precision: Double = 0.001): Boolean = y match {
      case x: Double     => (bdval - x).abs < precision
      case x: Float      => (bdval.floatValue - x).abs < precision
      case x: BigDecimal => (bdval - x).abs < precision
      case _             => false
    }
  }

  implicit def doubleWrap(value: Double): FloatingNumber[Double] =
    new FloatingNumber(value)

  implicit def floatWrap(value: Float): FloatingNumber[Float] =
    new FloatingNumber(value)

  implicit def bigDecimalWrap(value: BigDecimal): FloatingNumber[BigDecimal] =
    new FloatingNumber(value)
}
