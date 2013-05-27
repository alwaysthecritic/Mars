package samcarr.mars

import scala.io.Source
import java.io.PrintWriter
import java.io.FileNotFoundException

// The main runnable object - reads config file, outputs results.
object Mars {
    
    val FileEncoding = "UTF8"
    
    def main(args: Array[String]) {
        println("Welcome to Mars!")

        val config = readConfig("sampleData/sample1.txt")
        val finishedRobots = new MarsMissionRunner(config).runMissions()
        writeOutput("sampleData/output1.txt", finishedRobots)
    }
    
    private def readConfig(filePath: String) = {
        println("Reading config from " + filePath)
        
        // Note that this will respect any line endings: \r, \n, \r\n.
        val configLines = Source.fromFile(filePath, FileEncoding).getLines
        ConfigurationParser.parse(configLines)
    }
    
    private def writeOutput(filePath: String, robots: Seq[Robot]) {
        val output = OutputGenerator.render(robots)
        withPrintWriter(filePath) { _.write(output) }

        println("Wrote output to " + filePath)
    }
    
    private def withPrintWriter(filePath: String)(op: PrintWriter => Unit) {
        try {
            // PrintWriter constructor could throw exceptions, which we catch.
            val writer = new PrintWriter(filePath, FileEncoding)
            try op(writer)
            finally writer.close()
        }
        catch {
            case e: FileNotFoundException => println("Could not write to file: %s, exception: %s".format(filePath, e.getMessage()))
            case e: SecurityException => println("Access denied to write to file: %s, exception: %s".format(filePath, e.getMessage()))
        }
    }
}