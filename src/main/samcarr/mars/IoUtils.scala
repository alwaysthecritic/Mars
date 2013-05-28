package samcarr.mars

import java.io.File
import scala.io.Source
import java.io.FileNotFoundException
import java.io.PrintWriter

// Utilities for IO, to hide some of the nasties of dealing with the Java classes, especially
// exception handling and closing resources.
// This is not a completely generic implementation - it is somewhat specific to the needs of the
// surrounding codebase, for instance fixing the encoding to UTF8 and printing errors to the console.
object IoUtils {
    
    case class FileEncoding(name: String)
    val Utf8 = FileEncoding("UTF8")
    
    // Ensures Source is closed and covers FileNotFoundException.
    def withSource[A](file: File)(op: Source => Option[A])(implicit encoding: FileEncoding): Option[A] = {
        try {
            val source = Source.fromFile(file, encoding.name)
            try op(source)
            finally source.close()
        }
        catch {
            case e: FileNotFoundException => {
                println("Couldn't find file: " + file.getAbsolutePath())
                None
            }
        }
    }
    
    // Ensures PrintWriter is closed and prints any exceptions arising from constructor.
    def withPrintWriter(filePath: String)(op: PrintWriter => Unit)(implicit encoding: FileEncoding) {
        try {
            // PrintWriter constructor could throw exceptions, which we catch.
            val writer = new PrintWriter(filePath, encoding.name)
            try op(writer)
            finally writer.close()
        }
        catch {
            case e: FileNotFoundException => println("Could not write to file: %s, exception: %s".format(filePath, e.getMessage()))
            case e: SecurityException => println("Access denied to write to file: %s, exception: %s".format(filePath, e.getMessage()))
        }
    }
}