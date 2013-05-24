package samcarr.mars

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class ConfigurationFileLoaderSpec extends FlatSpec with ShouldMatchers {
  
    private val validConfig = List("5 3", "1 1 E", "RFRFRFRF", "", "3 2 N", "FRRFLLFFRRFLL").iterator
      
    "ConfigurationFileLoader" should "parse correctly formatted config" in {
        val config = ConfigurationParser.parse(validConfig)
        
        config.maxX should equal (5)
        config.maxY should equal (3)
        
        val missions = config.missions.toArray
        missions should have length (2)
        
        missions(0) should have {
          'startX (1)
          'startY (1)
          'facing (Direction.East)
          'commands ("RFRFRFRF")
        }
        missions(1) should have {
          'startX (3)
          'startY (2)
          'facing (Direction.North)
          'commands ("FRRFLLFFRRFLL")
        }
    }
      
    it should "throw BadConfigurationException if grid dims are not integers" in {
        val lines = List("ab c")
        checkExceptionMessage(lines, "Couldn't parse Mars dimensions from line: ab c")
    }
      
    it should "throw BadConfigurationException if grid dims are more than two digits" in {
        val lines = List("999 333")
        checkExceptionMessage(lines, "Couldn't parse Mars dimensions from line: 999 333")
    }
    
    it should "throw BadConfigurationException if first robot start coord is more than two digits" in {
        val lines = List("10 10", "111 2 E")
        checkExceptionMessage(lines, "Couldn't parse robot start position from line: 111 2 E")
    }
    
    it should "throw BadConfigurationException if second robot start coord is more than two digits" in {
        val lines = List("10 10", "1 222 E")
        checkExceptionMessage(lines, "Couldn't parse robot start position from line: 1 222 E")
    }
    
    it should "throw BadConfigurationException if robot start direction not one of NSEW" in {
        val lines = List("10 10", "1 1 Z")
        checkExceptionMessage(lines, "Couldn't parse robot start position from line: 1 1 Z")
    }
    
    it should "throw BadConfigurationException if robot commands not purely LRF" in {
        val lines = List("10 10", "1 1 E", "LRFLX")
        checkExceptionMessage(lines, "Commands not recognised (only LRF allowed) or too long (max 100): LRFLX")
    }
    
    private def checkExceptionMessage(configLines: List[String], expectedMessage: String) {
        val thrown = evaluating { ConfigurationParser.parse(configLines.iterator) } should produce [BadConfigurationException]
        thrown.getMessage should be (expectedMessage)
    }
}