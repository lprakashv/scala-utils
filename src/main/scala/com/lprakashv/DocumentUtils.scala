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
}
