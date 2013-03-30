package com.tyoverby.macrolisp.parsers.genjs

import com.tyoverby.macrolisp.parsers.lisp.{Token => LispToken}
import com.tyoverby.macrolisp.parsers.genjs.{Token => genJSToken}

case class RuleToken(lispSide: LispToken, jsSide: List[genJSToken])
