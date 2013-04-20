package com.tyoverby.macrolisp.parsers.genjs

import JSTokens._
import scala.util.parsing.combinator.JavaTokenParsers

object GenJsParser extends JavaTokenParsers {
  private[this] def stringLit: Parser[Token] = stringLiteral ^^ genStringLiteral

  private[this] def variable: Parser[Token] = ":[a-zA-Z]+".r ^^ Variable

  private[this] def group: Parser[Token] = "(" ~> rep(innerExp) <~ ")" ^^ Group

  private[this] def repeat: Parser[Token] = nonWrappingExp <~ "..." ^^ Repeat

  private[this] def commaRepeat: Parser[Token] = nonWrappingExp <~ ",,," ^^ CommaRepeat

  private[this] def nonWrappingExp: Parser[Token] = variable | group

  private[this] def innerExp: Parser[Token] = repeat | commaRepeat | nonWrappingExp | stringLit

  def exp: Parser[List[Token]] = "{" ~> rep1(innerExp) <~ "}"

  def parseProgram: Parser[List[Token]] = phrase(exp)
}