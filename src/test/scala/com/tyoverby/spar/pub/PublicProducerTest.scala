package com.tyoverby.spar.pub

import org.scalatest.{Matchers, FlatSpec}
import java.io.File

class PublicProducerTest extends FlatSpec with Matchers {
  "a program with lots of components" should "compile" in {
    val files = List("math", "flow", "list", "object", "set", "last").map("./src/test/resources/standard/" + _ + ".rules")

    val compiled = PublicProducer.compileFileFiles(new File("./src/test/resources/program.jslisp"), files.map(x => new File(x)))
  }
}
