package com.tyoverby.macrolisp.watcher

import java.io.File

object AutocompTest extends App {
  val autoCompiler = {
    val base = "./src/test/resources/autocomptest/"
    val src = new File(base + "srcdir/")
    val rules = new File(base + "rulesdir")
    val out = new File(base + "outdir")

    new AutoCompiler(
      rules, ".*rules$",
      src, ".*prog$",
      out, ".comp")
  }

  new Thread(autoCompiler).start()
}
