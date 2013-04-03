package com.tyoverby.macrolisp

import util.parsing.combinator.JavaTokenParsers
import util.parsing.input.CharSequenceReader

object MyParser extends JavaTokenParsers {
  override def floatingPointNumber: Parser[String] = """-?(\d+(\.\d*)?|\d*\.\d+)([eE][+-]?\d+)?[fFdD]?""".r
  override def decimalNumber: Parser[String] = """(\d+(\.\d*)?|\d*\.\d+)""".r
  override def wholeNumber: Parser[String] = """-?\d+""".r

  def number: Parser[String] = (floatingPointNumber | decimalNumber |  wholeNumber)

  def identifier: Parser[String] = """[a-zA-Z]+""".r

  def parenGroup: Parser[List[Any]] = "(" ~> rep(exp) <~ ")"

  def exp: Parser[Any] = number | identifier | parenGroup

  def whole: Parser[Any] = phrase(rep(exp))
}

object Test extends App {
  println(MyParser.whole(new CharSequenceReader("5test")))
  println(MyParser.whole(new CharSequenceReader("(5 10 15)")))
}