package com.blinkbox.books.mallettools

import cc.mallet.types.IDSorter
import cc.mallet.types.Instance
import java.io.File
import scala.collection.JavaConverters._
import scala.util.Try

object Epubs {

  case class EpubFile(val file: File, val isbn: String)

  /**
   * Get iterator over content of epubs, converted to Mallet Instances.
   */
  def bookIterator(rootDir: File, limit: Int, knownIsbns: java.util.Set[String]): java.util.Iterator[Instance] = {
    println(s"Scanning for books in $rootDir")
    val epubs =
      // List(new File("/mnt/books/Penguin_UK/ebooks/content/9780141912530.epub.ceedb23")).toStream
      epubFiles(rootDir)
        .filter(epubFile => knownIsbns.contains(epubFile.isbn))
    val shuffledEpubs = util.Random.shuffle(epubs)

    shuffledEpubs
      .take(limit)
      .map(epub => Try((epub, sampled(DefaultEpubReader.getContent(epub.file)))))
      .filter(_.isSuccess)
      .map(_.get)
      .map { case (epub, content) => new Instance(content, null, epub.file.getPath, null) }
      .map(instance => { println(s"Read epub file ${instance.getName}"); instance })
      .toIterator
      .asJava
  }

  /** Get a representative sample of book: drop first 5%, and sample ever 10th word thereafter. */
  def sampled(content: String): String =
    content
      .drop(content.length / 20) // Skip first 5%
      .dropWhile(ch => ch != ' ') // Drop first word, so we don't start with a chopped-up word.
      .split("\\s") // Split into words
      .filter(!_.isEmpty)
      .zipWithIndex // Get position of word
      .filter(item => item._2 % 5 == 0) // Keep every fifth word only
      .map(_._1) // Turn back into words.
      .mkString(" ") // Skip first remaining word, to ensure we don't start with a chopped-up word.

  def isbns(file: java.io.File): java.util.Set[String] =
    scala.io.Source.fromFile(file.getAbsolutePath).getLines.toSet.asJava

  def getTopCategories(probabilities: Array[Double], limit: Int, cutoff: Double): java.util.List[IDSorter] = {
    val withIndex = probabilities.zipWithIndex
    val sorted = withIndex.sortWith(_._1 > _._1)
    sorted
      .take(limit)
      .takeWhile(_._1 > cutoff)
      //.map { _._2 }
      .toList
      .map { case (prob, topicId) => new IDSorter(topicId, prob) } //java.lang.Integer.valueOf)
      .asJava
  }

  def formatTopics(isbn: String, topCategories: java.util.List[IDSorter]): String = 
    s"$isbn\t${topCategories.asScala.map(_.getID).mkString(",")}"

  def topicDetails(topCategories: java.util.List[IDSorter]): String = 
    topCategories.asScala.map(t => s"${t.getID}: ${t.getWeight}").mkString(", ")

  def isbnFromFilename(filename: String): String = filename match {
    case EpubWithIsbn(isbn: String) => isbn
    case _                          => null
  }
  private val EpubWithIsbn = """.*/(\d{13})\.epub.*"""r

  def getEpubFiles(rootDir: File): Stream[File] =
    getFileTree(rootDir).filter(_.getName.contains("epub"))

  def epubFiles(rootDir: File): Stream[EpubFile] =
    getEpubFiles(rootDir)
      .map(f => EpubFile(f, isbnFromFilename(f.getAbsolutePath)))

  private def getFileTree(f: File): Stream[File] =
    f #::
      (if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
      else Stream.empty)

}
