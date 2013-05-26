package samcarr.mars

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class MissionRunnerSpec extends FlatSpec with ShouldMatchers {

    // qq Arguably should construct the Configuration object directly rather than relying on Parser
    // to remove dependency.
    private val configLines = List("5 3", "1 1 E", "RFRFRFRF", "", "3 2 N", "FRRFLLFFRRFLL", "", "0 3 W", "LLFFFLFLFL").iterator
    val config = ConfigurationParser.parse(configLines)
  
    "MissionRunner" should "produce expected output for missions" in {
        val missionRunner = new MarsMissionRunner(config)
        val results = missionRunner.runMissions()
        
        results should have length (3)
        
        results(0) should be (HappyRobotSituation(1, 1, Direction.East))
        results(1) should be (LostRobotSituation(3, 3, Direction.North))
        results(2) should be (HappyRobotSituation(2, 3, Direction.South))
    }
}