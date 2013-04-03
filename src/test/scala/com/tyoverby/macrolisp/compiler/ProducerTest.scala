package com.tyoverby.macrolisp.compiler

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.tyoverby.macrolisp.parsers.lisp.{Token => LispToken}
import com.tyoverby.macrolisp.parsers.lisp.LispTokens.{StringLiteral, Identifier}
import com.tyoverby.macrolisp.parsers.genjs.JSTokens.{StringLiteral => JSString, Variable => JSVariable, Repeat => JSRepeat, Group => JSGroup}
import com.tyoverby.macrolisp.parsers.genjs.{Token => JSToken}


class ProducerTest extends FlatSpec with ShouldMatchers {
  "walkInner" should "consume input from a list env and produce output" in {
    val env = Env[String, LispToken](Map(), Map(":a" -> List(Identifier("x"), Identifier("y"), Identifier("z"))))
    val jsSide = JSGroup(List(JSVariable(":a"), JSString("\"hi\"")))

    Producer.walkInner(env, jsSide)(null) should equal(List("xhi", "yhi", "zhi"))
  }

  it should "consume input from a single variable as well" in {
    val env = Env[String, LispToken](Map(":b"->Identifier("b")), Map(":a" -> List(Identifier("x"), Identifier("y"), Identifier("z"))))
    val jsSide = JSGroup(List(JSVariable(":b"), JSVariable(":a"), JSString("\"hi\"")))

    Producer.walkInner(env, jsSide)(null) should equal(List("bxhi", "byhi", "bzhi"))
  }

  it should "catch a consuming only singles error though" in {
    val env = Env[String, LispToken](Map(":b" -> Identifier("a")), Map())
    val jsSide = JSGroup(List(JSVariable(":b")))

    evaluating {
      Producer.walkInner(env, jsSide)(null)
    } should produce[ConsumingOnlySingles]
  }

  "walkOuter" should "produce viable results from non-nested" in {
    val env = Env[String,LispToken](Map(":a"->StringLiteral("\" hello there\"")), Map())
    val jsSide:List[JSToken] = List(JSString("\"hi\""), JSVariable(":a"))

    Producer.walkOuter(env,jsSide)(null) should equal("hi\" hello there\"")
  }

  it should "delegate nested expressions to walkInner" in {
    val env = Env[String, LispToken](Map(":a"->Identifier("a")), Map(":b"->List(Identifier("x"), Identifier("y"), Identifier("z"))))
    val jsSide: List[JSToken] = List(JSVariable(":a"), JSRepeat(JSGroup(List(JSVariable(":a"), JSVariable(":b")))))

    Producer.walkOuter(env,jsSide)(null) should equal("aaxayaz")
  }
}
