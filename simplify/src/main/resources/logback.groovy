import ch.qos.logback.core.ConsoleAppender;

appender('STDOUT', ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%-5level %-12logger{12} - %msg%n"
  }
}

root(WARN, ['STDOUT'])

scan()