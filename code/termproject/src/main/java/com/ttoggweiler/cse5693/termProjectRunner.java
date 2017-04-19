package com.ttoggweiler.cse5693;

import com.codahale.metrics.MetricRegistry;
import com.ttoggweiler.cse5693.ann.Network;
import com.ttoggweiler.cse5693.feature.DataLoader;
import com.ttoggweiler.cse5693.feature.DataSet;
import com.ttoggweiler.cse5693.feature.FeatureLoader;
import com.ttoggweiler.cse5693.feature.Parser;
import com.ttoggweiler.cse5693.predict.Performance;
import com.ttoggweiler.cse5693.tree.ID3Tree;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by ttoggweiler on 4/16/17.
 */
public class termProjectRunner
{

    public static final String INPUT_TENNIS = "tennis";
    public static final String INPUT_IRIS = "iris";
    public static final String INPUT_SOY_LG = "soy-lg";
    public static final String INPUT_SOY_SM = "soy-sm";
    public static final String INPUT_THYROID = "thyroid";
    public static final String INPUT_IDENTITY = "identity";
    public static final String INPUT_BOOL = "bool";
    public static final String INPUT_IRIS_NOISY = "iris-noisy";

    public static final double DEFAULT_MUTATION_RATE = 0.01;
    public static final int DEFAULT_POPULATION = 1000;
    public static final int DEFAULT_MAX_GENERATIONS = 1000;
    public static final double DEFAULT_REPLACEMENT_RATE = 0.6;
    public static final double DEFAULT_FITNESS_THRESHOLD = 0.9;
    public static final String DEFAULT_SELECTION = "rank";

    private static Logger log = LoggerFactory.getLogger(termProjectRunner.class);
    private static final MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) throws Exception
    {
        String inputType = null;
        String featureFilePath = null;
        String dataFilePath = null;
        String testFilePath = null;

        double mutationRate = DEFAULT_MUTATION_RATE;
        double replacementRate = DEFAULT_REPLACEMENT_RATE;
        double fitnessThreshold = DEFAULT_FITNESS_THRESHOLD;
        int populationSize = DEFAULT_POPULATION;
        int maxGenerations = DEFAULT_MAX_GENERATIONS;
        String selectionMethod = DEFAULT_SELECTION;
        String script = "";

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
                case "-mutationRate":
                    mutationRate = Parser.toDouble(keyValue[1]).orElse(DEFAULT_MUTATION_RATE);
                    log.info("Set mutation rate to {}", mutationRate);
                    break;
                case "-population":
                    populationSize = Parser.toInteger(keyValue[1]).orElse(DEFAULT_POPULATION);
                    log.info("Set population size to {}", populationSize);
                    break;
                case "-replaceRate":
                    replacementRate = Parser.toDouble(keyValue[1]).orElse(DEFAULT_REPLACEMENT_RATE);
                    log.info("Set replacement rate to {}", replacementRate);
                    break;
                case "-fitThreshold":
                    fitnessThreshold = Parser.toDouble(keyValue[1]).orElse(DEFAULT_REPLACEMENT_RATE);
                    log.info("Set fitness threshold to {}", fitnessThreshold);
                    break;
                case "-generations":
                    maxGenerations = Parser.toInteger(keyValue[1]).orElse(DEFAULT_MAX_GENERATIONS);
                    log.info("Set max generations to {}", maxGenerations);
                    break;
                case "-selection":
                    selectionMethod = keyValue[1];
                    log.info("Set selection method to {}", selectionMethod);
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
                    featureFilePath = "/inputFiles/soybean/soybean-attr.txt";
                    dataFilePath = "/inputFiles/soybean/soybean-lg-train.txt";
                    testFilePath = "/inputFiles/soybean/soybean-lg-test.txt";
                    break;
                case INPUT_SOY_SM:
                    featureFilePath = "/inputFiles/soybean/soybean-attr.txt";
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
        log.info("=== CSE5693-HW4 Genetic-Algorithm Runner ====");
        log.info("Author: Troy Toggweiler");
        log.info("Date: 4/4/2017 ");


        log.info("\n\n===  FILES  ===");
        log.info("Loading feature file: {}", featureFilePath);
        log.info("Loading data file: {}", dataFilePath);
        log.info("Loading validation file: {}", testFilePath);

        // Load features and data
        FeatureLoader featLoader = new FeatureLoader(featureFilePath);
        List<Map<String, Comparable>> trainingDatas = DataLoader.loadDataFromFile(dataFilePath, featLoader.getAllFeatures());
        List<Map<String, Comparable>> validationDatas = DataLoader.loadDataFromFile(testFilePath, featLoader.getAllFeatures());

        DataSet ds = new DataSet(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), trainingDatas);
        DataSet ds_validation = new DataSet(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), validationDatas);

        List<Map<String, Comparable>> allData = new ArrayList<>(trainingDatas);
        allData.addAll(validationDatas);

        ID3Tree tree = new ID3Tree(featLoader.getTargetFeatures().get(0), ds);
        log.info(tree.getClassifierString(false));

        Performance p = new Performance(tree, ds.getData());
        Performance pv = new Performance(tree, ds_validation.getData());

        log.info("Tree Depth: {}",tree.getRootNode().getMaxDepth());
        log.info("Training: {}", p.getPerformanceString(true, true, false));
        log.info("Validation: {}", pv.getPerformanceString(true, true, false));

        log.info(" -- Depth Constrained");
        for (int i = 0; i < tree.getRootNode().getMaxDepth() + 1; i++) {
            log.info("Depth: {}",i);
            tree.setPredictionDepth(i);
            p = new Performance(tree, ds.getData());
            pv = new Performance(tree, ds_validation.getData());
            log.info("Training: {}", p.getPerformanceString(true, true, false));
            log.info("Validation: {}", pv.getPerformanceString(true, true, false));
        }



        Network ann = new Network(0.1, .85, 1000, featLoader.getArgumentFeatures(), featLoader.getTargetFeatures()
                , 9,9,9,9,9,9);
        ann.setName(inputType);

        log.info("== ANN Topology ==");
        log.info(ann.getTopologyString());

        log.info("== Train ANN ==");
        ann.train(trainingDatas);

        log.info("== Test ANN ==");
        Double percentCorrect = ann.getAccuracy(validationDatas);
        log.info("Classified {}% correct ", percentCorrect * 100);

    }
}
