package com.blinkbox.books.mallettools

import cc.mallet.types.Instance
import java.io.File
import scala.collection.JavaConverters._
import scala.util.Try

object Epubs {

  /**
   * Get iterator over content of epubs, converted to Mallet Instances.
   */
  def bookIterator(rootDir: File, limit: Int): java.util.Iterator[Instance] = {
    println(s"Scanning for books in $rootDir")
    val files =
      //      List(new File("/mnt/books/Penguin_UK/ebooks/content/9780141912530.epub.ceedb23")).toStream
      getEpubFiles(rootDir)
    val shuffledFiles = util.Random.shuffle(files)

    shuffledFiles
      .take(limit)
      .map(file => Try((file, DefaultEpubReader.getContent(file))))
      .filter(_.isSuccess)
      .map(_.get)
      .map { case (file, content) => new Instance(content, null, file.getPath, null) }
      .map(instance => { println(s"Read epub file ${instance.getName}"); instance })
      .toIterator
      .asJava
  }

  def getTopCategories(probabilities: Array[Double], limit: Int, cutoff: Double): java.util.List[java.lang.Integer] = {
    val withIndex = probabilities.zipWithIndex
    val sorted = withIndex.sortWith(_._1 > _._1)
    sorted
      .take(limit)
      .takeWhile(_._1 > cutoff)
      .map { _._2 }
      .toList
      .map(java.lang.Integer.valueOf)
      .asJava
  }

  def formatTopics(isbn: String, topCategories: java.util.List[Integer]): String = s"$isbn,${topCategories.asScala.mkString(",")}"

  val EpubWithIsbn = """.*/(\d{13})\.epub.*"""r
  def isbnFromFilename(filename: String): String = filename match {
    case EpubWithIsbn(isbn: String) => isbn
    case _                          => null
  }

  def getEpubFiles(rootDir: File): Stream[File] =
    getFileTree(rootDir).filter(_.getName.contains("epub"))

  private def getFileTree(f: File): Stream[File] =
    f #::
      (if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
      else Stream.empty)

}
