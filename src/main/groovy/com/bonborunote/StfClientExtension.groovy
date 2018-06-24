package com.bonborunote

class StfClientExtension {
  String stfHostUrl = ""
  String stfAuthToken = ""
  File outputDir = new File("./")
  TestTarget[] testTargets = []
  long testTimeoutMillis = 600000
  boolean  testWithScreenRecord = true
}