package com.tyoverby.spar.watcher

import java.io.File
import scala.collection.mutable

sealed trait EventType

case object Exists extends EventType

case object Create extends EventType

case object Update extends EventType

case object Delete extends EventType

class EventQueue {
  val queue = mutable.Queue[(File, EventType)]()

  def empty = queue.isEmpty
  def notEmpty = queue.nonEmpty

  def enqueue(file: File, typ: EventType) {
    queue.enqueue((file, typ))
  }

  def dequeue() = {
    queue.dequeue()
  }

  def enqueueExists(file: File) {
    println(file + " exists")
    enqueue(file, Exists)
  }

  def enqueueCreate(file: File) {
    println(file + " created")
    enqueue(file, Create)
  }

  def enqueueUpdate(file: File) {
    println(file + " updated")
    enqueue(file, Update)
  }

  def enqueueDelete(file: File) {
    println(file + " deleted")
    enqueue(file, Delete)
  }
}
