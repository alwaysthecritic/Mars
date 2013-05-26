package samcarr.mars

import java.io.File
import io.Source


class BadConfigurationException(message: String) extends Exception(message)

object ConfigurationParser {
  
    val MaxCommands = 100
    
    // Accepting an iterator of the lines of config means we don't have to worry about line endings
    // and it's convenient for the caller to use Source.fromFile("foo").getLines
    def parse(lines: Iterator[String]) : Configuration = {
        // First line is grid dimensions.
        val (maxX, maxY) = parseGridDimensions(lines.next)
        
        // Each subsequent set of 3 lines defines a robot journey (third line is a blank line).
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
        
        Mission(HappyRobotSituation(startX, startY, direction), commands)
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
