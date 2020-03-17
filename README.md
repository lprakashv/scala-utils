[![Build Status](https://travis-ci.org/lprakashv/scala-utils.svg?branch=master&&style=flat-square)](https://travis-ci.org/lprakashv/scala-utils)
[![Coverage Status](https://coveralls.io/repos/github/lprakashv/scala-utils/badge.svg?branch=master)](https://coveralls.io/github/lprakashv/scala-utils?branch=master)
![Scala CI](https://github.com/lprakashv/scala-utils/workflows/Scala%20CI/badge.svg)

# scala-utils
Utilities for Scala

## Common utilities in Scala
> My personal collection of Scala utilities and rewrite of an existing data structure or a pattern.

Packages and their descriptions:

* #### collections
    * MyTrie => A Trie data structure implementation in Scala.
    
* #### circuitbreaker
  * Circuit => A circuit specification for circuit-breaker design pattern.
  ```
  val myCircuit = new Circuit[Int](
      "sample-circuit", 
       5, 
       5.seconds,
       1, 
       -1
  )
  
  def doThingAndReturnInt: Int = ??? //method to wrap
  
  myCircuit.execute(doThingAndReturnInt)
  
  myCircuit.execute {
      val x = 23
      //.. do something here
      ???
      val y = getAndIntRandomly()
      x / y
  }
  ```

* #### commons
  * AritmeticUtils => utility implicit classes and extension methods needed to do arithmetic operations 

* #### files
  * FileUtils =>
  
  * DocumentUtils => 
    * `underscoreDocumentKeys`
      - Required arguments: source file path, destination file path, undescore symbol (by default it will be "_") to be put, [optional] indexes where the symbols need to be placed
      - The source file should have the lines having format: `*key<separator>value*`
      - The keys of the document with format: "thisIsAKey" will be transformed in the destination file as "this_is_a_key" ("this-is-a-key" if symbol "-" is passed instead of default one)
    
    * `camelcaseDocumentKeys`
      - Required arguments: source file path, destination file path, a start with capital boolean (by default it will be false) to be put, [optional] indexes where the case need to uppered.
      - The source file should have the lines having format: `*key<separator>value*`
      - The keys of the document with format: "this_is_a_key" will be transformed in the destination file as "thisIsAKey" ("ThisIsAKey" if start with capital is passed as true).


* #### strings
  * StringUtils =>