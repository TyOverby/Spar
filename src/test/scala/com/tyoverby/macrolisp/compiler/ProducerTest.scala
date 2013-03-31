package com.tyoverby.macrolisp.compiler

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.tyoverby.macrolisp.parsers.lisp.{Token => LispToken}
import com.tyoverby.macrolisp.parsers.lisp.LispTokens.Identifier
import com.tyoverby.macrolisp.parsers.genjs.JSTokens.{StringLiteral => JSString, Variable => JSVariable, Repeat => JSRepeat, Group => JSGroup}


class ProducerTest extends FlatSpec with ShouldMatchers {
  "walkInner" should "consume input from a list env and produce output" in {
    val env = Env[String, LispToken](Map(), Map(":a" -> List(Identifier("x"), Identifier("y"), Identifier("z"))))
    val jsSide = JSGroup(List(JSVariable(":a"), JSString("\"hi\"")))

    Producer.walkInner(env, jsSide)(null) should equal (List("x hi ", "y hi ", "z hi "))
  }

  it should "consume input from a single variable as well" in {

  }

  it should "catch a consuming only singles error though" in {

  }
}
