package com.tyoverby.spar.watcher

import java.io.File
import java.nio.file.{Path, Paths}
import java.nio.file.StandardWatchEventKinds._
import scala.collection.JavaConversions._


case class DirectoryWatcher(dirPath: File, regex: String,
                            exists: File => Any,
                            create: File => Any,
                            modify: File => Any,
                            delete: File => Any) extends Runnable {

  var running = false
  val root = dirPath
  val compRegex = regex.r

  def getAllFrom(dir: File):List[File] = {
    val (files, folders) = dir.listFiles().toList.partition(_.isFile)
    val matchingFiles = files.filter(n => compRegex.findFirstIn(n.toString).isDefined)

    matchingFiles ++ folders.map(getAllFrom).flatten
  }

  val watcher = root.toPath.getFileSystem.newWatchService()

  def registerFrom(dir: File) {
    dir.toPath.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
    if (dir.isDirectory) {
      dir.listFiles().filter(_.isDirectory).foreach(registerFrom)
    }
  }

  registerFrom(root)

  def allFilesFromRoot() = {
    getAllFrom(root)
  }
  override def run() {

    running = true
    while (running) {
      val next = watcher.take()
      val events = next.pollEvents()
      for (e <- events) {
        val eventPath = e.context().asInstanceOf[Path].toFile

        e.kind match {
          case ENTRY_CREATE => create(eventPath)
          case ENTRY_MODIFY => modify(eventPath)
          case ENTRY_DELETE => delete(eventPath)
        }
      }
    }
  }
}
