package com.tyoverby.macrolisp.pub

import java.io.{BufferedWriter, FileWriter, File}

object CompileAll {

  def writeToFile(source: String, fileName: String) {
    val f = new File(fileName)
    f.getParentFile.mkdirs()
    val fstream = new FileWriter(f)
    val out = new BufferedWriter(fstream)
    out.write(source)
    out.close()
  }

  def allFiles(pattern: String)(dir: File): List[File] = {
    val (files, folders) = List(dir.listFiles():_*).toList.partition(_.isFile)
    val correctFiles = files.filter(_.toString.matches("^.*"+pattern+"$"))

    correctFiles ++ folders.map(allFiles(pattern)).flatten
  }

  def compileAll(rulesDir: File, rulesPattern: String,
                 srcDir: File, srcPattern: String,
                 outDir: File, newExt: String) {

    val allRules = allFiles(rulesPattern)(rulesDir)
    val allSrc = allFiles(srcPattern)(srcDir)

    val parsedRules = PublicProducer.parseAllRules(allRules.map(_.toString): _*)

    allSrc.foreach(f => {
      println(s"compiling $f...")
      val (_, produced) = PublicProducer.compileFile(f, parsedRules)

      val srcdir = srcDir.getAbsoluteFile.toString
      val file = f.getAbsoluteFile.toString
      val dstdir = outDir.getAbsoluteFile.toString

      val sub = file.replaceAllLiterally(srcdir, "")
      val complete = (dstdir + sub).replace("\\\\..+$", "." + newExt)
      println(s"  writing $complete")
      writeToFile(produced, complete)
    })
  }


  def main(args: Array[String]) {
    if (args.length != 6) {
      printHelp()
      System.exit(1)
    }
    val rulesDir: File = new File(args(0))
    val rulesPattern: String = args(1)
    val srcDir: File = new File(args(2))
    val srcPattern: String = args(3)
    val outDir: File = new File(args(4))
    val newExt: String = args(5)

    compileAll(rulesDir, rulesPattern, srcDir, srcPattern, outDir, newExt)
  }

  def printHelp() {
    println("Usage: ")
    println("   java -jar LispTransformer.jar   rules-dir rules-pattern" +
      "   src-dir src-pattern  out-dir new-extension")
  }
}
