package com.tyoverby.macrolisp.compiler

import com.tyoverby.macrolisp.parsers.generator.GeneratorParser._
import com.tyoverby.macrolisp.parsers.lisp.LispParser.parseProgram
import util.parsing.input.CharSequenceReader
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.tyoverby.macrolisp.parsers.lisp.LispTokens.{ParenGroup, Identifier, StringLiteral}
import language.implicitConversions
import com.tyoverby.macrolisp.parsers.lisp.Token

class MatcherTest extends FlatSpec with ShouldMatchers {
  implicit def str2reader(str: String) = new CharSequenceReader(str)

  def getEnv(program: String, rules: String): Env[String, Token] = {
    val firstRule = parseSlurped(rules).head.get
    val parsedProg = parseProgram(program).get.head
    PatternMatcher.matchExpr(parsedProg, firstRule)
  }

  "A single group with identifiers" should "produce the correct environment" in {
    val program = """(hi arg1 arg2)"""
    val rule = """(:a :b :c) => :a "(" :b "," :c ")""""
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(":a" -> Identifier("hi"), ":b" -> Identifier("arg1"), ":c" -> Identifier("arg2")), Map()))
  }

  "nested groups with identifiers" should "produce the correct environment" in {
    val program = """(hi (arg1 arg2))"""
    val rule = """(:a (:b :c)) => :a "(" :b "," :c ")""""
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(":a" -> Identifier("hi"), ":b" -> Identifier("arg1"), ":c" -> Identifier("arg2")), Map()))
  }

  "a repeated variable matcher" should "produce the correct environment" in {
    val program = """(hi x y z)"""
    val rule = """(hi :x...) => "hi(" :x,,, ")" """
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(), Map(":x" -> List(Identifier("x"), Identifier("y"), Identifier("z")))))
  }

  "a repeated value in a paren group" should "produce the correct environment" in {
    val program = """(cond (one two) (three four) (five six))"""
    val rule = """(cond (:a :b)...) => "hi" """
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(), Map(
      ":a" -> List(Identifier("one"), Identifier("three"), Identifier("five")),
      ":b" -> List(Identifier("two"), Identifier("four"), Identifier("six")))))
  }

  "repeated values in a paren group with a nested group" should "produce the correct environment" in {
    val program = """(cond (one two) (three (println "hi")) (five six))"""
    val rule = """(cond (:a :b)...) => "hi" """
    val testenv = getEnv(program, rule)
    testenv should equal(Env(Map(), Map(
      ":a" -> List(Identifier("one"), Identifier("three"), Identifier("five")),
      ":b" -> List(Identifier("two"), ParenGroup(List(Identifier("println"), StringLiteral("\"hi\""))), Identifier("six")))))
  }
}
