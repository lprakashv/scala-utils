package com.lprakashv

object StringUtils {
  private def transformWord(word: String,
                            positionList: List[Int],
                            placeOnPosition: Char => String
                           ): String = {
    word.foldLeft((0, positionList, List.empty[String])) {
      case ((index, Nil, acc), c) => (index + 1, Nil, c.toString :: acc)
      case ((index, ph :: pt, acc), c) if index == ph => (index + 1, pt, placeOnPosition(c) :: acc)
      case ((index, p, acc), c) => (index + 1, p, c.toString :: acc)
    }._3.reverse.mkString
  }

  def camelCasedWord(word: String,
                     startsWithCapital: Boolean = false,
                     positions: Option[List[Int]] = None): Either[String, String] = {
    if (word.contains(" ")) Left("CONTAINS_SPACE")
    else {
      Right(positions.map(_.sorted) match {
        case Some(positionsList: List[Int]) =>
          transformWord(word.toLowerCase, positionsList, _.toUpper.toString)
        case None =>
          word.toLowerCase.foldLeft(List.empty[String], false: Boolean) {
            case ((acc: List[String], capNext: Boolean), c: Char) =>
              val allowedChars = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toSet

              if (allowedChars.contains(c) && capNext) (c.toUpper.toString :: acc, false)
              else if (!allowedChars.contains(c)) (acc, true)
              else (c.toString :: acc, false)
          }._1.reverse.mkString
      })
    }
  }

  def underscoredWord(word: String,
                      symbol: String = "_",
                      positions: Option[List[Int]] = None): Either[String, String] = {
    if (word.contains(" ")) Left("CONTAINS_SPACE")
    else {
      Right(positions.map(_.sorted) match {
        case Some(positionsList: List[Int]) =>
          transformWord(word.toLowerCase, positionsList, c => s"_$c")

        case None =>
          val l = word.map {
            case c: Char if c.isUpper => s"$symbol${c.toLower}"
            case c: Char => c.toString
          }.mkString
          if (l.headOption.exists(_.toString == symbol)) l.tail
          else l
      })
    }
  }
}
