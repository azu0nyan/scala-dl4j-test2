package MathTest

import java.io.{File, PrintWriter}

import scala.util.Random

object Generator {
  val count = 10000
  val seed = 123
  val numArguments = 5
  val numResults = 1

  def func(a:Seq[Double]):Int = {


    /*val res0 = a.min
    val res1 = a.max
    val res2 = a.sum / a.length
    val res3 = x0 * x1 * x2 * x4 * x6
    val res4 = ((x0 + x1) / 2) *  ((x1 + x2 + x4) / 3)

    val res =  Seq(res0, res1, res2, res3, res4)
    res.foreach(v => assert(v >= 0))
    res.foreach(v => assert(v <= 1))
    return res*/
    return a.sum.toInt
  }

  def generate():Seq[Seq[Any]] = {
    val r = new Random(seed)
    for(i <- 0 until count) yield {
      val data:Seq[Double] = for(i <- 0 until numArguments) yield r.nextDouble()
      data :+ func(data)
    }
  }

  val argAndResNames:Seq[String] = (for(i <- 0 until numArguments) yield "a" +i) ++
    (for(i <- 0 until numResults) yield "r" + i)

  def storeToCsV(f:File, data:Seq[Seq[Any]]) : Unit = {
    val toFile = new PrintWriter(f)
    toFile.println(argAndResNames.mkString(","))
    data.foreach(d => toFile.println(d.mkString(",")))

    toFile.close()
  }

  def main(args: Array[String]): Unit = {
    storeToCsV(new File("mathcsv.csv"), generate())
  }




}
