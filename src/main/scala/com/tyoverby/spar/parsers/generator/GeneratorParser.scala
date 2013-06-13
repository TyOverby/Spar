package com.tyoverby.spar.parsers.generator

import util.parsing.input.CharSequenceReader
import scala.util.parsing.combinator.JavaTokenParsers

import com.tyoverby.spar.parsers.lisp.LispParser
import com.tyoverby.spar.parsers.genjs.GenJsParser

object GeneratorParser extends JavaTokenParsers {


  def parseLisp: Parser[LispSide] = (LispParser.parseExpression ^^ LispSide).asInstanceOf[Parser[LispSide]]

  def parseJS: Parser[JSSide] = (GenJsParser.parseProgram ^^ JSSide).asInstanceOf[Parser[JSSide]]


  def parseRule: Parser[Rule] = rep(comment)~> parseLisp ~ "=>" ~ parseJS <~ rep(comment) ^^ {
    case lisp ~ "=>" ~ js => Rule(lisp, js)
  }

  def comment = "#[^\n]*\n?".r

  def parseRules: Parser[List[Rule]] = rep(parseRule)

  def parseSlurped(str: String): ParseResult[List[Rule]] = {
    parseRules(new CharSequenceReader(str))
  }
}