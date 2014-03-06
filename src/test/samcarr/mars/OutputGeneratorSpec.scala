package samcarr.mars

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import Direction._

class OutputGeneratorSpec extends FlatSpec with ShouldMatchers {
    
    "OutputGenerator" should "produce correct string output" in {
        val robots = Seq(Right(Robot(1, 1, East)), Left(Robot(3, 3, North)), Right(Robot(2, 3, South)))
        val output = OutputGenerator.render(robots)
        val outputLines = output.split("\r\n")
        
        outputLines should have length (3)
        
        outputLines(0) should be ("1 1 E")
        outputLines(1) should be ("3 3 N LOST")
        outputLines(2) should be ("2 3 S")
    }
}