package com.tyoverby.macrolisp.interp

import org.mozilla.javascript._
import com.tyoverby.macrolisp.pub.PublicProducer
import com.tyoverby.macrolisp.parsers.lisp.{Token => LispToken}
import scala.io.Source

object Interpreter extends App {
  // Compiler
  val rulesFiles = List("math", "flow", "list", "object", "set", "last").map("./src/test/resources/standard/" + _ + ".rules").map(Source.fromFile)
  var rules = PublicProducer.parseRuleSources(rulesFiles: _*)

  val context = Context.enter()
  //  val shell = new Shell() {}
  val scope = context.initStandardObjects(Globals)

  var line = 0

  while (true) {
    val i = InterpreterHelper.retrieveStatement()

    val input = if (i.startsWith("#reload")) {
      rules = PublicProducer.parseRuleSources(rulesFiles: _*)
      println("<< rules reloaded >>")
      InterpreterHelper.retrieveStatement()
    } else {
      i
    }

    try {
      val actualInput = if (input.startsWith("#generate")) input.replace("#generate", "") else input


      val program: List[LispToken]= PublicProducer.parseSourceSlurped(actualInput, "<console>")
      val compiled = PublicProducer.compile(program, rules)
      line += 1
      val result =
        if (input.startsWith("#generate")) compiled
        else context.evaluateString(scope, compiled, "<terminal>", line, null)


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

object Globals extends ScriptableObject {
  val names = Array("print")

  //  defineFunctionProperties(names, this.getClass, ScriptableObject.DONTENUM)


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

  def getClassName = "Globals"
}