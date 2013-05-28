package samcarr.mars

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import Direction._

class MissionRunnerSpec extends FlatSpec with ShouldMatchers {

    "MissionRunner" should "produce expected output for missions - sample 1" in {
        val config = Configuration(5, 3, List(Mission(HappyRobot(1, 1, East), "RFRFRFRF"),
                                              Mission(HappyRobot(3, 2, North), "FRRFLLFFRRFLL"),
                                              Mission(HappyRobot(0, 3, West), "LLFFFLFLFL")))
        val missionRunner = new MissionRunner(config)
        val results = missionRunner.runMissions()
        
        results should have length (3)
        
        results(0) should be (HappyRobot(1, 1, East))
        results(1) should be (LostRobot(3, 3, North))
        results(2) should be (HappyRobot(2, 3, South))
    }
    
    it should "produce expected output for missions - sample 2" in {
        val config = Configuration(1, 1, List(Mission(HappyRobot(0, 0, East), "FLFLFLF"),
                                              Mission(HappyRobot(1, 1, South), "FFFF"),
                                              Mission(HappyRobot(0, 0, East), "FFFF")))
        val missionRunner = new MissionRunner(config)
        val results = missionRunner.runMissions()
        
        results should have length (3)
        
        results(0) should be (HappyRobot(0, 0, South))
        results(1) should be (LostRobot(1, 0, South))
        results(2) should be (HappyRobot(1, 0, East))
    }
    
    it should "produce expected output for missions - sample 3" in {
        val config = Configuration(10, 0, List(Mission(HappyRobot(0, 0, East), "FFFFRFFRR"),
                                              Mission(HappyRobot(10, 0, South), "RFFFFFFLFRFFF")))
        val missionRunner = new MissionRunner(config)
        val results = missionRunner.runMissions()
        
        results should have length (2)
        
        results(0) should be (LostRobot(4, 0, South))
        results(1) should be (HappyRobot(1, 0, West))
    }
}