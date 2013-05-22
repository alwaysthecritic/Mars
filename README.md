# Mars

To help learn Scala I used the classic Mars robot challenge.

Because I was learning Scala and avoiding looking at anybody else's solutions, I may have done naive things!

## Building and running

The project is defined with [sbt](http://www.scala-sbt.org) so you'll need that installed. Project files for Eclipse (I've been using Scala IDE, an Eclipse variant) are easily generated with `sbt eclipse` then you can import the project into the IDE. Be sure to treat the sbt definition as definitive and edit that rather than the IDE's version - then rerun `sbt eclipse`.

No IDE is required - sbt at the command line can do everything:

    sbt run
    sbt test
    sbt compile