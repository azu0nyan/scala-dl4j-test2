package NotesTest

import java.io.{File, PrintWriter}


import scala.util.Random

object NotesGenerator {
  val count = 100000
  val seed = 123
  val samplesPerSec = 44100
  val sampleDt: Double = 1d / samplesPerSec.toDouble
  val samplesPerEntry = 400


  val periodicFunction: Double => Double = x => math.sin(x * (2d * math.Pi))

  def generate(): Seq[Seq[Any]] = {
    println(s"generating $count entries samples per entry $samplesPerEntry samples per sec $samplesPerSec")
    val r = new Random(seed)
    for (_ <- 0 until count) yield {
      val note = Note(4, r.nextInt(12))

      val offset:Double = r.nextDouble()
      val func: Int => Double = x => periodicFunction.apply(offset + x * sampleDt * note.freq)

      (for (i <- 0 until samplesPerEntry) yield func(i)) :+ note.toneNumber
    }
  }

  def storeToCsV(f:File, data:Seq[Seq[Any]]) : Unit = {
    val toFile = new PrintWriter(f)
   // toFile.println(argAndResNames.mkString(","))
    data.foreach(d => toFile.println(d.mkString(",")))

    toFile.close()
  }
  val notesFilename = "notes.csv"
  def main(args: Array[String]): Unit = {
    storeToCsV(new File(notesFilename), generate())
  }


}
