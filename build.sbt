name := "Mars"

version := "1.0"

scalaVersion := "2.10.1"

// set the main source directory
unmanagedSourceDirectories in Compile <<= baseDirectory(base => List("src/main") map (base / _ ))

// set the test source directory
unmanagedSourceDirectories in Test <<= baseDirectory(base => List("src/test") map (base / _ ))

// set the main class for the main 'run' task
// change Compile to Test to set it for 'test:run'
mainClass in (Compile, run) := Some("samcarr.mars.Mars")

// add a test dependency on ScalaTest
libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
