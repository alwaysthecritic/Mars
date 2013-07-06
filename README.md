# Mars

To help learn Scala I used the classic Mars robot challenge.

Because I was learning Scala and avoiding looking at anybody else's solutions, I may have done naive things!

## Notes

- Mars contains the 'main' function: it reads the config file, runs the missions and writes the output.
- MissionRunner is the core of the program that actually simulates the robots' missions and probably the most interesting part.
- A functional approach is used for simulating the missions, preferring immutable data and a pipeline that transforms it (into new immutable data). Therefore relatively 'dumb' data structures are preferred, rather than OO style data+behaviour.
    - The string of robot commands are mapped into a list of functions each of which takes a Robot and returns a new Robot. Given a start Robot, running all the command functions on it delivers the end Robot.
    - The abstract Robot class records a specific position and direction of a Robot. HappyRobot and LostRobot case classes extend Robot (algebraic data types) which allows the lost case to be neatly handled by the command functions by simply returning the lost robot as is.
    - It would be purer to make the scent map immutable and pass it through the pipe, but that would almost certainly make the code more complicated for no real added value.
    - ConfigurationParser uses nice, functional error handling, but there's an interesting story behind it.
        - It was originally written so that it threw a BadConfigurationException for each of its error cases, but this highlighted an important issue with exceptions in a non-strict world. The config wasn't actually being fully parsed until attempting to render the output unravelled the whole lazy structure. But if the config was bad, exceptions were thrown at that point, after the readConfig try/catch block was long gone, so they weren't caught and the program fell over in an ugly way.
        - This prompted a rewrite of the error handling to use a more functional approach, in this case explicitly returning an Either from any method that could fail. This way lazy evaluation cannot defeat the error handling. There's an interesting blog post to be had from this exercise, as it also took quite a bit of understanding and effort to get the Either handling working nicely and looking relatively simple!
- Unit tests using ScalaTest, mostly built via TDD, resulting in easy-to test code with good separation of concerns and encapsulation. Some functional tests that exercise the whole program would be nice to add in the future though.

## Building and running

The project is defined with [sbt](http://www.scala-sbt.org) so you'll need that installed. Project files for Eclipse (I've been using Scala IDE, an Eclipse variant) are easily generated with `sbt eclipse` then you can import the project into the IDE. Be sure to treat the sbt definition as definitive and edit that rather than the IDE's version - then rerun `sbt eclipse`.

No IDE is required - sbt at the command line can do everything:

    sbt run
    sbt test
    sbt compile