package com.lprakashv

import org.scalatest.FunSuite

class FloatingNumberTest extends FunSuite {
  test("1/3 == 0.333") {
    import com.lprakashv.commons.FloatingNumber.Implicits._
    assert(BigDecimal(1.0) / 3 ~= 0.333)
  }

  test("PI != 0.33 with 0.0001 precision") {
    import com.lprakashv.commons.FloatingNumber.Implicits._
    assert(!(math.Pi ~= (BigDecimal(3.1), 0.0001)))
  }

  test("PI == 0.33 with 0.1 precision") {
    import com.lprakashv.commons.FloatingNumber.Implicits._
    assert(math.Pi ~= (3.1, 0.1))
  }

  test("PI == 0.33") {
    import com.lprakashv.commons.FloatingNumber.Implicits._
    assert(math.Pi.floatValue() ~= (3.1f, 0.1))
  }
}
