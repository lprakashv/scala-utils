[![Build Status](https://travis-ci.org/lprakashv/scala-utils.svg?branch=master&style=flat-square)](https://travis-ci.org/lprakashv/scala-utils?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/lprakashv/scala-utils/badge.svg?branch=master)](https://coveralls.io/github/lprakashv/scala-utils?branch=master)

# scala-utils
> My personal collection of Scala utilities and rewrite of an existing data structure or a pattern.

##### Packages and their descriptions:

* #### collections
    * MyTrie => A Trie data structure implementation in Scala.
    
* #### resiliency [take out as library in another module/repo]
  * Circuit => A circuit specification for circuit-breaker design pattern.
  
  Usage:
  ```
  val myCircuit = new Circuit[Int](
      "sample-circuit", 
       5, 
       5.seconds,
       1, 
       -1
  )
  
  def doThingAndReturnInt: Int = ??? //method to wrap
  
  def doThingAndReturnIntF: Future[Int] = ??? //async method to wrap
  
  myCircuit.execute(doThingAndReturnInt)
  
  myCircuit.executeAsync(doThingAndReturnIntF)
  
  myCircuit.execute {
      val x = 23
      //.. do something here
      ???
      val y = getAndIntRandomly()
      x / y
  }
  
  //others methods like
  
  implict val circuit: Circuit[T] = ???
  
  (f: => R).execute
  
  (ff: => Future[R]).executeAsync
  
  //another circuit to be applied on similar blocks
  val circuit2: Circuit[T] = ???
  
  (f: => R)(circuit2).execute
    
  (ff: => Future[R])(circuit2).executeAsync
  ```

* #### commons
  * AritmeticUtils => utility implicit classes and extension methods needed to do arithmetic operations 

* #### files
  * FileUtils =>
  * DocumentUtils => 

* #### strings
  * StringUtils =>
  
  
## TODOs:
- [ ] Added Retry component in resiliency package.
- [ ] Logging:
    - [ ] Expedite scala logging libraries.
        - [ ] Create on if needed on top of Slf4j.
    - [ ] Improve logging.
- [ ] Make project modular with sbt modules (especially the resiliency package):
    - [ ] Added publish support.
    - [ ] Publish resiliency library.
- [ ] Add maven and gradle support as well with common build script (refer popular sbt projects also supporting maven and gradle).
 
