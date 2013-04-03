package com.tyoverby.macrolisp.interp

import org.mozilla.javascript._
import java.io.{InputStreamReader, BufferedReader}
import com.tyoverby.macrolisp.pub.PublicProducer
import com.tyoverby.macrolisp.parsers.lisp.{Token => LispToken}

object Interpreter extends App {
  // Compiler
  val rulesFiles = List("math", "flow", "list", "object", "set", "last").map("./src/test/resources/standard/" + _ + ".rules")
  var rules = PublicProducer.parseAllRules(rulesFiles: _*)

  val context = Context.enter()
  //  val shell = new Shell() {}
  val scope = context.initStandardObjects(Globals)

  var line = 0

  while (true) {
    val i = InterpreterHelper.retrieveStatement()

    val input = if (i.startsWith("#reload")){
      rules = PublicProducer.parseAllRules(rulesFiles: _*)
      println("<< rules reloaded >>")
      InterpreterHelper.retrieveStatement()
    } else {
      i
    }

    try {
      val actualInput = if (input.startsWith("#generate")) input.replace("#generate", "") else input


      val program: (String, List[LispToken]) = PublicProducer.parseStringSource(actualInput)
      val compiled = PublicProducer.compile(program, rules)
      line += 1
      val result =
        if (input.startsWith("#generate")) compiled._2
        else context.evaluateString(scope, compiled._2, "<terminal>", line, null)


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

  def getClassName = "Globals"
}