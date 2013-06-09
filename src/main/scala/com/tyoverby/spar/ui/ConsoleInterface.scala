package com.tyoverby.spar.ui

import java.io.{BufferedWriter, FileWriter, File}

object ConsoleInterface {
  def printUsage() {

    val usage =
      """
        | Usage: lispparser rules-dir rules-pattern src-dir src-pattern out-dir out-extension
      """.stripMargin
    print(usage)
  }

  def main(args: Array[String]) {

    if (args.length != 3) {
      println("Bad args!")
      sys.exit(1)
    }
    val rule = new File(args(0))
    val src = new File(args(1))
    val dst = new File(args(2))

    val cRule = com.tyoverby.spar.pub.PublicProducer.parseRuleFiles(rule)
    val cProg = com.tyoverby.spar.pub.PublicProducer.compileFileRules(src, cRule)

    dst.getParentFile.mkdirs()

    val fstream = new FileWriter(dst)
    val out = new BufferedWriter(fstream)
    out.write(cProg)
    out.close()

    //    val arguments: Array[String] =
    //      if (args.length == 6) {
    //        args
    //      } else if (args.length == 0) {
    //        val lines = Source.fromFile("./lispparser.conf").getLines().foldLeft("")(_ + " " + _)
    //        lines.split("\\s+")
    //      }
    //      else {
    //        println("")
    //        sys.exit(1)
    //      }
    //
    //
    //
    //
    //    val rulesDir = new File(arguments(0))
    //    val rulesPatt = arguments(1)
    //
    //    val srcDir = new File(arguments(2))
    //    val srcPatt = arguments(3)
    //
    //    val outDir = new File(arguments(4))
    //    val outExt = new File(arguments(5))
    //
    //    val allRules = CompileAll.allFiles(rulesPatt)(rulesDir)
    //    val allSource = CompileAll.allFiles(srcPatt)(srcDir)
    //
    ////    val compiledRules = com.tyoverby.spar.pub.PublicProducer.compileR
  }
}
