package com.lprakashv

object Main extends App {

  val inFilePath = "src/main/resources/MOCK_DATA.json"
  val outFilePath = args(0)

  val inFilePath2 = outFilePath
  val outFilePath2 = args(1)

  import DocumentUtils._

  camelcaseDocumentKeys(inFilePath, outFilePath, ":")
  underscoreDocumentKeys(inFilePath2, outFilePath2, ":")
}
