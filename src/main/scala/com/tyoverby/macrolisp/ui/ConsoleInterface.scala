package com.tyoverby.macrolisp.ui

import java.io.File
import com.tyoverby.macrolisp.pub.CompileAll
import scala.io.Source

object ConsoleInterface {
  def printUsage() {

    val usage =
      """
        | Usage: lispparser rules-dir rules-pattern src-dir src-pattern out-dir out-extension
      """.stripMargin
    print(usage)
  }

  def main(args: Array[String]) {
    val arguments: Array[String] =
      if (args.length == 6) {
        args
      } else if (args.length == 0) {
        val lines = Source.fromFile("./lispparser.conf").getLines().foldLeft("")(_ + " " + _)
        lines.split("\\s+")
      }
      else {
        println("")
        sys.exit(1)
      }




    val rulesDir = new File(arguments(0))
    val rulesPatt = arguments(1)

    val srcDir = new File(arguments(2))
    val srcPatt = arguments(3)

    val outDir = new File(arguments(4))
    val outExt = new File(arguments(5))

    val allRules = CompileAll.allFiles(rulesPatt)(rulesDir)
    val allSource = CompileAll.allFiles(srcPatt)(srcDir)

//    val compiledRules = com.tyoverby.macrolisp.pub.PublicProducer.compileR
  }
}
