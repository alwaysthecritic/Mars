package samcarr.mars

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class ConfigurationParserSpec extends FlatSpec with ShouldMatchers {
  
    private val validConfig = List("5 3", "1 1 E", "RFRFRFRF", "", "3 2 N", "FRRFLLFFRRFLL").iterator
      
    "ConfigurationParser" should "parse correctly formatted config" in {
        val config = ConfigurationParser.parse(validConfig.toSeq).right.get
        
        config.maxX should equal (5)
        config.maxY should equal (3)
        
        val missions = config.missions.toArray
        missions should have length (2)
        
        missions(0) should have (
            'initialRobot (HappyRobot(1, 1, Direction.East)),
            'commands ("RFRFRFRF")
        )
        missions(1) should have (
            'initialRobot (HappyRobot(3, 2, Direction.North)),
            'commands ("FRRFLLFFRRFLL")
        )
    }
    
    it should "fail if there are no lines of config" in {
        val config = List()
        checkFailureMessage(config, "Config was empty.")
    }
    
    it should "fail if grid dims are not integers" in {
        val config = List("ab c")
        checkFailureMessage(config, "Couldn't parse Mars dimensions from line: ab c")
    }
      
    it should "fail if grid dims are more than two digits" in {
        val config = List("999 333")
        checkFailureMessage(config, "Couldn't parse Mars dimensions from line: 999 333")
    }
    
    it should "fail if first robot start coord is more than two digits" in {
        val config = List("10 10", "111 2 E")
        checkFailureMessage(config, "Couldn't parse robot start position from line: 111 2 E")
    }
    
    it should "fail if second robot start coord is more than two digits" in {
        val config = List("10 10", "1 222 E")
        checkFailureMessage(config, "Couldn't parse robot start position from line: 1 222 E")
    }
    
    it should "fail if robot start coords are out of bounds" in {
        // Just inside boundary conditions - no exception thrown.
        ConfigurationParser.parse(List("10 10", "10 10 E", "L").toSeq)
        ConfigurationParser.parse(List("10 10", "0 0 E", "L").toSeq)
        
        // Just outside boundary conditions - exception thrown with correct message.
        // Note that negative start positions would fail parsing (not bounds check) as '-' is not allowed.
        checkFailureMessage(List("10 10", "11 0 E", "L"), "Robot start position out of bounds: 11 0 E")
        checkFailureMessage(List("10 10", "0 11 E", "L"), "Robot start position out of bounds: 0 11 E")
    }
    
    it should "fail if robot start direction not one of NSEW" in {
        val config = List("10 10", "1 1 Z")
        checkFailureMessage(config, "Couldn't parse robot start position from line: 1 1 Z")
    }
    
    it should "fail if robot commands not purely LRF" in {
        val config = List("10 10", "1 1 E", "LRFLX")
        checkFailureMessage(config, "Commands not recognised (only LRF allowed) or too long (max 100): LRFLX")
    }
    
    private def checkFailureMessage(configLines: List[String], message: String) {
        ConfigurationParser.parse(configLines.toSeq) should be (Left(FailParsing(message)))
    }
}