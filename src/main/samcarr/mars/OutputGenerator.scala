package samcarr.mars

object OutputGenerator {
    
    // Given a list of Robots, generate String in the output format required, one line per Robot.
    def render(robots: Seq[Robot]): String = {
        val lines = robots.map {
            case HappyRobot(x, y, facing) => "%d %d %s".format(x, y, facing)
            case LostRobot(x, y, facing) => "%d %d %s LOST".format(x, y, facing)
        }
        lines.mkString("\r\n")
    }
}