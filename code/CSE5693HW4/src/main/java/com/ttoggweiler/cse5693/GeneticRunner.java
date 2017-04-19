package com.ttoggweiler.cse5693;

import com.codahale.metrics.MetricRegistry;
import com.ttoggweiler.cse5693.feature.DataLoader;
import com.ttoggweiler.cse5693.feature.FeatureLoader;
import com.ttoggweiler.cse5693.feature.Parser;
import com.ttoggweiler.cse5693.genetic.GeneticSearch;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessProportional;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessSelector;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessTournament;
import com.ttoggweiler.cse5693.genetic.fitness.selection.RankProportional;
import com.ttoggweiler.cse5693.rule.Hypothesis;
import com.ttoggweiler.cse5693.rule.Performance;
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

    public static final double DEFAULT_MUTATION_RATE = 0.01;
    public static final int DEFAULT_POPULATION = 1000;
    public static final int DEFAULT_MAX_GENERATIONS = 1000;
    public static final double DEFAULT_REPLACEMENT_RATE = 0.6;
    public static final double DEFAULT_FITNESS_THRESHOLD = 0.9;
    public static final String DEFAULT_SELECTION = "rank";

    private static Logger log = LoggerFactory.getLogger(GeneticRunner.class);
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
        String script ="";

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
                case "-mutationRate":
                    mutationRate = Parser.toDouble(keyValue[1]).orElse(DEFAULT_MUTATION_RATE);
                    log.info("Set mutation rate to {}",mutationRate);
                    break;
                case "-population":
                    populationSize = Parser.toInteger(keyValue[1]).orElse(DEFAULT_POPULATION);
                    log.info("Set population size to {}",populationSize);
                    break;
                case "-replaceRate":
                    replacementRate = Parser.toDouble(keyValue[1]).orElse(DEFAULT_REPLACEMENT_RATE);
                    log.info("Set replacement rate to {}",replacementRate);
                    break;
                case "-fitThreshold":
                    fitnessThreshold = Parser.toDouble(keyValue[1]).orElse(DEFAULT_REPLACEMENT_RATE);
                    log.info("Set fitness threshold to {}",fitnessThreshold);
                    break;
                case "-generations":
                    maxGenerations = Parser.toInteger(keyValue[1]).orElse(DEFAULT_MAX_GENERATIONS);
                    log.info("Set max generations to {}",maxGenerations);
                    break;
                case "-selection":
                    selectionMethod = keyValue[1];
                    log.info("Set selection method to {}",selectionMethod);
                    break;
                case "-input":
                    inputType = keyValue[1];
                    log.info("Set input files to {}",inputType);
                    break;
                case "-script":
                    script = keyValue[1];
                    log.info("Set script to {}",inputType);
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

        FitnessSelector fs;
        switch (selectionMethod.toLowerCase().trim())
        {
            case "rank":
                fs = new RankProportional();
                break;
            case "tournament":
                fs = new FitnessTournament();
                break;
            case "fitProp":
            default:
                fs = new FitnessProportional();
        }




        if(PreCheck.isEmpty(testFilePath))testFilePath = dataFilePath;
        log.info("=== CSE5693-HW4 Genetic-Algorithm Runner ====");
        log.info("Author: Troy Toggweiler");
        log.info("Date: 4/4/2017 ");


        log.info("\n\n===  FILES  ===");
        log.info("Loading feature file: {}", featureFilePath);
        log.info("Loading data file: {}", dataFilePath);
        log.info("Loading validation file: {}", testFilePath);

        FeatureLoader featLoader = new FeatureLoader(featureFilePath);

        List<Map<String, ? extends Comparable>> trainingDatas = DataLoader.loadDataFromFile(dataFilePath, featLoader.getAllFeatures());
        List<Map<String, ? extends Comparable>> validationDatas = DataLoader.loadDataFromFile(testFilePath, featLoader.getAllFeatures());

        List<Map<String, ? extends Comparable>> allData = new ArrayList<>(trainingDatas);
        allData.addAll(validationDatas);


        if(PreCheck.notEmpty(script))
        {
            GeneticSearch gs = new GeneticSearch(populationSize,replacementRate,mutationRate);

            switch (script)
            {
                case "selection":

                    for(int i = 0; i<=1000; i += 100) {
                        gs.setGenerationLimit(i);
                        gs.setFitnessSelector(new RankProportional());
                        Hypothesis rankh = gs.generateAndEvolveClassifier(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), trainingDatas);
                        gs.setFitnessSelector(new FitnessTournament());
                        Hypothesis tourh = gs.generateAndEvolveClassifier(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), trainingDatas);
                        gs.setFitnessSelector(new FitnessProportional());
                        Hypothesis fitProp = gs.generateAndEvolveClassifier(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), trainingDatas);

                        log.info("---- Rank Generation count: {}  ----\n",i);
                        Performance dataPerform =new Performance(rankh,trainingDatas);
                        log.info("Rank Data Performance: {}",dataPerform.getPerformanceString(true,true,false));
                        Performance validPerfrom =new Performance(rankh,validationDatas);
                        log.info("Rank Validation Performance: {}",validPerfrom.getPerformanceString(true,true,false));
                        log.info("Rank Hypotheses:{}\n",rankh.getClassifierString(false));

                        dataPerform =new Performance(tourh,trainingDatas);
                        log.info("Tournament Data Performance: {}",dataPerform.getPerformanceString(true,true,false));
                        validPerfrom =new Performance(tourh,validationDatas);
                        log.info("Tournament Validation Performance: {}",validPerfrom.getPerformanceString(true,true,false));
                        log.info("Tour Hypotheses:{}\n",tourh.getClassifierString(false));

                        dataPerform =new Performance(fitProp,trainingDatas);
                        log.info("FitProp Data Performance: {}",dataPerform.getPerformanceString(true,true,false));
                        validPerfrom =new Performance(fitProp,validationDatas);
                        log.info("FitProp Validation Performance: {}",validPerfrom.getPerformanceString(true,true,false));
                        log.info("FitProp Hypotheses:{}\n",fitProp.getClassifierString(false));

                    }
                    return;
                case "replace":

                    for(double i = 0.1; i<=1000; i += 0.1) {
                        gs.setCrossoverReplacementRate(i);
                        gs.setFitnessSelector(new RankProportional());
                        Hypothesis rankh = gs.generateAndEvolveClassifier(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), trainingDatas);
                        gs.setFitnessSelector(new FitnessTournament());
                        Hypothesis tourh = gs.generateAndEvolveClassifier(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), trainingDatas);
                        gs.setFitnessSelector(new FitnessProportional());
                        Hypothesis fitProp = gs.generateAndEvolveClassifier(featLoader.getArgumentFeatures(), featLoader.getTargetFeatures(), trainingDatas);

                        log.info("  ----- Replacement Rate: {}  -----\n",i);
                        Performance dataPerform =new Performance(rankh,trainingDatas);
                        log.info("Rank Data Performance: {}",dataPerform.getPerformanceString(true,true,false));
                        Performance validPerfrom =new Performance(rankh,validationDatas);
                        log.info("Rank Validation Performance: {}",validPerfrom.getPerformanceString(true,true,false));
                        log.info("Rank Hypotheses: {}\n",rankh.getClassifierString(false));

                        dataPerform =new Performance(tourh,trainingDatas);
                        log.info("Tournament Data Performance: {}",dataPerform.getPerformanceString(true,true,false));
                        validPerfrom =new Performance(tourh,validationDatas);
                        log.info("Tournament Validation Performance: {}",validPerfrom.getPerformanceString(true,true,false));
                        log.info("Tournament Hypotheses: {}\n",tourh.getClassifierString(false));

                        dataPerform =new Performance(fitProp,trainingDatas);
                        log.info("FitProp Data Performance: {}",dataPerform.getPerformanceString(true,true,false));
                        validPerfrom =new Performance(fitProp,validationDatas);
                        log.info("FitProp Validation Performance: {}",validPerfrom.getPerformanceString(true,true,false));
                        log.info("FitProp Hypotheses: {}\n",fitProp.getClassifierString(false));

                    }

                    return;
            }
        }

        GeneticSearch gs = new GeneticSearch(populationSize,replacementRate,mutationRate);
        gs.setGenerationLimit(maxGenerations);
        gs.setFitnessThreshold(fitnessThreshold);
        gs.setFitnessSelector(fs);
        Hypothesis h = gs.generateAndEvolveClassifier(featLoader.getArgumentFeatures(),featLoader.getTargetFeatures(),trainingDatas);

        log.info("Hypotheses: \n",h.getClassifierString(false));

        Performance dataPerform =new Performance(h,trainingDatas);
        log.info("Data Performance: {}",dataPerform.getPerformanceString(true,true,false));

        Performance validPerfrom =new Performance(h,validationDatas);
        log.info("Validation Performance: {}",validPerfrom.getPerformanceString(true,true,false));
    }

}
