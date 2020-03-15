package com.lprakashv

import org.scalatest.FunSuite

class StringUtilsTest extends FunSuite {
  test(
    "StringUtils.camelCasedWord returns Left(CONTAINS_SPACE) if contains space"
  ) {
    assert(
      StringUtils.camelCasedWord("asdjn aasdoia") == Left("CONTAINS_SPACE")
    )
  }

  test(
    "StringUtils.snakeCasedWord returns Left(CONTAINS_SPACE) if contains space"
  ) {
    assert(
      StringUtils.snakeCasedWord("asdjn aasdoia") == Left("CONTAINS_SPACE")
    )
  }

  test("StringUtils.camelCasedWord for this_is_a_word is thisIsAWord") {
    assert(StringUtils.camelCasedWord("this_is_a_word") == Right("thisIsAWord"))
  }

  test("StringUtils.snakeCasedWord for thisIsAWord is this_is_a_word") {
    assert(StringUtils.snakeCasedWord("thisIsAWord") == Right("this_is_a_word"))
  }

  test(
    "StringUtils.camelCasedWord for this-is-a-word with snake symbol = \"-\" is thisIsAWord"
  ) {
    assert(
      StringUtils.camelCasedWord("this-is-a-word", false, "-") == Right(
        "thisIsAWord"
      )
    )
  }

  test(
    "StringUtils.snakeCasedWord for thisIsAWord with snake symbol = \"-\" is this-is-a-word"
  ) {
    assert(
      StringUtils.snakeCasedWord("thisIsAWord", "-") == Right("this-is-a-word")
    )
  }

  test(
    "StringUtils.camelCasedWord for this_is_a_word with starting capital is ThisIsAWord"
  ) {
    assert(
      StringUtils
        .camelCasedWord("this_is_a_word", startsWithCapital = true) == Right(
        "ThisIsAWord"
      )
    )
  }

  test(
    "StringUtils.snakeCasedWord for thisisaword with positions[1,4,6] is t_his_is_aword"
  ) {
    assert(
      StringUtils
        .snakeCasedWord("thisisaword", positions = Some(Set(1, 4, 6))) == Right(
        "t_his_is_aword"
      )
    )
  }

  test(
    "StringUtils.camelCasedWord for thisisaword with positions[1,4,6] capital is t_his_is_aword"
  ) {
    assert(
      StringUtils
        .camelCasedWord("thisisaword", positions = Some(Set(1, 4, 6))) == Right(
        "tHisIsAword"
      )
    )
  }

  test("StringUtils.snakeCasedWord for _thisIsAWord is this_is_a_word") {
    assert(
      StringUtils.snakeCasedWord("_thisIsAWord") == Right("this_is_a_word")
    )
  }
}
