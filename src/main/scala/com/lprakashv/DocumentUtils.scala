package com.lprakashv

import java.io.BufferedWriter

object DocumentUtils {
  private def transformDocumentKeys(sourcePath: String,
                                    destPath: String,
                                    kvSeparator: String,
                                    wordTransformer: String => Either[String, String]): Unit = {
    import FileUtils._

    implicit val writer: BufferedWriter = writerOfPath(destPath)
    val inReader = readerOfPath(sourcePath)

    inReader.getLines()
      .map(l =>
        if (l.contains(kvSeparator)) {
          val splitLine = l.split(":")

          splitLine(0) = splitLine(0)
            .split(" ", -1)
            .map {
              case x if x.startsWith("\"") && x.endsWith("\"") =>
                "\"" + wordTransformer(x.substring(1, x.length - 1)).getOrElse("") + "\""
              case x =>
                wordTransformer(x).getOrElse("")
            }.mkString(" ")

          splitLine.mkString(kvSeparator)
        } else l
      ).foreach(writeLine)

    inReader.close()
    writer.close()
  }

  def underscoreDocumentKeys(sourcePath: String,
                             destPath: String,
                             kvSeparator: String,
                             underscoreSymbol: String = "_"
                            ): Unit = {
    import StringUtils._

    transformDocumentKeys(sourcePath, destPath, ":", underscoredWord(_, underscoreSymbol))
  }

  def camelcaseDocumentKeys(sourcePath: String,
                            destPath: String,
                            kvSeparator: String,
                            startsWithCapital: Boolean = false
                           ): Unit = {
    import StringUtils._

    transformDocumentKeys(sourcePath, destPath, ":", camelCasedWord(_, startsWithCapital))
  }

  def jsonPrettyString(obj: Any,
                       currentLevel: Int = 0,
                       tabLength: Int = 1): String = {
    obj match {
      case l: Int => s"$l"
      case l: Long => s"$l"
      case str: String => s""""$str""""
      case m: Map[String, Any] =>
        "{\n" +
          m.map {
            case (k: String, s: Any) => (" " * tabLength * (currentLevel + 2)) + s""""$k": ${jsonPrettyString(s, currentLevel + 4)}"""
          }.mkString(",\n") +
          "\n" + (" " * tabLength * currentLevel) + "}"
      case s: Seq[Any] => "[\n" + s.map { a: Any => (" " * tabLength * (currentLevel + 2)) + jsonPrettyString(a, currentLevel + 4) }.mkString(",\n") +
        "\n" + (" " * tabLength * currentLevel) + "]"
      case _ => ""
    }
  }
}
