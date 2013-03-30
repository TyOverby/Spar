package com.tyoverby.macrolisp.parsers.lisp

import com.tyoverby.macrolisp.parsers.AbstractParser
import com.tyoverby.macrolisp.parsers.lisp.LispTokens._

object LispParser extends AbstractParser {


  override def ident: Parser[String] = """[^0-9()\[\]{}. #":][^()\[\]{}. ""]*""".r

  private[this] def break: Parser[String] = """[ (){}\[\]]|\z""".r

  private[this] def variable: Parser[Token] = ":[a-zA-Z]+".r ^^ Variable

  private[this] def repeat: Parser[Token] = wrappableExpressions <~ "..." ^^ Repeat

  private[this] def number: Parser[Token] = (floatingPointNumber | decimalNumber |  wholeNumber) <~ break ^^ TokenTranslations.genNumberLiteral

  private[this] def identifier: Parser[Token] = ident ^^ Identifier

  private[this] def stringLit: Parser[Token] = stringLiteral ^^ StringLiteral

  private[this] def parenGroup: Parser[Token] = "(" ~> rep(exp) <~ ")" ^^ ParenGroup

  private[this] def bracketGroup: Parser[Token] = "[" ~> rep(exp) <~ "]" ^^ BracketGroup

  private[this] def curlyGroup: Parser[Token] = "{" ~> rep(exp) <~ "}" ^^ CurlyGroup

  private[this] def wrappableExpressions: Parser[Token] = parenGroup | bracketGroup | curlyGroup | variable

//  private[this] def nonWrappedExp: Parser[Token] = number | stringLit| identifier | variable  | parenGroup | bracketGroup | curlyGroup

  private[this] def exp: Parser[Token] = repeat | number | stringLit | identifier | wrappableExpressions

  def parseExpression: Parser[Token] = exp

  def parseProgram: Parser[List[Token]] = phrase(rep1(exp))
}