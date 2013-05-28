package samcarr.mars

import scala.io.Source
import java.io.File
import java.io.PrintWriter
import java.io.FileNotFoundException

// The main runnable object - reads config file, outputs results.
object Mars {
    
    val FileEncoding = "UTF8"
    
    def main(args: Array[String]) {
        println("Welcome to Mars!")

        val inputFilePath = if (args.length >= 1) args(0) else "sampleData/sample1.txt"
        val outputFilePath = if (args.length >= 2) args(1) else "sampleData/output1.txt"
          
        val config = readConfig(inputFilePath)
        if (config.isDefined) {
            val finishedRobots = new MarsMissionRunner(config.get).runMissions()
            writeOutput(outputFilePath, finishedRobots)
        }
    }
    
    private def readConfig(filePath: String): Option[Configuration] = {
        println("Reading config from " + filePath)
        
        val file = new File(filePath)
        try {
            withSource(file) { source =>
                // getLines will respect any line endings: \r, \n, \r\n.
                val lines = source.getLines
                Some(ConfigurationParser.parse(lines))
            }
        }
        catch {
           case e: FileNotFoundException => {
               println("Couldn't find config file: " + file.getAbsolutePath())
               None
           }
           case e: BadConfigurationException => {
               println("Couldn't parse config: " + e.getMessage())
               None
           }
        }
    }
    
    private def writeOutput(filePath: String, robots: Seq[Robot]) {
        val output = OutputGenerator.render(robots)
        withPrintWriter(filePath) { _.write(output) }

        println("Wrote output to " + filePath)
    }
    
    // Ensures Source is closed.
    private def withSource[A](file: File)(op: Source => A): A = {
        val source = Source.fromFile(file, FileEncoding)
        try op(source)
        finally source.close()
    }
    
    // Ensures PrintWriter is closed and prints any exceptions arising from constructor.
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