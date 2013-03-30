package com.tyoverby.macrolisp.compiler

import com.tyoverby.macrolisp.parsers.lisp.{Token => LispToken}
import com.tyoverby.macrolisp.parsers.lisp.LispTokens._
import com.tyoverby.macrolisp.parsers.lisp.LispTokens.Variable
import com.tyoverby.macrolisp.parsers.lisp.LispTokens.StringLiteral
import com.tyoverby.macrolisp.parsers.generator.Rule
import com.tyoverby.macrolisp.parsers.lisp.LispTokens.Identifier

object Env {
  def empty[K, V]: Env[K, V] = Env(Map(), Map())
}

case class Env[K, V](singleVars: Map[K, V], listVars: Map[K, List[V]]) {
  def addSingle(key: K, value: V): Env[K, V] = {
    copy(singleVars = singleVars ++ Map(key -> value))
  }

  def addList(key: K, values: List[V]): Env[K, V] = {
    copy(listVars = listVars ++ Map(key -> values))
  }

  def addList(key: K, value: V): Env[K, V] = {
    val oldList = listVars.get(key).getOrElse(Nil)
    copy(listVars = listVars ++ Map(key -> (value :: oldList)))
  }

  def getSingle(key: K): (V, Env[K, V]) = {
    (singleVars(key), copy(singleVars - key))
  }

  def getFromList(key: K): (V, Env[K, V]) = {
    (listVars(key).head, copy(listVars = listVars ++ Map(key -> (listVars(key).tail))))
  }

  def ++(other: Env[K, V]): Env[K, V] = {
    Env(singleVars ++ other.singleVars, listVars ++ other.listVars)
  }

  def +>(other: Env[K, V]): Env[K, V] = {
    val oldOtherList = other.listVars.withDefaultValue(Nil)
    val newList: Map[K, List[V]] = singleVars.map {
      case (k, v) => k -> (v :: oldOtherList(k))
    }
    println(other)
    Env(Map(), newList)
  }
}

object PatternMatcher {

  case object PatternNotMatchedException extends Exception

  def matcher(mToken: LispToken, progToken: LispToken)(env: Env[String, LispToken]): Env[String, LispToken] = {
    (mToken, progToken) match {
      case (Identifier(x), Identifier(y)) if x == y => env
      case (StringLiteral(x), StringLiteral(y)) if x == y => env
      case (NumberLiteral(x), NumberLiteral(y)) if x == y => env
      case (Variable(v), o@Identifier(_)) => env.addSingle(v, o)
      case (a: Group, o: Group) => {
        def extract(alst: List[LispToken], olst: List[LispToken]): Env[String, LispToken] = {
          (alst, olst) match {
            case (Nil, Nil) => Env.empty
            case ((Repeat(Variable(inner))) :: Nil, oxy) => env.addList(inner, oxy)
            case ((Repeat(inner)) :: Nil, oxy) => oxy.map(a => matcher(inner, a)(Env.empty)).foldLeft(Env.empty[String, LispToken])((a, b) => b +> a)
            case (x :: xs, y :: ys) => matcher(x, y)(Env.empty) ++ extract(xs, ys)
            case (_, Nil) => throw PatternNotMatchedException
            case (Nil, _) => throw PatternNotMatchedException
          }
        }
        extract(a.lst, o.lst)
      }
      case _ => throw PatternNotMatchedException
    }
  }

  def matchExpr(progToken: LispToken, rule: Rule): Env[String, LispToken] = {
    matcher(rule.lisp.lispExpr, progToken)(Env.empty)
  }


  def matchAgainstAll(progToken: LispToken, rules: List[Rule]) {
    def attemptToFind(rule: Rule): Boolean = {
      try {
        matchExpr(progToken, rule)
        true
      }
      catch {
        case _: Throwable => false
      }
    }

    val realRule = rules.find(attemptToFind)
    val applied: Env[String, LispToken] = realRule match {
      case Some(r) => matchExpr(progToken, r)
      case _ => throw PatternNotMatchedException
    }
  }
}
