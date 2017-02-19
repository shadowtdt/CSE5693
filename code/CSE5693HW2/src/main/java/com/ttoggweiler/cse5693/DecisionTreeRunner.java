package com.ttoggweiler.cse5693;


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
    public static void main(String[] args) throws Exception
    {
        log.info("=== CSE5693-Hw2 Decision Tree Runner ====");

        String dataFilePath = "/inputFiles/tennis-train.txt";
        String featureFilePath = "/inputFiles/tennis-attr.txt";
        String testFilePath = "/inputFiles/tennis-test.txt";
//
//        String dataFilePath = "/inputFiles/bool-train.txt";
//        String featureFilePath = "/inputFiles/bool-attr.txt";
//        String testFilePath = "/inputFiles/bool-test.txt";

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
        FeatureNode rootNode = TreeBuilder.buildTree(target,features,trainingDatas);
        log.info("\n"+rootNode.toString());
        //for(Map<String,Comparable> data : validationDatas ) log.info("#{} Classification: {}",i++, rootNode.getClassificationDistribution(data).toString());

        Double trainingAccuracy = Examiner.getAccuracy(target,rootNode,trainingDatas);
        log.info("Training accuracy; {}%",trainingAccuracy*100);

        Double validationAccuracy = Examiner.getAccuracy(target,rootNode,validationDatas);

        log.info("Validation accuracy; {}%",validationAccuracy*100);
    }



}
