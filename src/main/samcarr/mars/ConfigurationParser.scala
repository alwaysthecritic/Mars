package samcarr.mars

import java.io.File
import io.Source

// Accept an iterator of the lines of config, so that we don't have to worry about
// line endings and so that we can potentially handle in a 'streamed' manner.
// qq Actually streaming may require very careful handling and a test to prove it's working!
// qq Would be nice to report line numbers in error messages - zip numbers with lines at some point?
class ConfigurationParser(lines: Iterator[String]) {
  
    val MaxCommands = 100
    
    // qq How to pick out duff file format and give useful errors whilst keeping happy path neat?
    def parse() : Configuration = {
        // First line is grid dimensions.
        val (maxX, maxY) = parseGridDimensions(lines.next)
        
        // Each subsequent set of 3 lines defines a robot journey (third line is a blank line).
        // qq How to do this so that it effectively streams through without reading the whole 'file'?
        val robotMissions = lines.grouped(3).map(parseMission(_, maxX, maxY))
        
        Configuration(maxX, maxY, robotMissions.toSeq)
    }
    
    private def parseGridDimensions(line:String): (Int, Int) = {
        // Max two digits per dimension as max coordinate is 50 (though we don't enforce that specifically).
        val lineFormat = """^(\d{1,2}) (\d{1,2})$""".r
        line match {
            case lineFormat(maxX, maxY) => (maxX.toInt, maxY.toInt)
            case _ => throw new BadConfigurationException("Couldn't parse Mars dimensions from line: " + line)
        }
    }
    
    private def parseMission(lines: Seq[String], maxX:Int, maxY:Int): Mission = {
        val firstLine = lines(0)
        // Max two digits per coordinate as max coordinate is 50 (though we don't enforce that specifically).
        val firstLineFormat = """^(\d{1,2}) (\d{1,2}) ([NSEW])$""".r
        val (startX, startY, direction) = firstLine match {
            case firstLineFormat(startX, startY, direction) => (startX.toInt, startY.toInt, Direction.withName(direction))
            case _ => throw new BadConfigurationException("Couldn't parse robot start position from line: " + firstLine)
        }
        
        if (startX > maxX || startY > maxY)
          throw new BadConfigurationException("Robot start position out of bounds: %d %d".format(startX, startY))
        
        val secondLine = lines(1)
        val secondLineFormat = """^([LRF]{0,%s})$""".format(MaxCommands).r
        val commands = secondLine match {
          case secondLineFormat(commands) => commands
          case _ => throw new BadConfigurationException("Commands not recognised (only LRF allowed) or too long (max %s): %s".format(MaxCommands, secondLine))
        }
        
        Mission(startX, startY, direction, commands)
    }
    
    private def parseRobotStart(line:String): (Int, Int) = {
        // Allow max two digits per dimension as max coordinate is 50 (though we don't enforce that specifically).
        val lineFormat = """^(\d{1,2}) (\d{1,2})$""".r
        line match {
            case lineFormat(width, height) => (width.toInt, height.toInt)
            case _ => throw new BadConfigurationException("Couldn't parse Mars dimensions from line: " + line)
        }
    }
}

// qq Is this the best way to define my own exception? Is there a more meaningful base class?
class BadConfigurationException(message: String) extends Exception(message)
