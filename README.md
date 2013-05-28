# Mars

To help learn Scala I used the classic Mars robot challenge.

Because I was learning Scala and avoiding looking at anybody else's solutions, I may have done naive things!

## Notes

- Mars contains the main function - start here.
- A functional approach is used for simulating the missions, preferring immutable data and a pipeline that transforms it (into new immutable data).
    - The string of robot commands are mapped into a list of functions each of which takes a Robot and returns a new Robot. Given a start Robot, running all the command functions on it delivers the end Robot.
    - The abstract Robot class records a specific position and direction of a Robot. HappyRobot and LostRobot case classes extend Robot (algebraic data types) which allows the lost case to be neatly handled by the command functions by simply returning the lost robot as is.
    - It would be purer to make the scent map immutable and pass it through the pipe, but that might be over the top. I might try it.
    - Interestingly, if Configuration.missions is typed as a Seq (instead of a List as now) it can be somewhat lazy, and the configuration may not be fully parsed until we actually come to output the final results to file, when the laziness collapses. This wrecks the exception handling for configuration parsing however, because they get thrown later, outside the try/catch block.
- Unit tests using ScalaTest, mostly built via TDD, resulting in easy-to test code with good separation of concerns and encapsulation.
- Build driven with sbt.

## Building and running

The project is defined with [sbt](http://www.scala-sbt.org) so you'll need that installed. Project files for Eclipse (I've been using Scala IDE, an Eclipse variant) are easily generated with `sbt eclipse` then you can import the project into the IDE. Be sure to treat the sbt definition as definitive and edit that rather than the IDE's version - then rerun `sbt eclipse`.

No IDE is required - sbt at the command line can do everything:

    sbt run
    sbt test
    sbt compile