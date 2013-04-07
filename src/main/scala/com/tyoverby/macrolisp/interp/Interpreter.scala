package com.tyoverby.macrolisp.interp

import org.mozilla.javascript._
import java.io.{InputStreamReader, BufferedReader}
import com.tyoverby.macrolisp.pub.PublicProducer
import com.tyoverby.macrolisp.parsers.lisp.{Token => LispToken}

object Interpreter extends App {
  // Compiler
  val rulesFiles = List("math", "flow", "list", "object", "set", "last").map("./src/test/resources/standard/" + _ + ".rules")
  val rules = PublicProducer.parseAllRules(rulesFiles: _*)

  val context = Context.enter()
  //  val shell = new Shell() {}
  val scope = context.initStandardObjects()

  val input = new BufferedReader(new InputStreamReader(System.in))

  var line = 0

  while (true) {
    val sb = new StringBuilder
    var seen = false

    do {
      if (!seen) print("> ") else print("... ")
      sb.append(input.readLine())
    } while (!Helper.sameParens(sb.toList))

    try {

      val program: (String, List[LispToken]) = PublicProducer.parseStringSource(sb.toString())
      val compiled = PublicProducer.compile(program, rules)
      line += 1
      val result = context.evaluateString(scope, compiled._2, "<terminal>", line, null)

      println(s"$result")
    }
    catch {
      case e: Exception => {
        Console.err.flush()
        System.out.flush()
        Console.err.println("#" + e.getMessage)
        Console.err.flush()
        System.out.flush()
      }
    }
  }
}

object Globals {
  val names = Array("print")

  def print(cx: Context, thisObj: Scriptable, args: Array[Object], funObj: Function) {
    for (i <- 0 until args.length) {
      if (i > 0)
        System.out.print(" ")

      // Convert the arbitrary JavaScript value into a string form.
      val s = Context.toString(args(i))

      System.out.print(s)
    }
    System.out.println()
  }
}

object Helper {
  def sameParens(str: List[Char]): Boolean = {
    def helper(str: List[Char], parenCount: Int, bracketCount: Int, curleyCount: Int): Boolean = {
      str match {
        case Nil => parenCount >= 0 && bracketCount >= 0 && curleyCount >= 0
        case '(' :: xs => helper(xs, parenCount + 1, bracketCount, curleyCount)
        case ')' :: xs => helper(xs, parenCount - 1, bracketCount, curleyCount)
        case '[' :: xs => helper(xs, parenCount, bracketCount + 1, curleyCount)
        case ']' :: xs => helper(xs, parenCount, bracketCount - 1, curleyCount)
        case '{' :: xs => helper(xs, parenCount, bracketCount, curleyCount + 1)
        case '}' :: xs => helper(xs, parenCount, bracketCount, curleyCount - 1)
        case _ :: xs => helper(xs, parenCount, bracketCount, curleyCount)
      }
    }
    helper(str, 0, 0, 0)
  }
}