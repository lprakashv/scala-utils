package com.lprakashv

import com.lprakashv.files.DocumentUtils
import org.scalatest.{FunSpec, FunSuite}

import scala.util.Try

class DocumentUtilsTest extends FunSuite {
  test("Testing MOCK_DATA.json to camel cased MOCK_DATA_C.json") {
    DocumentUtils.camelcaseDocumentKeys(
      getClass.getClassLoader.getResource("MOCK_DATA.json").getFile,
      getClass.getClassLoader.getResource(".").getPath + "MOCK_DATA_C.json",
      ":"
    )
    assert(
      Try(getClass.getClassLoader.getResource("MOCK_DATA_C.json").getFile).isSuccess
    )
  }

  test("Testing MOCK_DATA_C.json to snake cased MOCK_DATA_S.json") {
    DocumentUtils.snakeCaseDocumentKeys(
      getClass.getClassLoader.getResource("MOCK_DATA.json").getFile,
      getClass.getClassLoader.getResource(".").getPath + "MOCK_DATA_S.json",
      ":"
    )
    assert(
      Try(getClass.getClassLoader.getResource("MOCK_DATA_S.json").getFile).isSuccess
    )
  }

  test("Testing jsonPrettyString") {
    assert(
      DocumentUtils.jsonPrettyString(
        Map(
          "top1" -> List(
            Map("a" -> "234", "12" -> 144),
            34,
            90223423423423L,
            "sdfe"
          ),
          "top2" -> Map("leaf" -> 0)
        )
      ) ==
        """{
          |  "top1": [
          |      {
          |          "a": "234",
          |          "12": 144
          |        },
          |      34,
          |      90223423423423,
          |      "sdfe"
          |    ],
          |  "top2": {
          |      "leaf": 0
          |    }
          |}""".stripMargin
    )
  }
}
