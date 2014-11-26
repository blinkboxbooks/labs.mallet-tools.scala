package com.blinkbox.books.mallettools

import java.io.{ File, FileInputStream }

import nl.siegmann.epublib.bookprocessor.HtmlCleanerBookProcessor
import nl.siegmann.epublib.epub.EpubReader
import nl.siegmann.epublib.domain.Resource
import org.jsoup.Jsoup

class DefaultEpubReader(val epubFile: File) {
  import scala.collection.JavaConverters._

  val reader = new EpubReader
  val is = new FileInputStream(epubFile)
  val book = reader.readEpub(is)
  is.close()

  /** Get context as a single, merged String. */
  def getText: String = book.getContents.asScala.map(extractText).mkString(" ")

  private def extractText(rs: Resource): String = {
    val stripper = new HtmlCleanerBookProcessor()
    val st = new String(stripper.processHtml(rs, book, "UTF-8"))
    Jsoup.parse(st).text()
  }

}

object DefaultEpubReader {
  
  def getContent(epubFile: File) = 
    new DefaultEpubReader(epubFile).getText
  
}