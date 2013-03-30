package com.tyoverby.macrolisp.parsers.genjs

trait Token

object JSTokens{
  case class StringLiteral(str: String) extends Token
  case class Variable(name: String) extends Token
  case class CommaRepeat(tok: Token) extends Token
  case class Group(lst: List[Token]) extends Token
  case class Repeat(tok: Token) extends Token
}
