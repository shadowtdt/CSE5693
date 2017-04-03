package com.ttoggweiler.cse5693;

import com.codahale.metrics.MetricRegistry;
import com.ttoggweiler.cse5693.feature.DataLoader;
import com.ttoggweiler.cse5693.feature.FeatureLoader;
import com.ttoggweiler.cse5693.feature.Parser;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by ttoggweiler on 3/7/17.
 */
public class GeneticRunner
{
    public static final String INPUT_TENNIS = "tennis";
    public static final String INPUT_IRIS = "iris";
    public static final String INPUT_IDENTITY = "identity";
    public static final String INPUT_BOOL = "bool";
    public static final String INPUT_IRIS_NOISY = "iris-noisy";

//    public static final double DEFAULT_MOMENTUM = 0.9;
//    public static final int DEFAULT_ITERATIONS = 3000;
//    public static final int DEFAULT_HIDDEN_LAYER_COUNT = 1;
//    public static final int DEFAULT_HIDDEN_NODE_COUNT = 4;
//    public static final double DEFAULT_LEARNING_RATE = 0.1;

    private static Logger log = LoggerFactory.getLogger(GeneticRunner.class);
    private static final MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) throws Exception
    {
        String inputType = null;
        String featureFilePath = null;
        String dataFilePath = null;
        String testFilePath = null;

//        double momentum = DEFAULT_MOMENTUM;
//        double learningRate = DEFAULT_LEARNING_RATE;
//        int iterations = DEFAULT_ITERATIONS;
//        int hiddenLayers = DEFAULT_HIDDEN_LAYER_COUNT;
//        int hiddenNodes = DEFAULT_HIDDEN_NODE_COUNT;

        log.debug("Args: {}", Arrays.toString(args));

        for (String arg : args) {
            if (!arg.contains("=")) continue;
            String[] keyValue = arg.split("=");
            switch (keyValue[0].trim().toLowerCase()) {
                case "-afile":
                    featureFilePath = keyValue[1];
                    log.info("Set attribute file to {}",featureFilePath);
                    break;
                case "-tfile":
                    dataFilePath = keyValue[1];
                    log.info("Set training file to {}",dataFilePath);
                    break;
                case "-vfile":
                    testFilePath = keyValue[1];
                    log.info("Set validation file to {}",testFilePath);
                    break;
//                case "-momentum":
//                    momentum = Parser.toDouble(keyValue[1]).orElse(DEFAULT_MOMENTUM);
//                    log.info("Set momentum to {}",momentum);
//                    break;
//                case "-iterations":
//                    iterations = Parser.toInteger(keyValue[1]).orElse(DEFAULT_ITERATIONS);
//                    log.info("Set iterations to {}",iterations);
//                    break;
//                case "-learnrate":
//                    learningRate = Parser.toDouble(keyValue[1]).orElse(DEFAULT_LEARNING_RATE);
//                    log.info("Set learning rate to {}",learningRate);
//                    break;
//                case "-hlayers":
//                    hiddenLayers = Parser.toInteger(keyValue[1]).orElse(DEFAULT_HIDDEN_LAYER_COUNT);
//                    log.info("Set hidden layers to to {}",hiddenLayers);
//                    break;
//                case "-hnodes":
//                    hiddenNodes = Parser.toInteger(keyValue[1]).orElse(DEFAULT_HIDDEN_NODE_COUNT);
//                    log.info("Set hidden nodes to to {}",hiddenNodes);
//                    break;
                case "-input":
                    inputType = keyValue[1];
                    log.info("Set input files to {}",inputType);
                    break;
                default:
                    log.warn("Unrecognized argument: {} , Ignoring...", arg);
            }
        }

        if (PreCheck.isEmpty(featureFilePath, dataFilePath, testFilePath) && PreCheck.isEmpty(inputType))
            inputType = INPUT_IRIS;

        if (!PreCheck.isEmpty(inputType)) {
            switch (inputType) {
                case INPUT_TENNIS:
                    featureFilePath = "/inputFiles/tennis-attr.txt";
                    dataFilePath = "/inputFiles/tennis-train.txt";
                    testFilePath = "/inputFiles/tennis-test.txt";
                    break;
                case INPUT_IRIS:
                    featureFilePath = "/inputFiles/iris-attr.txt";
                    dataFilePath = "/inputFiles/iris-train.txt";
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
                default:
                    throw new IllegalArgumentException("Invalid input type: " + inputType);
            }
        }

        if(PreCheck.isEmpty(testFilePath))testFilePath = dataFilePath;
        log.info("=== CSE5693-HW3 Artificial-Neural-Network Runner ====");
        log.info("Author: Troy Toggweiler");
        log.info("Date: 3/15/2017 ");


        log.info("\n\n===  FILES  ===");
        log.info("Loading feature file: {}", featureFilePath);
        log.info("Loading data file: {}", dataFilePath);
        log.info("Loading validation file: {}", testFilePath);

        FeatureLoader featLoader = new FeatureLoader(featureFilePath);

        List<Map<String, Comparable>> trainingDatas = DataLoader.loadDataFromFile(dataFilePath, featLoader.getAllFeatures());
        List<Map<String, Comparable>> validationDatas = DataLoader.loadDataFromFile(testFilePath, featLoader.getAllFeatures());

        List<Map<String, Comparable>> allData = new ArrayList<>(trainingDatas);
        allData.addAll(validationDatas);

    }
}
