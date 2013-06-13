package com.tyoverby.spar.parsers

import genjs.JSTokens.{StringLiteral => JSStringLiteral, Variable => JSVariable}
import lisp.LispTokens.{Identifier => LispIdentifier, ParenGroup => LispParenGroup, Variable => LispVariable}
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import util.parsing.input.CharSequenceReader
import language.implicitConversions

class GeneratorParserTest extends FlatSpec with Matchers {

  import com.tyoverby.spar.parsers.generator.GeneratorParser.parseRule

  implicit def str2reader(str: String) = new CharSequenceReader(str)

  "The Generator Parser" should "correctly validate rules" in {
    System.out.println(
      parseRule(
        """ (a :b :c) => {"a(" :b "," :c ")"}
          | (x :y :z) => {"hello"}
        """.stripMargin).toString)
    //      .get should equal(
    //      Rule(LispSide(LispParenGroup(List(LispIdentifier("a"), LispVariable(":b"), LispVariable(":c")))),
    //        JSSide(List(JSStringLiteral("\"a(\""), JSVariable(":b"), JSStringLiteral("\",\""), JSVariable(":c"), JSStringLiteral("\")\"")))))
  }
}
