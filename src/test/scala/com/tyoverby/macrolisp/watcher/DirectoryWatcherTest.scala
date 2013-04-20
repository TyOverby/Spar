package com.tyoverby.macrolisp.watcher

import java.io.File

/**
 * User: Ty
 * Date: 4/12/13
 * Time: 1:38 PM
 */
object DirectoryWatcherTest extends App {
  def fileEvent(message: String)(file: File) {
    println(file + " " + message)
  }

  val exists = fileEvent("exists.") _
  val create = fileEvent("was created.") _
  val modify = fileEvent("was modified.") _
  val delete = fileEvent("was deleted.") _

  val watcher = DirectoryWatcher(new File("./src/test/resources"), "rules", exists, create, modify, delete)

  new Thread(watcher).run()
}