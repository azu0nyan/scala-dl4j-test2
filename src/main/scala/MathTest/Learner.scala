package MathTest

import java.io.File

import org.datavec.api.records.reader.RecordReader
import org.datavec.api.records.reader.impl.csv.CSVRecordReader
import org.datavec.api.split.FileSplit
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.evaluation.classification.Evaluation
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.SplitTestAndTrain
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Sgd
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Learner extends App{

  val log = LoggerFactory.getLogger("math learner");
  //First: get the dataset using the record reader. CSVRecordReader handles loading/parsing
  val numLinesToSkip = 1;
  val  delimiter = ',';
  val  recordReader:RecordReader = new CSVRecordReader(numLinesToSkip,delimiter);
  recordReader.initialize(new FileSplit(new File("mathcsv.csv")));

  //Second: the RecordReaderDataSetIterator handles conversion to DataSet objects, ready for use in neural network
  val labelIndex = 5;
  val numClasses = 4;
  val batchSize = 150;

  val iterator:DataSetIterator  = new RecordReaderDataSetIterator(recordReader,batchSize,labelIndex,numClasses);
  val allData:DataSet = iterator.next();
  allData.shuffle();
  val testAndTrain:SplitTestAndTrain = allData.splitTestAndTrain(0.65);  //Use 65% of data for training

  val trainingData:DataSet = testAndTrain.getTrain();
  val  testData:DataSet = testAndTrain.getTest();

  //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
 // DataNormalization normalizer = new NormalizerStandardize();
  //normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
  //normalizer.transform(trainingData);     //Apply normalization to the training data
  //normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set


  val numInputs = 5;
  val outputNum = 4;
  val  seed = 6;


  log.info("Build model....");
   val conf:MultiLayerConfiguration = new NeuralNetConfiguration.Builder()
    .seed(seed)
    .activation(Activation.TANH)
    .weightInit(WeightInit.XAVIER)
    .updater(new Sgd(0.1))
    .l2(1e-4)
    .list()
    .layer(new DenseLayer.Builder().nIn(numInputs).nOut(3)
      .build())
    .layer(new DenseLayer.Builder().nIn(3).nOut(3)
      .build())
    .layer( new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
      .activation(Activation.SOFTMAX)
      .nIn(3).nOut(outputNum).build())
    .build();

  //run the model
  val  model:MultiLayerNetwork = new MultiLayerNetwork(conf);
  model.init();
  model.setListeners(new ScoreIterationListener(100));

  for(i<- 0 until 1000) {
    model.fit(trainingData)
  };


  //evaluate the model on the test set
  val eval:Evaluation = new Evaluation(4);
  val  output:INDArray = model.output(testData.getFeatures());
  eval.eval(testData.getLabels(), output);
  log.info(eval.stats());
//  println(output)

  println("--------------------------------------")
  println("--------------------------------------")
  println("--------------------------------------")
  println("--------------------------------------")
  for(i <- 0 until output.rows()){
    var maxi = 0
    for(j<- 0 until 4) {
      if(output.getRow(i).getDouble(j.toLong) >output.getRow(i).getDouble(maxi.toLong)){
        maxi = j
      }
    }

    var sum = (for(k <- 0 until 5) yield testData.getFeatures.getRow(i).getDouble(k.toLong)).sum.toInt

    println(output.getRow(i) + " " + testData.getFeatures().getRow(i) + " " + maxi + " " + sum)
  }




}
