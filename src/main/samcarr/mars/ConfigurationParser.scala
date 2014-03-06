package samcarr.mars

import java.io.File
import io.Source
import scala.collection.mutable.ListBuffer

sealed case class FailParsing(reason:String)

object ConfigurationParser {
  
    val MaxCommands = 100
    
    val GridDimensionsRegex = """^(\d{1,2}) (\d{1,2})$""".r
    // Max two digits per coordinate as max coordinate is 50 (though we don't enforce that specifically).
    val MissionStartRegex = """^(\d{1,2}) (\d{1,2}) ([NSEW])$""".r
    val MissionCommandsRegex = """^([LRF]{0,%s})$""".format(MaxCommands).r
    
    def parse(lines: Seq[String]) : Either[FailParsing, Configuration] = {
        if (lines.isEmpty) Left(FailParsing("Config was empty."))
        else parseGridDimensions(lines.head).right.flatMap { case (maxX, maxY) =>
            // Each subsequent set of 3 lines defines a robot journey (third line of which is blank).
            val missionLines = lines.tail.grouped(3)
            // Because missionLines is an iterator, this is all (deliberately) lazy from here on, so
            // won't actually parse all missions if a failure is found.
            val missions = missionLines map (parseMission(_, maxX, maxY))
            failIfAnyFailed(missions.toStream).right map (missions => Configuration(maxX, maxY, missions.toList))
        }
    }
    
    def failIfAnyFailed[A,B](eithers: Stream[Either[A,B]]): Either[A, Seq[B]] = {
        @annotation.tailrec
        def go(acc: ListBuffer[B], eithers: Seq[Either[A,B]]): Either[A, Seq[B]] = eithers match {
            case Stream.Empty => Right(acc.toSeq)
            case Left(a) #:: _ => Left(a)
            case Right(b) #:: t => go(acc += b, t)
        }
        go(ListBuffer[B](), eithers)
    }
    
    private def parseGridDimensions(line:String): Either[FailParsing, (Int, Int)] = line match {
        case GridDimensionsRegex(maxX, maxY) => Right((maxX.toInt, maxY.toInt))
        case _ => Left(FailParsing("Couldn't parse Mars dimensions from line: " + line))
    }
    
    private def parseMission(lines: Seq[String], maxX:Int, maxY:Int): Either[FailParsing, Mission] = {
        // qq Would be nice to use a for comprehension, but Either.right doesn't support pattern matching
        // for a tuple in a case statement (because it doesn't have filter/withFilter). Similarly in parse().
        parseMissionStartPos(lines(0), maxX, maxY).right flatMap { case (x, y, direction) =>
            parseMissionCommands(lines(1)).right map (commands => Mission(Robot(x, y, direction), commands))
        }
    }
    
    private def parseMissionStartPos(line: String, maxX: Int, maxY: Int) = line match {
        case MissionStartRegex(startX, startY, direction) => {
            if (startX.toInt <= maxX && startY.toInt <= maxY) Right(startX.toInt, startY.toInt, Direction.withName(direction))
            else Left(FailParsing(s"Robot start position out of bounds: $line"))
        }
        case _ => Left(FailParsing(s"Couldn't parse robot start position from line: $line"))
    }
    
    private def parseMissionCommands(line: String) = line match {
        case MissionCommandsRegex(commands) => Right(commands)
        case _ => Left(FailParsing(s"Commands not recognised (only LRF allowed) or too long (max $MaxCommands): $line"))
    }
}
