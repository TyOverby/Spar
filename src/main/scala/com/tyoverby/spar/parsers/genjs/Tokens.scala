package com.tyoverby.spar.parsers.genjs
import org.apache.commons.lang.StringEscapeUtils

trait Token

object JSTokens{
  def genStringLiteral(str: String): StringLiteral = {
    StringLiteral(StringEscapeUtils.unescapeJava(str))
  }

  case class StringLiteral(str: String) extends Token
  case class Variable(name: String) extends Token
  case class CommaRepeat(tok: Token) extends Token
  case class Group(lst: List[Token]) extends Token
  case class Repeat(tok: Token) extends Token
}
