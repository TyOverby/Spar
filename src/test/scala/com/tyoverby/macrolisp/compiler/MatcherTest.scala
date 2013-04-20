package com.tyoverby.macrolisp.compiler

import com.tyoverby.macrolisp.parsers.generator.GeneratorParser._
import com.tyoverby.macrolisp.parsers.lisp.LispParser.parseProgram
import util.parsing.input.CharSequenceReader
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.tyoverby.macrolisp.parsers.lisp.LispTokens.{NumberLiteral, ParenGroup, Identifier, StringLiteral}
import language.implicitConversions
import com.tyoverby.macrolisp.parsers.lisp.Token

class MatcherTest extends FlatSpec with ShouldMatchers {
  implicit def str2reader(str: String) = new CharSequenceReader(str)

  def getEnv(program: String, rules: String): Env[String, Token] = {
    val firstRule = parseSlurped(rules).head.get
    val parsedProg = parseProgram(program).get.head
    PatternMatcher.matchExpr(parsedProg, firstRule)
  }

  "the matcher" should "correctly evaluate A single group with identifiers" in {
    val program = """(hi arg1 arg2)"""
    val rule = """(:a :b :c) => { :a "(" :b "," :c ")" }"""
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(":a" -> Identifier("hi"), ":b" -> Identifier("arg1"), ":c" -> Identifier("arg2")), Map()))
  }

  it should "correctly evaluate nested groups with identifiers" in {
    val program = """(hi (arg1 arg2))"""
    val rule = """(:a (:b :c)) => { :a "(" :b "," :c ")}""""
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(":a" -> Identifier("hi"), ":b" -> Identifier("arg1"), ":c" -> Identifier("arg2")), Map()))
  }

  it should "correctly evaluate a repeated variable matcher" in {
    val program = """(hi x y z)"""
    val rule = """(hi :x...) => {"hi(" :x,,, ")" }"""
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(), Map(":x" -> List(Identifier("x"), Identifier("y"), Identifier("z")))))
  }

  it should "correctly evaluate a repeated value in a paren group" in {
    val program = """(cond (one two) (three four) (five six))"""
    val rule = """(cond (:a :b)...) => { "hi" }"""
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(), Map(
      ":a" -> List(Identifier("one"), Identifier("three"), Identifier("five")),
      ":b" -> List(Identifier("two"), Identifier("four"), Identifier("six")))))
  }

  it should "correctly evaluate repeated values in a paren group with a nested group" in {
    val program = """(cond (one two) (three (println "hi")) (five six))"""
    val rule = """(cond (:a :b)...) => { "hi" }"""
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(), Map(
      ":a" -> List(Identifier("one"), Identifier("three"), Identifier("five")),
      ":b" -> List(Identifier("two"), ParenGroup(List(Identifier("println"), StringLiteral("\"hi\""))), Identifier("six")))))
  }

  it should "correctly evaluate two values after a repeater" in {
    val program = """(test 1 2 3 4 5)"""
    val rule = """(test :a... :b :c) => { "hi" }"""
    val testenv = getEnv(program, rule)
    testenv should equal(Env(
      Map(":b" -> NumberLiteral(4.0), ":c" -> NumberLiteral(5.0)),
      Map(":a" -> List(NumberLiteral(1.0), NumberLiteral(2.0), NumberLiteral(3.0)))))
  }

  it should "correctly evaluate two groups after a repeater" in {
    val program = """(test (1 a) (2 b) (3 c) (4 d) (5 e))"""
    val rule = """(test (:nx :lx)... (:b :c)) => { "hi" }"""
    val testenv = getEnv(program, rule)
    testenv should equal(Env(
      Map(":b" -> NumberLiteral(5.0), ":c" -> Identifier("e")),
      Map(
        ":nx" -> List(NumberLiteral(1.0), NumberLiteral(2.0), NumberLiteral(3.0), NumberLiteral(4.0)),
        ":lx" -> List(Identifier("a"), Identifier("b"), Identifier("c"), Identifier("d")))))
  }
}
