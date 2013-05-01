package com.tyoverby.macrolisp.pub

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.io.File

class PublicProducerTest extends FlatSpec with ShouldMatchers {
  "a program with lots of components" should "compile" in {
    val files = List("math", "flow", "list", "object", "set", "last").map("./src/test/resources/standard/" + _ + ".rules")

    val compiled = PublicProducer.compileFileFiles(new File("./src/test/resources/program.jslisp"), files.map(x => new File(x)))
  }
}
