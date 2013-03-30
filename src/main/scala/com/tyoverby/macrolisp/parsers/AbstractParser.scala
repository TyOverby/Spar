package com.tyoverby.macrolisp.parsers

import util.parsing.combinator.JavaTokenParsers

class AbstractParser extends JavaTokenParsers {
  protected def wrap[A, B, T <: Parser[A]](parser: T, gen: A => B): Parser[B] = new Parser[B] {
    def apply(in: Input) = {
      parser.apply(in) match {
        case Success(value, next) => Success(gen(value.asInstanceOf[A]), next)
        case Failure(msg, next) => Failure(msg, next)
        case Error(msg, next) => Error(msg, next)
      }
    }
  }
}
