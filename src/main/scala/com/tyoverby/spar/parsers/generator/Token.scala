package com.tyoverby.spar.parsers.generator

import com.tyoverby.spar.parsers.lisp.{Token=>LispToken}
import com.tyoverby.spar.parsers.genjs.{Token=>JSToken}

trait Token
case class LispSide(lispExpr: LispToken) extends Token
case class JSSide(jsprog: List[JSToken]) extends Token


case class Rule(lisp: LispSide, js: JSSide)