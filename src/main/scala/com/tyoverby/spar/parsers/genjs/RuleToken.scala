package com.tyoverby.spar.parsers.genjs

import com.tyoverby.spar.parsers.lisp.{Token => LispToken}
import com.tyoverby.spar.parsers.genjs.{Token => genJSToken}

case class RuleToken(lispSide: LispToken, jsSide: List[genJSToken])
