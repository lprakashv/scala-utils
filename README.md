[![Build Status](https://travis-ci.org/lprakashv/scala-utils.svg?branch=master&&style=flat-square)](https://travis-ci.org/lprakashv/scala-utils)

# scala-utils
Utilities for Scala

## Common utilities in Scala
### FileUtils

### StringUtils

### DocumentUtils
* underscoreDocumentKeys
  - Required arguments: source file path, destination file path, undescore symbol (by default it will be "_") to be put, [optional] indexes where the symbols need to be placed
  - The source file should have the lines having format: `*key<separator>value*`
  - The keys of the document with format: "thisIsAKey" will be transformed in the destination file as "this_is_a_key" ("this-is-a-key" if symbol "-" is passed instead of default one)

* camelcaseDocumentKeys
  - Required arguments: source file path, destination file path, a start with capital boolean (by default it will be false) to be put, [optional] indexes where the case need to uppered.
  - The source file should have the lines having format: `*key<separator>value*`
  - The keys of the document with format: "this_is_a_key" will be transformed in the destination file as "thisIsAKey" ("ThisIsAKey" if start with capital is passed as true).

## Test it using:
1. Update your testing code in `src/main/com/lprakashv/Main.scala` file
2. `sbt clean assembly`
3. Generated JAR file: `target/scala-2.13/scala-utils-assembly-<version>.jar`
4. `java -jar <jar-file> <optional-args>...`
