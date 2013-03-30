package com.tyoverby.macrolisp.parsers

import lisp.LispTokens._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import util.parsing.input.CharSequenceReader
import language.implicitConversions

class LispParserTest extends FlatSpec with ShouldMatchers {

  import com.tyoverby.macrolisp.parsers.lisp.LispParser._

  implicit def str2reader(str: String) = new CharSequenceReader(str)

  "The Lisp Parser" should "correctly parse paren Groups" in {
    parseExpression("(test)").get should equal(ParenGroup(List(Identifier("test"))))

    parseExpression("(this (is nested))").get should equal(ParenGroup(List(Identifier("this"), ParenGroup(List(Identifier("is"), Identifier("nested"))))))
  }

  it should "correctly parse bracket groups" in {
    parseExpression("[hi]").get should equal(BracketGroup(List(Identifier("hi"))))
    parseExpression("[[]]").get should equal(BracketGroup(List(BracketGroup(List()))))
  }

  it should "correctly parse curly groups" in {
    parseExpression("{test}").get should equal(CurlyGroup(List(Identifier("test"))))
    parseExpression("{{}}").get should equal(CurlyGroup(List(CurlyGroup(List()))))
  }

  it should "correctly parse intermixed groups" in {
    parseExpression("({[inside]})").get should equal(ParenGroup(List(CurlyGroup(List(BracketGroup(List(Identifier("inside"))))))))
  }

  it should "correctly parse string literals" in {
    parseExpression("\"hi\"").get should equal(StringLiteral("\"hi\""))
    parseExpression("\"\\\"t()est\"").get should equal(StringLiteral("\"\\\"t()est\""))
  }

  it should "correctly parse identifiers" in {
    parseExpression("(* hi test)").get should equal(ParenGroup(List(Identifier("*"), Identifier("hi"), Identifier("test"))))
  }

  it should "correctly parse numbers" in {
    parseExpression("1.234").get should equal(NumberLiteral(1.234))
    parseExpression("5").get should equal(NumberLiteral(5.0))
    parseExpression("5e10").get should equal(NumberLiteral(5e10))
  }

  it should "correctly parse variables" in {
    parseExpression(":hi").get should equal(Variable(":hi"))
  }

  it should "correctly parse repeated values" in {
    parseExpression(":hi...").get should equal(Repeat(Variable(":hi")))
    parseExpression("(:xs)...").get should equal(Repeat(ParenGroup(List(Variable(":xs")))))
  }

  it should "reject malformed programs" in {
    // Unmatched paren
    parseProgram("(]").isEmpty should be(true)
    // Can't use numbers in identifiers
    parseProgram(":9").isEmpty should be(true)
    // Unclosed paren
    parseProgram("(test \"hi\"").isEmpty should be(true)
    // Unclosed paren
    parseProgram("(").isEmpty should be(true)
    // Weird number syntax
    parseProgram("1.fef").isEmpty should be(true)
    // Unclosed quote
    parseProgram("\"hi there").isEmpty should be(true)
  }
}

