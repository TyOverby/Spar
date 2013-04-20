package com.tyoverby.macrolisp.pub

import com.tyoverby.macrolisp.parsers.generator.Rule
import com.tyoverby.macrolisp.parsers.generator.GeneratorParser
import com.tyoverby.macrolisp.compiler.Producer
import io.Source
import com.tyoverby.macrolisp.parsers.lisp.LispParser
import com.tyoverby.macrolisp.parsers.lisp.{Token => LispToken}
import java.io.File

case class RuleParsingFailure(msg: String) extends Exception(msg)

case class ProgramCompilationFailure(filename: String, msg: String) extends Exception(msg)

object PublicProducer {
  /**
   * Parses the rules for a compilation from a fileName
   * @param fileName The path to the file that contains the rules
   * @return A list of rules that were parsed from the file.
   * @throws RuleParsingFailure If the rules can't be parsed
   */
  def parseRules(fileName: String): List[Rule] = {
    val source = Source.fromFile(fileName)
    val slurped = source.getLines().fold("")(_ + _)
    val parsed = GeneratorParser.parseSlurped(slurped)

    val failed = parsed.filter {
      case GeneratorParser.NoSuccess(_, _) => true
      case _ => false
    }

    if (!failed.isEmpty) throw RuleParsingFailure(failed.map(_.toString).mkString("\n\n"))



    parsed.map {
      case GeneratorParser.Success(result: Rule, _) => result
      case x@GeneratorParser.NoSuccess(_, _) => throw RuleParsingFailure(x.toString)
    }
  }

  def parseAllRules(fileNames: String*): List[Rule] = {
    fileNames.map(parseRules).foldLeft(List[Rule]())(_ ++ _)
  }

  /**
   * Parses the lisp program from a filename
   * @param fileName The name of the file that contains the lisp program
   * @return (fileName, tokens) A tuple containing the file name with the list of tokens that make up the program
   */
  def parseSource(fileName: String): (String, List[LispToken]) = {
    val source = Source.fromFile(fileName)
    val slurped = source.foldLeft("")(_ + _)

    parseStringSource(slurped,fileName)
  }

  /**
   * TODO: write documentation
   * @param slurped
   * @param fileName
   * @return
   */
  def parseStringSource(slurped: String, fileName: String = "<no file>"):(String, List[LispToken]) = {
    val parts = LispParser.parseSlurped(slurped)

    parts match {
      case LispParser.Success(result: List[LispToken], _) => (fileName, result)
      case x@LispParser.NoSuccess(_, _) => throw ProgramCompilationFailure(fileName, x.toString)
    }
  }

  /**
   * Compiles the program with the provided rules
   * @param rules The rules to compile the program against
   * @param program The program to be compiled
   * @return
   */
  def compile(program: (String, List[LispToken]), rules: List[Rule]): (String, String) = {
    val (filename, tokens) = program

    (filename, Producer.produceAll(tokens)(rules))
  }

  def compileFiles(programFile: String, rulesFiles: String*): (String, String) = {
    compile(parseSource(programFile), parseAllRules(rulesFiles:_*))
  }

  def compileFile(file: File, rules: List[Rule]): (String, String) = {
    compile(parseSource(file.toString), rules)
  }
}
