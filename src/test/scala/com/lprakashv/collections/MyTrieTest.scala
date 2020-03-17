package com.lprakashv.collections

import com.lprakashv.files.FileUtils
import org.scalatest.FunSpec

class MyTrieTest extends FunSpec {
  describe("A MyTrie with test data") {
    describe("when empty") {
      val trie = new MyTrie[Int]

      it("should have sortedMap(\"\").size == 0") {
        assert(trie.sortedMap("").isEmpty)
      }

      it("should have get(\"\") == None") {
        assert(trie.get("").isEmpty)
      }
    }

    describe("when inserted lalit") {
      val trie = new MyTrie[Int]

      trie.insert("lalit", 7)

      it("should get(\"lalit\") == Some(7)") {
        assert(trie.get("lalit").contains(7))
      }

      it("should have sortedMap(\"la\").size == 1") {
        assert(trie.sortedMap("la").size == 1)
      }

      it("should have sortedMap(\"li\").size == 0") {
        assert(trie.sortedMap("li").isEmpty)
      }
    }
  }

  describe(
    "when loaded 4 files in test resources: " +
      "lorem_ipsum0.txt, lorem_ipsum1.txt, lorem_ipsum2.txt, lorem_ipsum3.txt"
  ) {
    val trie = new MyTrie[Int]

    for {
      i <- 0 to 3

      cls = getClass.getClassLoader
      rs = cls.getResource(s"./lorem_ipsum$i.txt")
      filePath = rs.getFile

      _ = alert("FP - " + filePath)

      line <- FileUtils
        .readerOfPath(filePath)
        .getLines()
      word <- "[A-Za-z0-9]+".r
        .findAllIn(line)
    } yield { trie.insert(word, 1, _ + 1) }

    it(
      "should have top words with prefix='as' == List(assueverit[9],assum[7],assentior[4])"
    ) {
      assert(
        trie.sortedMap("as") == List(
          ("assueverit", 9),
          ("assum", 7),
          ("assentior", 4)
        )
      )
    }

    it(
      "should have top words with prefix='pl' == List(placerat[27],platea[7],platonem[5])"
    ) {
      assert(
        trie.sortedMap("pl") == List(
          ("placerat", 27),
          ("platea", 7),
          ("platonem", 5)
        )
      )
    }

    it("should get(\"assum\") == 7") {
      assert(trie.get("assum").contains(7))
    }
  }
}
