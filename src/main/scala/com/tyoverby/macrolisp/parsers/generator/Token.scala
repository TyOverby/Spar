package com.tyoverby.macrolisp.parsers.generator

import com.tyoverby.macrolisp.parsers.lisp.{Token=>LispToken}
import com.tyoverby.macrolisp.parsers.genjs.{Token=>JSToken}

trait Token
case class LispSide(lispExpr: LispToken) extends Token
case class JSSide(jsprog: List[JSToken]) extends Token


case class Rule(lisp: LispSide, js: JSSide)