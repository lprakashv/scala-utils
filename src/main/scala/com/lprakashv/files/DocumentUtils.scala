package com.lprakashv.files

import java.io.BufferedWriter

object DocumentUtils {
  private def transformDocumentKeys(
    sourcePath: String,
    destPath: String,
    kvSeparator: String,
    wordTransformer: String => Either[String, String]
  ): Unit = {
    import com.lprakashv.files.FileUtils._

    implicit val writer: BufferedWriter = writerOfPath(destPath)
    val inReader = readerOfPath(sourcePath)

    inReader
      .getLines()
      .map(
        l =>
          if (l.contains(kvSeparator)) {
            val splitLine = l.split(":")

            splitLine(0) = splitLine(0)
              .split(" ", -1)
              .map {
                case x if x.startsWith("\"") && x.endsWith("\"") =>
                  "\"" + wordTransformer(x.substring(1, x.length - 1))
                    .getOrElse("") + "\""
                case x =>
                  wordTransformer(x).getOrElse("")
              }
              .mkString(" ")

            splitLine.mkString(kvSeparator)
          } else l
      )
      .foreach(writeLine)

    inReader.close()
    writer.close()
  }

  def snakeCaseDocumentKeys(sourcePath: String,
                            destPath: String,
                            kvSeparator: String,
                            snakeSymbol: String = "_"): Unit = {
    import com.lprakashv.strings.StringUtils._

    transformDocumentKeys(
      sourcePath,
      destPath,
      ":",
      snakeCasedWord(_, snakeSymbol)
    )
  }

  def camelcaseDocumentKeys(sourcePath: String,
                            destPath: String,
                            kvSeparator: String,
                            startsWithCapital: Boolean = false): Unit = {
    import com.lprakashv.strings.StringUtils._

    transformDocumentKeys(
      sourcePath,
      destPath,
      ":",
      camelCasedWord(_, startsWithCapital)
    )
  }

  def jsonPrettyString(obj: Any,
                       currentLevel: Int = 0,
                       tabLength: Int = 1): String = {
    val currentTab: String = " " * tabLength * currentLevel
    val nextTab: String = " " * tabLength * (currentLevel + 2)

    obj match {
      case l: Int      => s"$l"
      case l: Long     => s"$l"
      case str: String => s""""$str""""
      case m: Map[String, Any] =>
        m.map {
            case (k: String, s: Any) =>
              s"""$nextTab"$k": ${jsonPrettyString(s, currentLevel + 4)}"""
          }
          .mkString("{\n", ",\n", s"\n$currentTab}")
      case s: Seq[Any] =>
        s.map { a: Any =>
            s"$nextTab${jsonPrettyString(a, currentLevel + 4)}"
          }
          .mkString("[\n", ",\n", s"\n$currentTab]")
      case unknown => throw new Exception(s"Unrecognized element: $unknown")
    }
  }
}
