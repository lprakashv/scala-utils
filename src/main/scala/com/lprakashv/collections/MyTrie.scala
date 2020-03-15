package com.lprakashv.collections
import com.lprakashv.collections.MyTrie.MyTrieNode

class MyTrie[T] {
  val root: MyTrieNode[T] = MyTrieNode[T]()

  def insert(k: String, v: T): Unit =
    insert(k, v, _ => v)

  def insert(k: String, v: T, handleOldValue: T => T): Unit = {
    val lastNode = k.toLowerCase.foldLeft(root) {
      case (node, char) =>
        node.get(char) match {
          case Some(n) => n
          case None    => node.add(char)
        }
    }
    lastNode.value match {
      case Some(oldValue) => lastNode.putValue(handleOldValue(oldValue))
      case None           => lastNode.putValue(v)
    }
  }

  def get(k: String): Option[T] = {
    k.toLowerCase
      .foldLeft(Option(root)) { (nodeOpt, char) =>
        nodeOpt.flatMap(_.get(char))
      }
      .flatMap(_.value)
  }

  def sortedMap(
    prefix: String
  )(implicit ordering: Ordering[T]): List[(String, T)] = {
    def rec(p: String, node: MyTrieNode[T]): List[(String, T)] = {

      node.value.map(v => (p, v)) match {
        case Some(head) =>
          head :: node.map.toList.flatMap { case (c, t) => rec(s"$p$c", t) }
        case None => node.map.toList.flatMap { case (c, t) => rec(s"$p$c", t) }
      }
    }

    val prefixLastNodeOpt = prefix.toLowerCase.foldLeft(Option(root)) {
      (nodeOpt, char) =>
        nodeOpt.flatMap(_.get(char))
    }

    (prefixLastNodeOpt match {
      case Some(prefixLastNode) => rec(prefix, prefixLastNode)
      case None                 => List.empty[(String, T)]
    }).sortBy(_._2)(ordering.reverse)
  }
}

object MyTrie {
  case class MyTrieNode[T](map: collection.mutable.Map[Char, MyTrieNode[T]] =
                             collection.mutable.Map.empty[Char, MyTrieNode[T]],
                           var value: Option[T] = None) {
    def add(c: Char): MyTrieNode[T] = {
      val node = MyTrieNode[T]()
      map.addOne(c, node)
      node
    }

    def putValue(v: T): Unit = value = Some(v)

    def get(c: Char): Option[MyTrieNode[T]] = map.get(c)
  }
}
