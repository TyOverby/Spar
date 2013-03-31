package com.tyoverby.macrolisp.compiler

import com.tyoverby.macrolisp.parsers.lisp.{Token => LispToken}
import com.tyoverby.macrolisp.parsers.generator.Rule
import com.tyoverby.macrolisp.parsers.lisp.LispTokens.{Identifier, NumberLiteral, StringLiteral}
import com.tyoverby.macrolisp.parsers.genjs.JSTokens.{StringLiteral => JSString, Variable => JSVariable, Repeat => JSRepeat, CommaRepeat, Group => JSGroup}
import com.tyoverby.macrolisp.parsers.genjs.{Token => JSToken}

case class ProducerException(reason: String) extends Exception(reason)

object Producer {
  def mergedown(token: LispToken)(implicit rules: List[Rule]): String = {
    token match {
      case StringLiteral(s) => s + " "
      case NumberLiteral(n) => s"$n "
      case Identifier(i) => i + " "
      case other => produceSingle(other) + " "
    }
  }

  def produceSingle(lisp: LispToken)(implicit rules: List[Rule]): String = {
    val (env, jss) = PatternMatcher.matchAgainstAll(lisp, rules)

    "hi"
  }

  def walkOuter(env: Env[String, LispToken], jsprog: List[JSToken])(implicit rules: List[Rule]): String = {
    jsprog.map {
      case JSString(s) => s.substring(1, s.length - 1)
      case JSVariable(v) => mergedown(env.singleVars(v))
      case CommaRepeat(JSVariable(i)) => env.listVars(i).map(mergedown).mkString(", ")
      case CommaRepeat(group: JSGroup) => walkInner(env, group).mkString(", ")
      case JSRepeat(group: JSGroup) => walkInner(env, group).mkString(" ")
    }.mkString(" ")
  }

  def walkInner(env: Env[String, LispToken], jsSide: JSGroup)(implicit rules: List[Rule]): List[String] = {
    def walkInsideGroup(env: Env[String, LispToken], part: JSToken): (String, Env[String, LispToken]) = part match {
      case JSString(s) => (s.substring(1, s.length - 1) + " ", env)
      case JSVariable(v) => {
        val (varResult, newEnv) = env.getFromList(v)
        (mergedown(varResult), newEnv)
      }
      case _ => throw ProducerException(s"Invalid JSToken $part inside of repeated expression")
    }

    var mutEnv = env
    val sb = new StringBuilder

    // There is a better way to do this.  Find it.
    try {
      jsSide.lst.map {
        part => {
          val (str, newenv) = walkInsideGroup(mutEnv, part)
          sb.append(str)
          mutEnv = newenv

          println(mutEnv)
        }
      }
    }
    catch {
      case ListEmptyException => return Nil
    }

//    if (mutEnv.consumedOnlySingles) throw ConsumingOnlySingles

    sb.toString() :: walkInner(mutEnv, jsSide)
  }
}
