package com.ttoggweiler.cse5693;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.ttoggweiler.cse5693.ann.Network;
import com.ttoggweiler.cse5693.explore.Evaluation;
import com.ttoggweiler.cse5693.data.DataSet;
import com.ttoggweiler.cse5693.feature.FeatureLoader;
import com.ttoggweiler.cse5693.predict.Classifier;
import com.ttoggweiler.cse5693.util.CollectionUtil;
import com.ttoggweiler.cse5693.util.JChartUtil.JCGrapher;
import com.ttoggweiler.cse5693.util.ValueParser;
import com.ttoggweiler.cse5693.predict.Performance;
import com.ttoggweiler.cse5693.tree.ID3Tree;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class termProjectRunner
{
    // Input modes
    private static final String INPUT_TENNIS = "tennis";
    private static final String INPUT_IRIS = "iris";
    private static final String INPUT_SOY_LG = "soy-lg";
    private static final String INPUT_SOY_SM = "soy-sm";
    private static final String INPUT_THYROID = "thyroid";
    private static final String INPUT_IDENTITY = "identity";
    private static final String INPUT_BOOL = "bool";
    private static final String INPUT_IRIS_NOISY = "iris-noisy";

    // Default config
    private static final int DEFAULT_TREE_DEPTH_LIMIT = 10;
    private static final int DEFAULT_ITERATIONS = 500;
    private static final double DEFAULT_LEARNING_RATE = 0.01;
    private static final double DEFAULT_MOMENTUM_RATE = 0.9;
    private static final double DEFAULT_NETWORK_ACCURACY_THRESHOLD = 0.99d;
    private static final int DEFAULT_HIDDEN_LAYER_NODE_COUNT = 8;
    private static final int DEFAULT_HIDDEN_LAYER_COUNT = 1;
    private static final int[] DEFAULT_NETWORK_LAYER_SIZES = CollectionUtil.fill(DEFAULT_HIDDEN_LAYER_COUNT, DEFAULT_HIDDEN_LAYER_NODE_COUNT);

    // Names
    public static final String TREENET_NAME = "TreeNet";
    public static final String ANN_NAME = "BP";
    public static final String TREE_NAME = "ID3";
    public static final String DEFAULT_BASE_RESULT_DIR = "./results/";
    public static final String RESULT_DIR = DEFAULT_BASE_RESULT_DIR;// + LocalDateTime.now().toString() + "/";

    // TreeNet Vars
    private static int treeNetDepth = DEFAULT_TREE_DEPTH_LIMIT;
    // ANN Vars
    private static int iterations = DEFAULT_ITERATIONS;
    private static double momentum = DEFAULT_MOMENTUM_RATE;
    private static double learningRate = DEFAULT_LEARNING_RATE;
    private static double accuracyThreshold = DEFAULT_NETWORK_ACCURACY_THRESHOLD;
    private static int[] hlSizes = DEFAULT_NETWORK_LAYER_SIZES;

    private static String inputType = null;
    private static String featureFilePath = null;
    private static String dataFilePath = null;
    private static String testFilePath = null;
    private static String script = "";

    private static Logger log = LoggerFactory.getLogger(termProjectRunner.class);
    private static final MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) throws Exception
    {

        handleArgs(args);
        log.info("=== CSE5693 TermProject TreeNet-ID3BP Runner ====");
        log.info("Author: Troy Toggweiler");
        log.info("Date: 4/26/2017 ");

        log.info("\n\n===  Files  ===");
        log.info("Loading feature file: {}", featureFilePath);
        log.info("Loading data file: {}", dataFilePath);
        log.info("Loading validation file: {}", testFilePath);

        // Load Features
        FeatureLoader featLoader = new FeatureLoader(featureFilePath);
        log.info("\n\n===  Features ===");
        featLoader.getArgumentFeatures().forEach(f -> log.info("{}", f.describe(false)));
        featLoader.getTargetFeatures().forEach(f -> log.info("{}", f.describe(false)));

        // Load Data
        List<Map<String, Comparable>> trainingDatas = DataSet.loadDataFromFile(dataFilePath, featLoader.getAllFeatures());
        List<Map<String, Comparable>> validationDatas = DataSet.loadDataFromFile(testFilePath, featLoader.getAllFeatures());

        DataSet trainDS = new DataSet(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), trainingDatas);
        trainDS.setName("TrainingData");
        DataSet validDS = new DataSet(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), validationDatas);
        validDS.setName("ValidationData");
        List<Map<String, Comparable>> allData = new ArrayList<>(trainingDatas);
        allData.addAll(validationDatas);
        DataSet allDS = new DataSet(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), validationDatas);
        allDS.setName("AllData");

        log.info("\n\n===  Data ===");
        log.info("{}", trainDS.describe(false));
        log.info("{}", validDS.describe(false));

        // Experimentation Vars

        trainDS = trainDS.subsetWithSize(300); // Note: 500~60s
        validDS = validDS.subsetWithSize(300);
        treeNetDepth = 99;
        iterations = 500;
        momentum = 0.9d;
        learningRate = 0.2d;
        accuracyThreshold = 0.99d;
        int nodeCount = 15;
        int layerCount = 1;
        hlSizes = CollectionUtil.fill(layerCount, nodeCount);

