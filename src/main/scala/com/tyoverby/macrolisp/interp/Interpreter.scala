package com.tyoverby.macrolisp.interp

import java.io.PrintStream

object Interpreter extends App {
  val node = Runtime.getRuntime.exec("nodejs")
  System.setOut(new PrintStream(node.getOutputStream))
}
