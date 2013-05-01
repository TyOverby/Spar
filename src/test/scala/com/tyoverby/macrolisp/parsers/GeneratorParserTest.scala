package com.tyoverby.macrolisp.parsers

import generator.{JSSide, Rule, LispSide}
import genjs.JSTokens.{StringLiteral=>JSStringLiteral, Variable=>JSVariable}
import lisp.LispTokens.{Identifier => LispIdentifier, ParenGroup => LispParenGroup, Variable=>LispVariable}
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import util.parsing.input.CharSequenceReader
import language.implicitConversions

class GeneratorParserTest extends FlatSpec with ShouldMatchers {

  import com.tyoverby.macrolisp.parsers.generator.GeneratorParser.parseRule

  implicit def str2reader(str: String) = new CharSequenceReader(str)

  "The Generator Parser" should "correctly validate rules" in {
    parseRule( """ (a :b :c) => {"a(" :b "," :c ")"} """).get should equal(
      Rule(LispSide(LispParenGroup(List(LispIdentifier("a"), LispVariable(":b"), LispVariable(":c")))),
           JSSide(List(JSStringLiteral("\"a(\""),JSVariable(":b"), JSStringLiteral("\",\""), JSVariable(":c"), JSStringLiteral("\")\"")))))
  }
}
