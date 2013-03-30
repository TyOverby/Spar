package com.tyoverby.macrolisp.compiler

import com.tyoverby.macrolisp.parsers.generator.GeneratorParser._
import com.tyoverby.macrolisp.parsers.lisp.LispParser.parseProgram
import util.parsing.input.CharSequenceReader
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.tyoverby.macrolisp.parsers.lisp.LispTokens.Identifier
import language.implicitConversions
import com.tyoverby.macrolisp.parsers.lisp.Token

class CompilerTest extends FlatSpec with ShouldMatchers {
  implicit def str2reader(str: String) = new CharSequenceReader(str)

  def getEnv(program: String, rules: String): Env[String, Token] = {
    val firstRule = parseSlurped(rules).head.get
    val parsedProg = parseProgram(program).get.head
    PatternMatcher.matchExpr(parsedProg, firstRule)
  }

  "A single group with identifiers" should "produce the correct environment" in {
    val program =  """(hi arg1 arg2)"""
    val rule = """(:a :b :c) => :a "(" :b "," :c ")""""
    val testenv = getEnv(program, rule)
    testenv should equal (Env(Map(":a" -> Identifier("hi"), ":b" -> Identifier("arg1"), ":c" -> Identifier("arg2")), Map()))
  }

  "nested groups with identifiers" should "produce the correct environment" in {
    val program =  """(hi (arg1 arg2))"""
    val rule = """(:a (:b :c)) => :a "(" :b "," :c ")""""
    val testenv = getEnv(program, rule)
    testenv should equal (Env(Map(":a" -> Identifier("hi"), ":b" -> Identifier("arg1"), ":c" -> Identifier("arg2")), Map()))
  }

  "a repeated variable matcher" should "produce the correct environment" in {
    val program = """(hi x y z)"""
    val rule = """(hi :x...) => "hi(" :x,,, ")" """
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(), Map(":x"->List(Identifier("x"), Identifier("y"), Identifier("z")))))
  }

  "a repeated value in a paren group" should "produce the correct environment" in {
    val program = """(cond (one two) (three four) (five six))"""
    val rule = """(cond (:a :b)...) => "hi" """
    val testenv = getEnv(program,rule)
    testenv should equal (Env(Map(), Map(":a"->List(Identifier("five"),Identifier("three"),Identifier("one")), ":b"-> List(Identifier("six"),Identifier("four"),Identifier("two")))))
  }

}
