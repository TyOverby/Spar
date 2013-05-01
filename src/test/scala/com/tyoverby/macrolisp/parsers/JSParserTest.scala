package com.tyoverby.macrolisp.parsers

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import util.parsing.input.CharSequenceReader
import language.implicitConversions
import com.tyoverby.macrolisp.parsers.genjs.JSTokens._

class JSParserTest extends FlatSpec with ShouldMatchers {

  import com.tyoverby.macrolisp.parsers.genjs.GenJsParser.parseProgram

  implicit def str2reader(str: String) = new CharSequenceReader(str)

  "The Javascript Gen. Parser" should "correctly parse groups" in {
    parseProgram("{(\"hi\")}").get should equal(List(Group(List(StringLiteral("\"hi\"")))))
    parseProgram("{(\"hi\" :xs)}").get should equal(List(Group(List(StringLiteral("\"hi\""), Variable(":xs")))))
  }

  it should "correctly parse single variables" in {
    parseProgram("{:x}").get should equal(List(Variable(":x")))
    parseProgram("{\"0\"}").get should equal(List(StringLiteral("\"0\"")))
  }

  it should "correctly parse ... repeaters" in {
    parseProgram("{:xs...}").get should equal(List(Repeat(Variable(":xs"))))
    parseProgram("{(:xs :ys)...}").get should equal(List(Repeat(Group(List(Variable(":xs"), Variable(":ys"))))))
  }

  it should "correctly parse ,,, repeaters" in {
    parseProgram("{:xs,,,}").get should equal(List(CommaRepeat(Variable(":xs"))))
    parseProgram("{(:xs :ys),,,}").get should equal(List(CommaRepeat(Group(List(Variable(":xs"), Variable(":ys"))))))
  }

  it should "correctly parse string literals" in {
    parseProgram("{\"hi\"}").get should equal(List(StringLiteral("\"hi\"")))
    parseProgram("{(\"hi\" \"five\")}").get should equal(List(Group(List(StringLiteral("\"hi\""), StringLiteral("\"five\"")))))
  }

  it should "correctly parse grouped repeated variables" in {
    parseProgram("""{ "((" :x ")" ( "+(" :xs ")" )... ")" }""").get should equal(List(StringLiteral("\"((\""), Variable(":x"),
      StringLiteral("\")\""), Repeat(Group(List(StringLiteral("\"+(\""), Variable(":xs"), StringLiteral("\")\"")))), StringLiteral("\")\"")))
  }

  it should "reject malformed programs" in {
    parseProgram("{(}").isEmpty should be(true)
    parseProgram("{\"hi}").isEmpty should be(true)
    parseProgram("{xs}").isEmpty should be(true)
    parseProgram("{:xs..}").isEmpty should be(true)
    parseProgram("{(:valid)").isEmpty should be(true)
  }
}
