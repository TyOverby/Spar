package com.tyoverby.macrolisp.pub

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class PublicProducerTest extends FlatSpec with ShouldMatchers {
  val files = List("math", "flow", "list", "object", "set", "last").map("./src/test/resources/standard/" + _ + ".rules")

  val compiled = PublicProducer.compileFiles("./src/test/resources/program.jslisp", files: _*)
  println(compiled)
}
