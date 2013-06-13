package com.tyoverby.spar.compiler

import com.tyoverby.spar.parsers.lisp.{Token => LispToken}
import com.tyoverby.spar.parsers.lisp.LispTokens._
import com.tyoverby.spar.parsers.lisp.LispTokens.Variable
import com.tyoverby.spar.parsers.lisp.LispTokens.StringLiteral
import com.tyoverby.spar.parsers.generator.{JSSide, Rule}
import com.tyoverby.spar.parsers.lisp.LispTokens.Identifier

object PatternMatcher {

  case class PatternNotMatchedException(token: LispToken, rules: List[Rule] = Nil) extends Exception(s"Could not match $token with a rule.  Available rules: ${rules.mkString("[", ",", "]")}")

  def matcher(mToken: LispToken, progToken: LispToken)(env: Env[String, LispToken]): Env[String, LispToken] = {
    (mToken, progToken) match {
      /** Match only if both identifiers are the same.  This is key to matching groups */
      case (Identifier(x), Identifier(y)) if x == y => env
      // Match only if both string literals are the same.
      // This is a design choice.  I don't know why you would want string
      // literals in your matching side.
      case (StringLiteral(x), StringLiteral(y)) if x == y => env
      // Match only if both Numbers are the same.
      // Same justification as above
      case (NumberLiteral(x), NumberLiteral(y)) if x == y => env
      // A variable matches with anything and adds it to the environment under that
      // variable name.
      case (Variable(v), o) => env.addSingle(v, o)
      // Matching any group against any other group performs a match on the
      // Inside of the groups.
      case (a: Group, o: Group) => {
        // Given the lists of tokens from inside both, try to
        // match their contents.
        def extract(alst: List[LispToken], olst: List[LispToken]): Env[String, LispToken] = (alst, olst) match {
          // If both groups are empty, yield an empty environment
          case (Nil, Nil) => Env.empty
          // If the last item in a group is repeat on a variable,
          // then add all of the rest of the Tokens to the environment
          // with the name of that variable
          case ((Repeat(Variable(inner))) :: Nil, oxy) => env.addList(inner, oxy)


          // If a group is being repeated, then recursively match on what is inside of that group
          // and add up the environment in a way that moves the values into listVars
          case ((Repeat(inner: Group)) :: Nil, oxy) => {
            // Reverse because the list is naturally reversed during traversal
            oxy.reverse
              // Map the match of each element in the program with the group
              // that is the pattern
              .map(a => matcher(inner, a)(Env.empty))
              // Concatenate all of the values (currently in a singleVar)
              // into one big environment where they all live in a listVar
              .foldLeft(Env.empty[String, LispToken])((a, b) => b +> a)
          }
          // If we have a repeated variable that wasn't at the end, we should try
          // to split the list of matched variables into two and then match them individually
          case ((Repeat(inner)) :: xs, oxy) => {
            val remainingXs = xs.length
            val totalYs = oxy.length
            val repeatedYs = totalYs - remainingXs

            val (rep, rest) = oxy.splitAt(repeatedYs)
            extract(Repeat(inner) :: Nil, rep) ++ extract(xs, rest)
          }
          // Match on the first elements of both lists, and then concatenate
          // them when done
          case (x :: xs, y :: ys) => matcher(x, y)(Env.empty) ++ extract(xs, ys)
          // The lists are of unequal size, then this does not match.
          case (_, Nil) => throw PatternNotMatchedException(o)
          case (Nil, _) => throw PatternNotMatchedException(o)
        }
        // Perform that pattern extraction on the lists
        extract(a.lst, o.lst)
      }
      // If a case here wasn't covered, then we do not match.
      case _ => throw PatternNotMatchedException(progToken)
    }
  }

  /**
   * Given a single token and a single rule, try to see if it can match.
   * If so, it produces an environment with the variable mappings.
   * @param progToken The token to match on.
   * @param rule The rule to match on.
   * @return A variable matching
   * @throws PatternNotMatchedException If a match was not found.
   */
  def matchExpr(progToken: LispToken, rule: Rule): Env[String, LispToken] = {
    matcher(rule.lisp.lispExpr, progToken)(Env.empty)
  }

  /**
   * Given a single Token and a list of rules, attempt to find a rule that
   * produces a mapping
   * @param progToken The token to attempt to match
   * @param rules A list of rules to try to match with
   * @return An environment and the JS producing part to match.
   */
  def matchAgainstAll(progToken: LispToken, rules: List[Rule]): (Env[String, LispToken], JSSide) = {
    /**
     * A predicate that checks to see if a rule matches against
     * a token
     */
    def attemptToFind(rule: Rule): Boolean = {
      try {
        matchExpr(progToken, rule)
        true
      }
      catch {
        case _: Throwable => false
      }
    }

    rules.find(attemptToFind) match {
      case Some(r) => (matchExpr(progToken, r), r.js)
      case None => throw PatternNotMatchedException(progToken, rules)
    }
  }
}
