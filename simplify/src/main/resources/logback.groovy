appender("FILE", FileAppender) {
  file = "testFile.log"
  append = true
  encoder(PatternLayoutEncoder) {
    pattern = "%level %logger - %msg%n"
  }
}

root(DEBUG, ["FILE"])