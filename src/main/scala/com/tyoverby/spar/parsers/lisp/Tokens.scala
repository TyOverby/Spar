package com.tyoverby.spar.parsers.lisp

sealed trait Token

object LispTokens {

  case class Identifier(ident: String) extends Token {
    override def toString: String = ident
  }

  case class Variable(str: String) extends Token {
    override def toString: String = str
  }

  case class StringLiteral(str: String) extends Token {
    override def toString: String = str
  }

  case class NumberLiteral(num: Double) extends Token{
    override def toString: String = num.toString
  }

  case class RegexLiteral(reg: String) extends Token

  trait Group extends Token {
    val lst: List[Token]
  }

  case class ParenGroup(lst: List[Token]) extends Group {
    override def toString: String = "(" + lst.mkString(" ") + ")"
  }

  case class BracketGroup(lst: List[Token]) extends Group{

    override def toString: String = "(" + lst.mkString(" ") + ")"
  }

  case class CurlyGroup(lst: List[Token]) extends Group{

    override def toString: String = "(" + lst.mkString(" ") + ")"
  }

  case class Repeat(tok: Token) extends Token{
    override def toString: String = tok.toString + "..."

  }
}


object TokenTranslations {
  def genNumberLiteral(s: String): LispTokens.NumberLiteral = {
    LispTokens.NumberLiteral(s.toDouble)
  }
}