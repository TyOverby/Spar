package com.tyoverby.macrolisp.watcher

import collection.mutable
import com.tyoverby.macrolisp.parsers.generator.Rule
import com.tyoverby.macrolisp.pub.PublicProducer
import java.util
import java.io.File

/**
 * User: Ty
 * Date: 4/12/13
 * Time: 2:00 PM
 */
class AutoCompiler(rulesDirectory: String, rulesRegex: String, sourceDirectory: String, sourceRegex: String, outputDirectory: String) {
  val rulesWatcher = DirectoryWatcher(rulesDirectory, rulesRegex, _ => (), _ => (), _ => (), _ => ())
  val sourceWatcher = DirectoryWatcher(sourceDirectory, sourceRegex, _ => (), _ => (), _ => (), _ => ())

  val fileToRuleMap = mutable.Map[File, List[Rule]]()
  val listOfSources = new  util.ArrayList[File]

  private[this] def ruleAddUpdate(file: File){
    fileToRuleMap += (file -> PublicProducer.parseRules(file.toString))
  }

  private[this] def forceRecompilation(){

  }

  private[this] def compile(sourceFile: File, rules: List[Rule]){
    val (name, contents) = PublicProducer.compileFile(sourceFile, rules)

  }
}
