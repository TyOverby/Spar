package com.tyoverby.macrolisp.parsers.genjs

import JSTokens._
import util.parsing.input.CharSequenceReader
import com.tyoverby.macrolisp.parsers.AbstractParser

object GenJsParser extends AbstractParser {
  private[this] def stringLit: Parser[Token] = stringLiteral ^^ StringLiteral

  private[this] def variable: Parser[Token] = ":[a-zA-Z]+".r ^^ Variable

  private[this] def group: Parser[Token] = "(" ~> rep(exp) <~ ")" ^^ Group

  private[this] def repeat: Parser[Token] = nonWrappingExp <~ "..." ^^ Repeat

  private[this] def commaRepeat: Parser[Token] = nonWrappingExp <~ ",,," ^^ CommaRepeat

  private[this] def nonWrappingExp: Parser[Token] = variable | group

  def exp: Parser[Token] = repeat | commaRepeat | nonWrappingExp | stringLit

  def parseProgram: Parser[List[Token]] = phrase(rep1(exp))
}