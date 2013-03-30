package com.tyoverby.macrolisp.parsers.lisp

sealed trait Token

object LispTokens {

  case class Identifier(ident: String) extends Token

  case class Variable(str: String) extends Token

  case class StringLiteral(str: String) extends Token

  case class NumberLiteral(num: Double) extends Token

  case class RegexLiteral(reg: String) extends Token

  trait Group extends Token{
    val lst: List[Token]
  }

  case class ParenGroup(lst: List[Token]) extends Group

  case class BracketGroup(lst: List[Token]) extends Group

  case class CurlyGroup(lst: List[Token]) extends Group

  case class Repeat(tok: Token) extends Token

}


object TokenTranslations {
  def genNumberLiteral(s: String): LispTokens.NumberLiteral = {
    LispTokens.NumberLiteral(s.toDouble)
  }
}