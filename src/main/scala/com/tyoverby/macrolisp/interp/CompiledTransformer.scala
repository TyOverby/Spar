//package com.tyoverby.macrolisp.interp
//
//import java.io._
//import sys.process._
//
//class CompiledTransformer(val input: InputStream, val output: InputStream) {
//  val in = new BufferedReader(new InputStreamReader(input))
//  val out = new PipedOutputStream(new PipedInputStream)
//}
//
//class Reroute(val from: => String) extends InputStream {
//  var fromString = from.toCharArray
//  var idx = 0
//
//
//  def read: Int = {
//    if (idx<fromString.length){
//      fromString(idx)
//    }
//    else{
//      fromString = from.toCharArray
//      idx = 0
//      read
//    }
//  }
//}
