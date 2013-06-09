package com.tyoverby.spar.compiler

import com.tyoverby.spar.parsers.lisp.{Token => LispToken}
import com.tyoverby.spar.parsers.generator.Rule
import com.tyoverby.spar.parsers.lisp.LispTokens.{Identifier, NumberLiteral, StringLiteral}
import com.tyoverby.spar.parsers.genjs.JSTokens.{StringLiteral => JSString, Variable => JSVariable, Repeat => JSRepeat, CommaRepeat, Group => JSGroup}
import com.tyoverby.spar.parsers.genjs.{Token => JSToken}

case class ProducerException(reason: String) extends Exception(reason)

object Producer {
  def mergedown(token: LispToken)(implicit rules: List[Rule]): String = {
    token match {
      case StringLiteral(s) => s
      case NumberLiteral(n) => if(n%1!=0) s"$n" else f"${n.toInt}"
      case Identifier(i) => i
      case other => produceSingle(other)
    }
  }

  def produceSingle(lisp: LispToken)(implicit rules: List[Rule]): String = {
    val (env, jss) = PatternMatcher.matchAgainstAll(lisp, rules)
    walkOuter(env,jss.jsprog)
  }

  def produceAll(lisps: List[LispToken])(implicit rules: List[Rule]): String = {
    lisps.map(l=>produceSingle(l)).mkString("")
  }

  def walkOuter(env: Env[String, LispToken], jsprog: List[JSToken])(implicit rules: List[Rule]): String = {
    jsprog.map {
      case JSString(s) => s.substring(1, s.length - 1)
      case JSVariable(v) => mergedown(env.singleVars(v))
      case CommaRepeat(JSVariable(i)) => env.listVars(i).map(mergedown).mkString(",")
      case CommaRepeat(group: JSGroup) => walkInner(env, group).mkString(",")
      case JSRepeat(JSVariable(i)) => env.listVars(i).map(mergedown).mkString(" ")
      case JSRepeat(group: JSGroup) => walkInner(env, group).mkString("")
    }.mkString("")
  }

  def walkInner(env: Env[String, LispToken], jsSide: JSGroup)(implicit rules: List[Rule]): List[String] = {
    def walkInsideGroup(env: Env[String, LispToken], part: JSToken): (String, Env[String, LispToken]) = part match {
      case JSString(s) => (s.substring(1, s.length - 1), env)
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
        }
      }
    }
    catch {
      case ListEmptyException => return Nil
    }

    if (mutEnv.consumedOnlySingles) throw new ConsumingOnlySingles

    sb.toString() :: walkInner(mutEnv.copy(consumedOnlySingles = true), jsSide)
  }
}
