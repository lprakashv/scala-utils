package com.lprakashv

import java.io.{BufferedWriter, File, FileWriter}

import scala.io.{BufferedSource, Source}

object FileUtils {
  def writerOfPath(ofPath: String): BufferedWriter = {
    new BufferedWriter(new FileWriter(new File(ofPath)))
  }

  def writeLine(line: String)
               (implicit bw: BufferedWriter): Unit = {
    bw.write(s"$line\n")
  }

  def readerOfPath(ifPath: String): BufferedSource = {
    Source.fromFile(ifPath, "UTF-8")
  }
}
