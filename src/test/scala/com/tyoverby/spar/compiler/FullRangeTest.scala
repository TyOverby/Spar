package com.tyoverby.spar.compiler

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.tyoverby.spar.parsers.generator.{Rule, GeneratorParser}
import com.tyoverby.spar.parsers.lisp.{Token => LispToken, LispParser}

class FullRangeTest extends FlatSpec with Matchers {
  def parseRules(rules: String): List[Rule] = GeneratorParser.parseSlurped(rules).get
  def parseProgram(program: String): List[LispToken] = LispParser.parseSlurped(program).get
  val produce = (Producer.produceAll(_:List[LispToken])(_:List[Rule])).curried

  "full production" should "compile simple function matching correctly" in {
    val rules = """ (:a :b :c) => {:a "(" :b ", " :c ")" } """
    val prog = """(func op1 op2)"""

    val parsedRules: List[Rule] = parseRules(rules)
    val parsedProgram: List[LispToken] = parseProgram(prog)

    produce(parsedProgram)(parsedRules) should equal("func(op1, op2)")
  }

  it should "compile a more advanced test of function matching correctly" in {
    val rules = """ (:a :xs...) => { :a "(" ( "(" :xs ")" ),,, ")" } """
    val prog = """(func op1 op2 op3 op4 op5)"""

    val parsedRules: List[Rule] = parseRules(rules)
    val parsedProgram: List[LispToken] = parseProgram(prog)

    produce(parsedProgram)(parsedRules) should equal("func((op1),(op2),(op3),(op4),(op5))")
  }

  it should "compile addition with multiple rules" in {
    val rules =
      """ # addition #
        | (+) => { "0" }
        | # addition #
        | (+ :x) => { :x }
        | # addition #
        | (+ :x :xs...) => { "(" :x "" ( "+" :xs )... ")" }
      """.stripMargin

    val parsedRules = parseRules(rules)

    produce(parseProgram("(+)"))(parsedRules) should equal ("0")
    produce(parseProgram("(+ 5)"))(parsedRules) should equal ("5")
    produce(parseProgram("(+ 5 10)"))(parsedRules) should equal ("(5+10)")
  }
  it should "compile lambdas correctly" in {
    val rules = """ (lambda (:xs...) :body) => { "(function (" :xs,,, "){ return " :body ";})" } """
    val prog = """(lambda (x y z) "body goes here")"""

    val parsedRules = parseRules(rules)
    val parsedProgram: List[LispToken] = parseProgram(prog)

    produce(parsedProgram)(parsedRules) should equal("(function (x,y,z){ return \"body goes here\";})")
  }

  it should "compile cond statements correctly" in {
    val rules =
      """(cond (:a :b) (:c :d)...) => { "(function (){"
        |                                 "if(" :a "){"
        |                                   "return " :b ";"
        |                                 "}"
        |                                 ("else if(" :c "){"
        |                                   "return " :d ";"
        |                                  "}")... "}) " }
      """.stripMargin

    val prog = """(cond (a b) (c d) (e f) (g h))"""

    val parsedRules = parseRules(rules)

   // produce(parseProgram(prog))(parsedRules) should equal ("")
  }

}
