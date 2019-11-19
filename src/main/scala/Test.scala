import org.deeplearning4j.common.resources.{DL4JResources, ResourceType}

//import scala.jdk.CollectionConverters._
import org.deeplearning4j.datasets.iterator._
import org.deeplearning4j.datasets.iterator.impl._
import org.deeplearning4j.nn.api._
import org.deeplearning4j.nn.multilayer._
import org.deeplearning4j.nn.graph._
import org.deeplearning4j.nn.conf._
import org.deeplearning4j.nn.conf.inputs._
import org.deeplearning4j.nn.conf.layers._
import org.deeplearning4j.nn.weights._
import org.deeplearning4j.optimize.listeners._
import org.deeplearning4j.datasets.datavec.RecordReaderMultiDataSetIterator
import org.nd4j.evaluation.classification.{Evaluation, ROCMultiClass}
import org.nd4j.linalg.learning.config._
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.lossfunctions.LossFunctions // mean squared error, multiclass cross entropy, etc.

object Test extends App{



  //println(DL4JResources.getDirectory(ResourceType.DATASET, "MNIST").getAbsolutePath)


//create dataset iterator

  import org.deeplearning4j.datasets.iterator.impl.EmnistDataSetIterator

  val batchSize = 16 // how many examples to simultaneously train in the network
  val emnistSet = EmnistDataSetIterator.Set.BALANCED
  val emnistTrain = new EmnistDataSetIterator(emnistSet, batchSize, true)
  val emnistTest = new EmnistDataSetIterator(emnistSet, batchSize, false)


  val outputNum = EmnistDataSetIterator.numLabels(emnistSet) // total output classes
  val rngSeed = 123 // integer for reproducability of a random number generator
  val numRows = 28 // number of "pixel rows" in an mnist digit
  val numColumns = 28

  val conf = new NeuralNetConfiguration.Builder()
    .seed(rngSeed)
    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
    .updater(new Adam())
    .l2(1e-4)
    .list()
    .layer(new DenseLayer.Builder()
      .nIn(numRows * numColumns) // Number of input datapoints.
      .nOut(1000) // Number of output datapoints.
      .activation(Activation.RELU) // Activation function.
      .weightInit(WeightInit.XAVIER) // Weight initialization.
      .build())
    .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
      .nIn(1000)
      .nOut(outputNum)
      .activation(Activation.SOFTMAX)
      .weightInit(WeightInit.XAVIER)
      .build())
    .build()
    /*.pretrain(false).backprop(true)*/
    //.build()

  // create the MLN
  val network = new MultiLayerNetwork(conf)
  network.init()

  // pass a training listener that reports score every 10 iterations
  val eachIterations = 5
  network.addListeners(new ScoreIterationListener(eachIterations))

  // fit a dataset for a single epoch
  // network.fit(emnistTrain)

  // fit for multiple epochs
   val numEpochs = 1
   network.fit(new MultipleEpochsIterator(numEpochs, emnistTrain))


  // evaluate basic performance
  val eval:Evaluation = network.evaluate(emnistTest)

  eval.accuracy()
  eval.precision()
  eval.recall()

  // evaluate ROC and calculate the Area Under Curve
  val roc:ROCMultiClass = network.evaluateROCMultiClass(emnistTest)
  roc.calculateAverageAUC()

  val classIndex = 0
  roc.calculateAUC(classIndex)

  // optionally, you can print all stats from the evaluations
  print(eval.stats())
  print(roc.stats())
}
