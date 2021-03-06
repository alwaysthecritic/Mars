package samcarr.mars

import scala.io.Source
import java.io.File
import java.io.PrintWriter
import java.io.FileNotFoundException
import IoUtils._

// The main runnable object - reads config file, outputs results.
object Mars {
    
    implicit val FileEncoding = Utf8
    
    def main(args: Array[String]) {
        val inputFilePath = if (args.length >= 1) args(0) else "sampleData/sample1.txt"
        val outputFilePath = if (args.length >= 2) args(1) else "sampleData/output1.txt"
          
        readConfig(inputFilePath).map { config =>
            val finishedRobots = new MissionRunner(config).runMissions()
            writeOutput(outputFilePath, finishedRobots)
        }
    }
    
    private def readConfig(filePath: String): Option[Configuration] = {
        println("Reading config from " + filePath)
        
        val file = new File(filePath)
        withSource(file) { source =>
            // getLines will respect any line endings: \r, \n, \r\n.
            ConfigurationParser.parse(source.getLines.toSeq) match {
                case Right(config) => Some(config)
                case Left(FailParsing(reason)) => println(s"Couldn't parse config: $reason"); None
            }
        }
    }
    
    private def writeOutput(filePath: String, robots: Seq[Robot]) {
        val output = OutputGenerator.render(robots)
        withPrintWriter(filePath) { _.write(output) }

        println("Wrote output to " + filePath)
    }
}