//        log.info("\n\n== Evaluation ==");
//        Evaluation base = Eval(trainDS, validDS);

        // Run Experiments
        log.info("\n\n===  Experiments ===");
        Timer.Context timer = metrics.timer("Eval").time();
      //  evalExampleSize(trainDS, validDS,  2, 3, 4, 5, 6, 8, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, trainDS.size());
        evalExampleSize(trainDS, validDS,   5, 15, 30, 50,  75, 100, trainDS.size());

     //   evalIterations(trainDS, validDS, 1, 2, 3, 4, 5, 6, 8, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100,110,120,130,140,150, 175,200);
       // evalNetHiddenLayerCount(trainDS, validDS, nodeCount, 1, 2, 3);
     //   evalNetHiddenNodeCount(trainDS, validDS, layerCount, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,15);

       // evalTreeSize(trainDS, validDS, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        evalNoisePercent(trainDS, validDS, .05f, .1f, 0.15f, .2f, .25f); // fixme corupting data?++
       // evalNoisePercent(trainDS, validDS, 0.01f, .02f,.03f, .04f, .05f, .08f, .1f, .12f, 0.15f, .18f, .2f, .22f, .25f); // fixme corupting data?++

        log.info("== Experimentation Duration:{} min ==" , (timer.stop() / 1000000000d)/60);
    }

    // Experimentation
    public static Evaluation Eval(DataSet train, DataSet valid)
    {
        ID3Tree tree = defaultTree(train);
        Network ann = defaultAnn(train);
        TreeNet treeNet = defaultTreeNet(train);
        return new Evaluation(train, valid, tree, ann, treeNet);
    }

    public static void evalNoisePercent(DataSet trainDS, DataSet validDS, float... noisyPercent) throws IOException
    {
        Map<String, XYSeries> noisyAccuracy = new HashMap<>();
        Map<String, XYSeries> noisyValidAccuracy = new HashMap<>();
        for (int i = 0; i < noisyPercent.length; i++) {
            log.info("\n\n== Noisy Evaluation:{}% ==", noisyPercent[i] * 100);
            Evaluation percentEval = Eval(trainDS.noisySubset(noisyPercent[i]), validDS);
            recordAccuracyResult(noisyPercent[i] * 100, percentEval, noisyAccuracy, noisyValidAccuracy);
        }
        chart("noiseChart.png", "Noisy Accuracy", "% Examples Corruption", "Accuracy", noisyAccuracy);
        chart("noiseChartValid.png", "Noisy Accuracy on Validation set", "% Examples Corruption", "Accuracy", noisyValidAccuracy);
    }

    public static void evalExampleSize(DataSet trainDS, DataSet validDS, int... exampleSizes) throws IOException
    {
        Map<String, XYSeries> sizeAccuracy = new HashMap<>();
        Map<String, XYSeries> sizeValidAccuracy = new HashMap<>();
        for (int i = 0; i < exampleSizes.length; i++) {
            log.info("\n\n== Evaluation Example Size:{} ==", exampleSizes[i]);
            DataSet subset = trainDS.subsetWithSize(exampleSizes[i]);
            Evaluation sizeEval = Eval(subset, validDS);
            recordAccuracyResult(exampleSizes[i], sizeEval, sizeAccuracy, sizeValidAccuracy);
        }
        chart("sizeChart.png", "Accuracy vs Training Examples", "Number of training examples", "Accuracy", sizeAccuracy);
        chart("sizeChartValid.png", "Accuracy vs Training Examples on Validation Set", "Number of training examples", "Accuracy", sizeValidAccuracy);
    }

    // Tree Experiments
    public static void evalTreeSize(DataSet trainDS, DataSet validDS, int... treeSize) throws IOException
    {
        Map<String, XYSeries> treeSizeAccuracy = new HashMap<>();
        Map<String, XYSeries> treeSizeValidAccuracy = new HashMap<>();
        for (int i = 0; i < treeSize.length; i++) {
            log.info("\n\n== Evaluation Tree Depth:{} ==", treeSize[i]);
            TreeNet treeNet = new TreeNet();
            treeNet.setName(TREENET_NAME);
            treeNet.setTreeVars(treeSize[i]);
            treeNet.setNetVars(
                    iterations,
                    learningRate,
                    momentum,
                    accuracyThreshold,
                    hlSizes);

            ID3Tree tree = new ID3Tree(trainDS.getTargets().get(0), treeSize[i]);
            tree.setName(TREE_NAME);

            Network ann = defaultAnn(trainDS);

            Evaluation sizeEval = new Evaluation(trainDS, validDS, ann, tree, treeNet);
            recordAccuracyResult(treeSize[i], sizeEval, treeSizeAccuracy, treeSizeValidAccuracy);
        }

        chart("treeSizeChart.png", "Accuracy vs Tree Depth", "Allowable Depth", "Accuracy", treeSizeAccuracy);
        chart("treeSizeChartValid.png", "Accuracy vs Tree Depth on Validation Set", "Allowable Depth", "Accuracy", treeSizeValidAccuracy);
    }

    // Net Experiments
    public static void evalIterations(DataSet trainDS, DataSet validDS, int... networkIterations) throws IOException
    {
        Map<String, XYSeries> accuracy = new HashMap<>();
        Map<String, XYSeries> validationAccuracy = new HashMap<>();
        for (int i = 0; i < networkIterations.length; i++) {
            log.info("\n\n== Evaluation Network Iterations:{} ==", networkIterations[i]);
            TreeNet treeNet = new TreeNet();
            treeNet.setName(TREENET_NAME);
            treeNet.setTreeVars(treeNetDepth);
            treeNet.setNetVars(
                    networkIterations[i],
                    learningRate,
                    momentum,
                    accuracyThreshold,
                    hlSizes);

            Network ann = new Network(
                    trainDS.getTargets(),
                    networkIterations[i],
                    learningRate,
                    momentum,
                    accuracyThreshold,
                    hlSizes);
            ann.setName(ANN_NAME);

            ID3Tree tree = defaultTree(trainDS);
            Evaluation sizeEval = new Evaluation(trainDS, validDS, ann, tree, treeNet);
            recordAccuracyResult(networkIterations[i], sizeEval, accuracy, validationAccuracy);
        }

        chart("netIterationsChart.png", "Accuracy vs Back Propagation Iterations", "Iterations", "Accuracy", accuracy);
        chart("netIterationsChartValid.png", "Accuracy vs Back Propagation Iterations on Validation Set", "Iterations", "Accuracy", validationAccuracy);
    }

    public static void evalNetHiddenLayerCount(DataSet trainDS, DataSet validDS, int nodeCount, int... hiddenLayers) throws IOException
    {
        Map<String, XYSeries> accuracy = new HashMap<>();
        Map<String, XYSeries> validationAccuracy = new HashMap<>();
        for (int i = 0; i < hiddenLayers.length; i++) {
            log.info("\n\n== Evaluation Network Hidden Layers:{} ==", hiddenLayers[i]);
            TreeNet treeNet = new TreeNet();
            treeNet.setName(TREENET_NAME);
            treeNet.setTreeVars(treeNetDepth);
            treeNet.setNetVars(
                    iterations,
                    learningRate,
                    momentum,
                    accuracyThreshold,
                    CollectionUtil.fill(hiddenLayers[i], nodeCount));

            Network ann = new Network(
                    trainDS.getTargets(),
                    iterations,
                    learningRate,
                    momentum,
                    accuracyThreshold,
                    CollectionUtil.fill(hiddenLayers[i], nodeCount));
            ann.setName(ANN_NAME);

            ID3Tree tree = defaultTree(trainDS);

            Evaluation eval = new Evaluation(trainDS, validDS, ann, tree, treeNet);
            recordAccuracyResult(hiddenLayers[i], eval, accuracy, validationAccuracy);
        }

        chart("netLayerCountChart.png", "Accuracy vs Number of Hidden Layers", "Number of Hidden Layers", "Accuracy", accuracy);
        chart("netLayerCountChartValid.png", "Accuracy vs Number of Hidden Layers on Validation Set", "Number of Hidden Layers", "Accuracy", validationAccuracy);
    }

    public static void evalNetHiddenNodeCount(DataSet trainDS, DataSet validDS, int layerCount, int... hiddenNodes) throws IOException
    {
        Map<String, XYSeries> accuracy = new HashMap<>();
        Map<String, XYSeries> validationAccuracy = new HashMap<>();
        for (int i = 0; i < hiddenNodes.length; i++) {
            log.info("\n\n== Evaluation Network Hidden Nodes:{} ==", hiddenNodes[i]);
            TreeNet treeNet = new TreeNet();
            treeNet.setName(TREENET_NAME);
            treeNet.setTreeVars(treeNetDepth);
            treeNet.setNetVars(
                    iterations,
                    learningRate,
                    momentum,
                    accuracyThreshold,
                    CollectionUtil.fill(layerCount, hiddenNodes[i]));

            Network ann = new Network(
                    trainDS.getTargets(),
                    iterations,
                    learningRate,
                    momentum,
                    accuracyThreshold,
                    CollectionUtil.fill(layerCount, hiddenNodes[i]));
            ann.setName(ANN_NAME);

            ID3Tree tree = defaultTree(trainDS);

            Evaluation eval = new Evaluation(trainDS, validDS, ann, tree, treeNet);
            recordAccuracyResult(hiddenNodes[i], eval, accuracy, validationAccuracy);
        }

        chart("netNodeCountChart.png", "Accuracy vs Number of Hidden Nodes", "Number of Nodes in Hidden Layers", "Accuracy", accuracy);
        chart("netNodeCountChartValid.png", "Accuracy vs Number of Hidden Nodes on Validation Set", "Number of Nodes in Hidden Layers", "Accuracy", validationAccuracy);
    }

    // Results and charts
    public static void recordAccuracyResult(double xVal, Evaluation result, Map<String, XYSeries> record, Map<String, XYSeries> recordValid)
    {
        for (Map.Entry<Classifier, Performance> eval : result.classifierPerformanceMap.entrySet()) {
            String key = eval.getKey().name();
            Performance performance = eval.getValue();
            if (!record.containsKey(key))
                record.put(key, new XYSeries(key));
            record.get(eval.getKey().name()).add(xVal, performance.getAccuracy() * 100);
        }
        for (Map.Entry<Classifier, Performance> eval : result.classifierValidPerformanceMap.entrySet()) {
            String key = eval.getKey().name();
            Performance performance = eval.getValue();
            if (!recordValid.containsKey(key))
                recordValid.put(key, new XYSeries(key));
            recordValid.get(eval.getKey().name()).add(xVal, performance.getAccuracy() * 100);
        }
    }

    public static void chart(String filename, String title, String xaxis, String yaxis, Map<String, XYSeries> data) throws IOException
    {
        XYSeriesCollection dataCollection = new XYSeriesCollection();
        data.values().forEach(dataCollection::addSeries);
        JFreeChart chart = ChartFactory.createScatterPlot(title, xaxis, yaxis, dataCollection);

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        //setContentPane( chartPanel );
        JCGrapher.saveChart(RESULT_DIR + filename, chart, 500, 500);
    }

    // Simple runs
    public static TreeNet defaultTreeNet(DataSet train)
    {
        TreeNet treeNet = new TreeNet();
        treeNet.setName(TREENET_NAME);
        treeNet.setTreeVars(treeNetDepth);
        treeNet.setNetVars(
                iterations,
                learningRate,
                momentum,
                accuracyThreshold,
                hlSizes
        );
        return treeNet;
    }

    public static ID3Tree defaultTree(DataSet train)
    {
        ID3Tree tree = new ID3Tree(train.getTargets().get(0), 999);
        tree.setName(TREE_NAME);
        return tree;
    }

    public static Network defaultAnn(DataSet train)
    {
        Network ann = new Network(
                train.getTargets(),
                iterations,
                learningRate,
                momentum,
                accuracyThreshold,
                hlSizes);
        ann.setName(ANN_NAME);
        return ann;
    }

    public static TreeNet runTreeNet(DataSet train)
    {
        TreeNet treeNet = defaultTreeNet(train);
        return treeNet;
    }

    public static ID3Tree runTree(DataSet train)
    {
        ID3Tree tree = defaultTree(train);
        log.info("== Train Tree[] ==", treeNetDepth);
        tree.train(train);
        log.info("== Tree Topology ==");
        log.info(tree.getClassifierString(false));
        return tree;
    }

    public static Network runAnn(DataSet train)
    {
        Network ann = defaultAnn(train);
        log.info("== Train ANN ==");
        ann.train(train);
        log.info("== ANN Topology ==");
        log.info(ann.getTopologyString());
        return ann;
    }

    public static void testAndPrintPerformance(Classifier classifier, DataSet data)
    {
        Performance p = new Performance(classifier, data);
        log.info("{}\ton {}: {}", classifier.name(), data.name(), p.getPerformanceString(true, true, false));
    }

    public static void handleArgs(String[] args)
    {
        log.debug("Args: {}", Arrays.toString(args));

        for (String arg : args) {
            if (!arg.contains("=")) continue;
            String[] keyValue = arg.split("=");
            switch (keyValue[0].trim().toLowerCase()) {
                case "-afile":
                    featureFilePath = keyValue[1];
                    log.info("Set attribute file to {}", featureFilePath);
                    break;
                case "-tfile":
                    dataFilePath = keyValue[1];
                    log.info("Set training file to {}", dataFilePath);
                    break;
                case "-vfile":
                    testFilePath = keyValue[1];
                    log.info("Set validation file to {}", testFilePath);
                    break;
                case "-learningrate":
                    learningRate = ValueParser.toDouble(keyValue[1]).orElse(DEFAULT_LEARNING_RATE);
                    log.info("Set learning rate to {}", learningRate);
                    break;
                case "-iterations":
                    iterations = ValueParser.toInteger(keyValue[1]).orElse(DEFAULT_ITERATIONS);
                    log.info("Set population size to {}", iterations);
                    break;
                case "-momentum":
                    momentum = ValueParser.toDouble(keyValue[1]).orElse(DEFAULT_MOMENTUM_RATE);
                    log.info("Set replacement rate to {}", momentum);
                    break;
                case "-treedepth":
                    treeNetDepth = ValueParser.toInteger(keyValue[1]).orElse(DEFAULT_TREE_DEPTH_LIMIT);
                    log.info("Set tree treeNetDepth to {}", treeNetDepth);
                    break;
                case "-input":
                    inputType = keyValue[1];
                    log.info("Set input files to {}", inputType);
                    break;
                case "-script":
                    script = keyValue[1];
                    log.info("Set script to {}", inputType);
                    break;
                default:
                    log.warn("Unrecognized argument: {} , Ignoring...", arg);
            }
        }

        if (PreCheck.isEmpty(featureFilePath, dataFilePath, testFilePath) && PreCheck.isEmpty(inputType))
            inputType = INPUT_SOY_LG;

        if (!PreCheck.isEmpty(inputType)) {
            switch (inputType) {
                case INPUT_TENNIS:
                    featureFilePath = "/inputFiles/tennis-attr.txt";
                    dataFilePath = "/inputFiles/tennis-train.txt";
                    testFilePath = "/inputFiles/tennis-test.txt";
                    break;
                case INPUT_IRIS:
                    featureFilePath = "/inputFiles/iris-attr.txt";
                    dataFilePath = "/inputFiles/iris/iris-train.txt";
                    testFilePath = "/inputFiles/iris-test.txt";
                    break;
                case INPUT_IDENTITY:
                    featureFilePath = "/inputFiles/identity-attr.txt";
                    dataFilePath = "/inputFiles/identity-train.txt";
                    testFilePath = dataFilePath;
                    break;
                case INPUT_BOOL:
                    featureFilePath = "/inputFiles/bool-attr.txt";
                    dataFilePath = "/inputFiles/bool-train.txt";
                    testFilePath = "/inputFiles/bool-test.txt";
                    break;
                case INPUT_IRIS_NOISY:
                    featureFilePath = "/inputFiles/iris-attr.txt";
                    dataFilePath = "/inputFiles/iris-trainNoisy.txt";
                    testFilePath = "/inputFiles/iris-test.txt";
                    break;
                case INPUT_SOY_LG:
                    featureFilePath = "/inputFiles/soybean/soybean-lg-attr.txt";
                    dataFilePath = "/inputFiles/soybean/soybean-lg-train.txt";
                    testFilePath = "/inputFiles/soybean/soybean-lg-test.txt";
                    break;
                case INPUT_SOY_SM:
                    featureFilePath = "/inputFiles/soybean/soybean-sm-attr.txt";
                    dataFilePath = "/inputFiles/soybean/soybean-sm-train.txt";
                    testFilePath = "/inputFiles/soybean/soybean-sm-test.txt";
                    break;
                case INPUT_THYROID:
                    featureFilePath = "/inputFiles/thyroid/thyroid-attr.txt";
                    dataFilePath = "/inputFiles/thyroid/thyroid-train.txt";
                    testFilePath = "/inputFiles/thyroid/thyroid-test.txt";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid input type: " + inputType);
            }
        }
        if (PreCheck.isEmpty(testFilePath)) testFilePath = dataFilePath;

    }

}
