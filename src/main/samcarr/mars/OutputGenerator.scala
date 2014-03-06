package samcarr.mars

object OutputGenerator {
    
    // Given a list of Robots, generate String in the output format required, one line per Robot.
    def render(robots: Seq[Either[Robot, Robot]]): String = {
        val lines = robots.map {
            case Right(Robot(x, y, facing)) => "%d %d %s".format(x, y, facing)
            case Left(Robot(x, y, facing)) => "%d %d %s LOST".format(x, y, facing)
        }
        lines.mkString("\r\n")
    }
}