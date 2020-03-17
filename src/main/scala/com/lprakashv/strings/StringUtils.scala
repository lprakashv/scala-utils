package com.lprakashv.strings

object StringUtils {
  private def transformWord(word: String,
                            positionList: Set[Int],
                            placeOnPosition: Char => String): String = {
    word.zipWithIndex.collect {
      case (c, i) if positionList.contains(i) => placeOnPosition(c)
      case (c, _)                             => c.toString
    }.mkString
  }

  def camelCasedWord(
    word: String,
    startsWithCapital: Boolean = false,
    snakeSymbol: String = "_",
    positions: Option[Set[Int]] = None
  ): Either[String, String] = {
    if (word.contains(" ")) Left("CONTAINS_SPACE")
    else {
      Right(positions match {
        case Some(positionsList: Set[Int]) =>
          transformWord(word.toLowerCase, positionsList, _.toUpper.toString)
        case None if startsWithCapital =>
          camelCasedWord(word.tail, startsWithCapital = false, snakeSymbol)
            .map(right => word.head.toUpper + right)
            .getOrElse("") // impossible
        case None =>
          word.toLowerCase
            .foldLeft(List.empty[String], false: Boolean) {
              case ((acc: List[String], capNext: Boolean), c: Char) =>
                val allowedChars =
                  (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toSet

                if (allowedChars.contains(c) && capNext)
                  (c.toUpper.toString :: acc, false)
                else if (!allowedChars.contains(c)) (acc, true)
                else (c.toString :: acc, false)
            }
            ._1
            .reverse
            .mkString
      })
    }
  }

  def snakeCasedWord(
    word: String,
    snakeSymbol: String = "_",
    positions: Option[Set[Int]] = None
  ): Either[String, String] = {
    if (word.contains(" ")) Left("CONTAINS_SPACE")
    else {
      Right(positions match {
        case Some(positionsList: Set[Int]) =>
          transformWord(word.toLowerCase, positionsList, c => s"$snakeSymbol$c")

        case None =>
          val l = word.map {
            case c: Char if c.isUpper => s"$snakeSymbol${c.toLower}"
            case c: Char              => c.toString
          }.mkString
          if (l.headOption.exists(_.toString == snakeSymbol)) l.tail
          else l
      })
    }
  }
}
