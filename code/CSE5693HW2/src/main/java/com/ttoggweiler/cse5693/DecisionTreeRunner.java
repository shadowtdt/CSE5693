package com.ttoggweiler.cse5693;


import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricRegistryListener;
import com.codahale.metrics.Timer;
import com.ttoggweiler.cse5693.learner.Examiner;
import com.ttoggweiler.cse5693.learner.TreeBuilder;
import com.ttoggweiler.cse5693.loader.DataLoader;
import com.ttoggweiler.cse5693.loader.FeatureLoader;
import com.ttoggweiler.cse5693.tree.Feature;
import com.ttoggweiler.cse5693.tree.FeatureNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Class that runs CSE5693 - HW2
 * Handles program arguments and sets up necessary libraries
 */
public class DecisionTreeRunner
{
    private static Logger log = LoggerFactory.getLogger(DecisionTreeRunner.class);
    static final MetricRegistry metrics = new MetricRegistry();
    static private final Timer treeBuildTimes = metrics.timer(MetricRegistry.name(DecisionTreeRunner.class, "treeBuildTimes"));

    public static void main(String[] args) throws Exception
    {
        log.info("=== CSE5693-Hw2 Decision Tree Runner ====");

//        String dataFilePath = "/inputFiles/tennis-train.txt";
//        String featureFilePath = "/inputFiles/tennis-attr.txt";
//        String testFilePath = "/inputFiles/tennis-test.txt";
//
        String dataFilePath = "/inputFiles/bool-train.txt";
        String featureFilePath = "/inputFiles/bool-attr.txt";
        String testFilePath = "/inputFiles/bool-test.txt";
//
//        String dataFilePath = "/inputFiles/iris-train.txt";
//        String featureFilePath = "/inputFiles/iris-attr.txt";
//        String testFilePath = "/inputFiles/iris-test.txt";



        log.info("= FILES =");
        log.info("Loading feature file: {}", featureFilePath);
        log.info("Loading data file: {}", dataFilePath);

        List<Feature> features = FeatureLoader.loadFeaturesFromFile(featureFilePath);
        List<Map<String, Comparable>> trainingDatas = DataLoader.loadDataFromFile(dataFilePath,features);
        List<Map<String, Comparable>> validationDatas = DataLoader.loadDataFromFile(testFilePath,features);


        int i =0;
        log.info("= FEATURES =");
        for (Feature feature : features) log.info("Feature Name: {} Values: {}",feature.getName(),feature.getValues());
        log.info("= TRAINING DATA =");
        for(Map<String,Comparable> data : trainingDatas ) log.info("#{} Values: {}",i++, data.toString());
        log.info("= Validation DATA =");
        for(Map<String,Comparable> data : validationDatas ) log.info("#{} Values: {}",i++, data.toString());

        Feature target = features.remove(features.size() - 1);
        Timer.Context treeBuild = treeBuildTimes.time();
        FeatureNode rootNode = TreeBuilder.buildTree(target,features,trainingDatas);
        Long buildDuration = treeBuild.stop()/1000000;

        log.info("Build time: {} (milliseconds)",buildDuration);

        log.info("\n"+rootNode.toString());
        //for(Map<String,Comparable> data : validationDatas ) log.info("#{} Classification: {}",i++, rootNode.getClassificationDistribution(data).toString());

        Double trainingAccuracy = Examiner.getAccuracy(target,rootNode,trainingDatas);
        log.info("Training accuracy; {}%",trainingAccuracy*100);

        Double validationAccuracy = Examiner.getAccuracy(target,rootNode,validationDatas);

        log.info("Validation accuracy; {}%",validationAccuracy*100);
    }



}
