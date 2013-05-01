package com.tyoverby.macrolisp.watcher

import collection.mutable
import collection.JavaConversions._
import java.io.{Closeable, BufferedWriter, FileWriter}
import com.tyoverby.macrolisp.parsers.generator.Rule
import com.tyoverby.macrolisp.pub.PublicProducer
import java.util
import java.io.File

/**
 * User: Ty
 * Date: 4/12/13
 * Time: 2:00 PM
 */
class AutoCompiler(rulesDirectory: File, rulesRegex: String,
                   sourceDirectory: File, sourceRegex: String,
                   outputDirectory: File, newExt: String) extends Runnable with Closeable {
  val soeq = new EventQueue
  val req = new EventQueue

  val rulesWatcher = DirectoryWatcher(rulesDirectory, rulesRegex, req.enqueueExists, req.enqueueCreate, req.enqueueUpdate, req.enqueueDelete)
  val sourceWatcher = DirectoryWatcher(sourceDirectory, sourceRegex, soeq.enqueueExists, soeq.enqueueCreate, soeq.enqueueUpdate, soeq.enqueueDelete)

  val fileToRuleMap = mutable.Map[File, List[Rule]]()
  val listOfSources = new util.ArrayList[File]

  var running = false

  def run() {
    running = true
    while (running) {
      var rulesUpdated = false
      val updatedFiles = new util.ArrayList[File]()

      while (req.notEmpty) {
        req.dequeue() match {
          case (file, Delete) => deleteRule(file)
          case (file, _) => insertRule(file)
        }

        rulesUpdated = true
      }

      while (soeq.notEmpty) {
        soeq.dequeue() match {
          case (file, Delete) => listOfSources.remove(file)
          case (file, Create) => listOfSources.add(file)
          case (file, Exists) => listOfSources.add(file)
          case (file, Update) => {
            updatedFiles.add(file)
          }
        }

        def compileAll(files: List[File], rules: List[Rule]) {
          files.par.foreach {
            f => {
              println("compiling " + f.getName)
              compile(f, rules)
            }
          }
        }

        lazy val rules = fileToRuleMap.toList.sortBy(f => f._1.toString).map(_._2).flatten

        if (rulesUpdated) {
          compileAll(listOfSources.toList, rules)
        } else if (!updatedFiles.isEmpty) {
          compileAll(updatedFiles.toList, rules)
        }

        Thread.sleep(100)
      }
    }
  }

  def close() {
    running = false
  }

  def insertRule(file: File) {
    fileToRuleMap += (file -> PublicProducer.parseRule(file))
  }

  def deleteRule(file: File) {
    fileToRuleMap.remove(file)
  }

  private[this] def compile(sourceFile: File, rules: List[Rule]) {
    val contents = PublicProducer.compileFileRules(sourceFile, rules)

    val srcdir = sourceDirectory.getAbsoluteFile.toString
    val file = sourceFile.getAbsoluteFile.toString
    val dstdir = outputDirectory.getAbsoluteFile.toString

    val sub = file.replaceAllLiterally(srcdir, "")
    val complete = dstdir + sub

    println(s"srcdir $srcdir")
    println(s"file $file")
    println(s"dstdir $dstdir")

    println()
    println(s"sub $sub")
    println(s"compl $complete")


    println(s"file-srcdir $sub")

    writeToFile(contents, complete)
  }

  def writeToFile(source: String, fileName: String) {
    val f = new File(fileName)
    f.getParentFile.mkdirs()
    val fstream = new FileWriter(f)
    val out = new BufferedWriter(fstream)
    out.write(source)
    out.close()
  }

}
