package com.ttoggweiler.cse5693;


import com.ttoggweiler.cse5693.learner.TreeBuilder;
import com.ttoggweiler.cse5693.loader.DataLoader;
import com.ttoggweiler.cse5693.loader.FeatureLoader;
import com.ttoggweiler.cse5693.tree.Feature;
import com.ttoggweiler.cse5693.tree.Node;
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
//
//        String dataFilePath = "/inputFiles/bool-train.txt";
//        String featureFilePath = "/inputFiles/bool-attr.txt";

//        String dataFilePath = "/inputFiles/iris-train.txt";
//        String featureFilePath = "/inputFiles/iris-attr.txt";

        log.info("= FILES =");
        log.info("Loading feature file: {}", featureFilePath);
        log.info("Loading data file: {}", dataFilePath);

        List<Feature> features = FeatureLoader.loadFeaturesFromFile(featureFilePath);
        List<Map<String, Comparable>> datas = DataLoader.loadDataFromFile(dataFilePath,features);


        int i =0;
        log.info("= FEATURES =");
        for (Feature feature : features) log.info("Feature Name: {} Values: {}",feature.getName(),feature.getValues());
        log.info("= TRAINING DATA =");
        for(Map<String,Comparable> data : datas ) log.info("#{} Values: {}",i++, data.toString());

        Feature target = features.remove(features.size() - 1);
        Node rootNode = TreeBuilder.buildTree(target,features,datas);
        log.info("\n"+rootNode.toTreeString());

    }


}
