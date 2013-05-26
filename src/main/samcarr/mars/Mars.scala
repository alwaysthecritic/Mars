package samcarr.mars

import scala.io.Source
import java.io.File

object Mars {
    def main(args: Array[String]) {
        println("Welcome to Mars!")
        
        // Note that this will respect any line endings: \r, \n, \r\n.
        val lines = Source.fromFile("sampleData/sample1.txt", "UTF8").getLines
        val config = ConfigurationParser.parse(lines)
        
        val missionRunner = new MarsMissionRunner(config)
        val finishPositions = missionRunner.runMissions()
    }
}