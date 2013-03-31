package com.tyoverby.macrolisp.compiler


object Env {
  def empty[K, V]: Env[K, V] = Env(Map(), Map())
}

case object ListEmptyException extends Exception

case class Env[K, V](singleVars: Map[K, V], listVars: Map[K, List[V]]) {
  /**
   * Adds a single key->value binding to the single vars mapping
   * @return A copy of the current environment with this key value mapping inside
   */
  def addSingle(key: K, value: V): Env[K, V] = {
    copy(singleVars = singleVars ++ Map(key -> value))
  }

  /**
   * Adds an entire list of values to a key in the list vars mapping
   * @return A copy of the current environment with this key-value mapping inside
   */
  def addList(key: K, values: List[V]): Env[K, V] = {
    copy(listVars = listVars ++ Map(key -> values))
  }

  /**
   * Adds a single value to a list in listVars
   * @return A copy of the current environment with this key-value mapping inside
   */
  def addToList(key: K, value: V): Env[K, V] = {
    val oldList = listVars.get(key).getOrElse(Nil)
    copy(listVars = listVars ++ Map(key -> (value :: oldList)))
  }

  /**
   * Retrieves a single value from the singleValues mapping
   * @return The corresponding value and an unmodified copy of the environment
   */
  def getSingle(key: K): (V, Env[K, V]) = {
    (singleVars(key), copy(singleVars))
  }

  /**
   * Retrieves a single values from the listVars mapping
   * and then returns an environment in which that value
   * is <em>not</em> in it.
   * @return The first value in the list corresponding to the
   *         key and a modified copied environment missing the
   *         value that was returned.
   */
  def getFromList(key: K): (V, Env[K, V]) = {
    // If there is actually a key in the list vars mapping, return the first one
    // in the list, and a new environment without that part.
    if(listVars.contains(key)){
      // If the list is empty, the only way this could be is if
      // it was consumed, so we will throw an exception to cancel the operation
      // in Producer
      if (listVars(key) == Nil) throw ListEmptyException
      (listVars(key).head, copy(listVars = listVars ++ Map(key -> (listVars(key).tail))))
    }
    // If there isn't then we must be talking about a single variable list
    else{
      (singleVars(key), this)
    }
  }

  /**
   * Merges two environments.  Any duplicates are overridden
   * by the 2nd environment
   * @return The merged environment
   */
  def ++(other: Env[K, V]): Env[K, V] = {
    Env(singleVars ++ other.singleVars, listVars ++ other.listVars)
  }

  /**
   * Merges two environments in such a way that single values in this list are
   * mapped into a list with the same key in the multi-mapping list in the other
   * @param other The other environment
   * @return An environment with the swapped mapping
   */
  def +>(other: Env[K, V]): Env[K, V] = {
    val oldOtherList = other.listVars.withDefaultValue(Nil)
    val newList: Map[K, List[V]] = singleVars.map {
      case (k, v) => k -> (v :: oldOtherList(k))
    }
    Env(Map(), newList)
  }
}
