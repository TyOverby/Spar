package com.tyoverby.macrolisp.parsers.generator

import com.tyoverby.macrolisp.parsers.AbstractParser
import util.parsing.input.CharSequenceReader

object GeneratorParser extends AbstractParser {

  import com.tyoverby.macrolisp.parsers.lisp.LispParser
  import com.tyoverby.macrolisp.parsers.genjs.GenJsParser


  def parseLisp: Parser[LispSide] = (LispParser.parseExpression ^^ LispSide).asInstanceOf[Parser[LispSide]]

  def parseJS: Parser[JSSide] = (GenJsParser.parseProgram ^^ JSSide).asInstanceOf[Parser[JSSide]]


  def parseRule: Parser[Rule] = parseLisp ~ "=>" ~ parseJS ^^ {
    case lisp ~ "=>" ~ js => Rule(lisp, js)
  }

  def separator = "#[ a-zA-Z0-9]+#".r

  def parseRules: Parser[List[Rule]] = (rep1sep(parseRule, separator)) // ^^ {(a,b) => List(a,b)}

  def parseSlurped(str: String): List[ParseResult[Rule]] = {
    val split = str.split(separator.toString()).filter(_.trim.length > 0)
    split.map(x => new CharSequenceReader(x))
      .map(parseRule)
      .toList
  }

//  def parseSlurpedDebug(str: String): List[(ParseResult[Rule], String)] = {
//    val split = str.split(separator.toString()).filter(_.trim.length > 0)
//    split.map(x => new CharSequenceReader(x))
//      .map(parseRule)
//      .zip(split)
//      .toList
//  }
}