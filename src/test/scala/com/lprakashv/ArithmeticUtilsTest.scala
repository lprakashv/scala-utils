package com.lprakashv

import org.scalatest.FunSuite

class ArithmeticUtilsTest extends FunSuite {
  test("1/3 == 0.333") {
    import ArithmeticUtils._
    assert(1.0 / 3 ~= 0.333)
  }

  test("PI == 0.33 with 0.0001 precision") {
    import ArithmeticUtils._
    assert(!(math.Pi ~= (3.1, 0.0001)))
  }

  test("PI == 0.33 with 0.1 precision") {
    import ArithmeticUtils._
    assert(math.Pi ~= (3.1, 0.1))
  }

  test("PI == 0.33") {
    import ArithmeticUtils._
    assert(math.Pi ~= (3.1, 0.1))
  }
}
