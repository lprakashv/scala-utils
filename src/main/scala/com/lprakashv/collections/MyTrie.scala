package com.lprakashv.collections
import com.lprakashv.collections.MyTrie.MyTrieNode

import scala.collection.mutable

class MyTrie[T] {
  val state: MyTrieNode[T] = MyTrieNode[T]()

  def insert(k: String, v: T): Unit =
    insert(k, v, _ => v)

  def insert(k: String, v: T, handleOldValue: T => T): Unit = {
    val lastNode = k.toLowerCase.foldLeft(state) {
      case (node, char) =>
        node.getNode(char) match {
          case Some(n) => n
          case None    => node.add(char)
        }
    }
    lastNode.value match {
      case Some(oldValue) => lastNode.setValue(handleOldValue(oldValue))
      case None           => lastNode.setValue(v)
    }
  }

  def getValue(k: String): Option[T] = {
    k.toLowerCase
      .foldLeft(Option(state)) { (nodeOpt, char) =>
        nodeOpt.flatMap(_.getNode(char))
      }
      .flatMap(_.value)
  }

  def sortedMap(
    prefix: String
  )(implicit ordering: Ordering[T]): List[(String, T)] = {
    def rec(p: String, node: MyTrieNode[T]): List[(String, T)] = {

      node.value.map(v => (p, v)) match {
        case Some(head) =>
          head :: node.nodeMap.toList.flatMap { case (c, t) => rec(s"$p$c", t) }
        case None =>
          node.nodeMap.toList.flatMap { case (c, t) => rec(s"$p$c", t) }
      }
    }

    val prefixLastNodeOpt = prefix.toLowerCase.foldLeft(Option(state)) {
      (nodeOpt, char) =>
        nodeOpt.flatMap(_.getNode(char))
    }

    (prefixLastNodeOpt match {
      case Some(prefixLastNode) => rec(prefix, prefixLastNode)
      case None                 => List.empty[(String, T)]
    }).sortBy(_._2)(ordering.reverse)
  }
}

object MyTrie {
  import mutable.{Map => MM}

  type Node[T] = MyTrieNode[T]

  case class MyTrieNode[T](nodeMap: MM[Char, Node[T]] = MM.empty[Char, Node[T]],
                           var value: Option[T] = None) {
    def add(c: Char): MyTrieNode[T] = {
      val node = MyTrieNode[T]()
      nodeMap.addOne(c, node)
      node
    }

    def setValue(v: T): Unit = value = Some(v)

    def getNode(c: Char): Option[MyTrieNode[T]] = nodeMap.get(c)
  }
}
