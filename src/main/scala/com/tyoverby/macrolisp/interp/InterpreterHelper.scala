package com.tyoverby.macrolisp.interp

import java.io.{InputStreamReader, BufferedReader}

object InterpreterHelper {
  def sameParens(str: List[Char]): Boolean = {
    def helper(str: List[Char], parenCount: Int, bracketCount: Int, curleyCount: Int): Boolean = {
      str match {
        case Nil => parenCount <= 0 && bracketCount <= 0 && curleyCount <= 0
        case '(' :: xs => helper(xs, parenCount + 1, bracketCount, curleyCount)
        case ')' :: xs => helper(xs, parenCount - 1, bracketCount, curleyCount)
        case '[' :: xs => helper(xs, parenCount, bracketCount + 1, curleyCount)
        case ']' :: xs => helper(xs, parenCount, bracketCount - 1, curleyCount)
        case '{' :: xs => helper(xs, parenCount, bracketCount, curleyCount + 1)
        case '}' :: xs => helper(xs, parenCount, bracketCount, curleyCount - 1)
        case _   :: xs => helper(xs, parenCount, bracketCount, curleyCount)
      }
    }
    helper(str, 0, 0, 0)
  }

  def retrieveStatement(): String = {
    val input = new BufferedReader(new InputStreamReader(System.in))
    val sb = new StringBuilder
    var seen = false
    do{
      if (!seen) print("> ") else print(". "); seen = true
      sb.append(input.readLine()).append(" ")
    }while(!sameParens(sb.toList))
    sb.toString()
  }
}
