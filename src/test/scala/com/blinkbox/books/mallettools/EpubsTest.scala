package com.blinkbox.books.mallettools

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EpubsTest extends FlatSpec with MockitoSugar {

  "epub file name extractor" should "get ISBN from epub file name" in {
    assert(Epubs.isbnFromFilename("./1234567890123.epub") == "1234567890123")
    assert(Epubs.isbnFromFilename("/mnt/books/foo/1234567890123.epub.c2df03e") == "1234567890123")
  }

  it should "ignore files with non-13-digit ISBN" in {
    // This is called from Java, OK??
    assert(Epubs.isbnFromFilename("/mnt/books/foo/123456789012.epub.c2df03e") == null)
    assert(Epubs.isbnFromFilename("/mnt/books/foo/12345678901234.epub.c2df03e") == null)
  }

  it should "ignore non-epub files" in {
    assert(Epubs.isbnFromFilename("/mnt/books/foo/1234567890123.txt") == null)
    assert(Epubs.isbnFromFilename("/mnt/books/foo/1234567890123.txt.c2df03e") == null)
  }

}
