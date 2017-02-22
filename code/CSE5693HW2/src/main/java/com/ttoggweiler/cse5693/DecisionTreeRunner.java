package com.ttoggweiler.cse5693;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.ttoggweiler.cse5693.learner.Examiner;
import com.ttoggweiler.cse5693.learner.TreeBuilder;
import com.ttoggweiler.cse5693.loader.DataLoader;
import com.ttoggweiler.cse5693.loader.FeatureLoader;
import com.ttoggweiler.cse5693.rule.Rule;
import com.ttoggweiler.cse5693.rule.RuleBuilder;
import com.ttoggweiler.cse5693.tree.Feature;
import com.ttoggweiler.cse5693.tree.FeatureNode;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class that runs CSE5693 - HW2
 * Handles program arguments and sets up necessary libraries
 */
public class DecisionTreeRunner
{
    private static Logger log = LoggerFactory.getLogger(DecisionTreeRunner.class);
    private static final MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) throws Exception
    {
//        String featureFilePath = "/inputFiles/tennis-attr.txt";
//        String dataFilePath = "/inputFiles/tennis-train.txt";
//        String testFilePath = "/inputFiles/tennis-test.txt";
//
//        String featureFilePath = "/inputFiles/bool-attr.txt";
//        String dataFilePath = "/inputFiles/bool-train.txt";
//        String testFilePath = "/inputFiles/bool-test.txt";
//
        String featureFilePath = "/inputFiles/iris-attr.txt";
        String dataFilePath = "/inputFiles/iris-train.txt";
        String testFilePath = "/inputFiles/iris-test.txt";

//        String featureFilePath = "/inputFiles/iris-attr.txt";
//        String dataFilePath = "/inputFiles/iris-trainNoisy.txt";
//        String testFilePath = "/inputFiles/iris-test.txt";

//        String featureFilePath = "inputFiles/EnjoySport-attr.txt";
//        String dataFilePath = "inputFiles/enjoySport-train.txt";
//        String testFilePath = dataFilePath;

        log.debug("Args: {}", Arrays.toString(args));
        if(args.length > 0)
            if(args.length < 3)
                throw new IllegalArgumentException("Must provide files for features, training and validation. Found: " + Arrays.toString(args));
            else{
                featureFilePath = args[0].trim();
                dataFilePath = args[1].trim();
                testFilePath = args[2];
            }
        log.info("=== CSE5693-Hw2 Decision Tree Runner ====");






        log.info("\n\n===  FILES  ===");
        log.info("Loading feature file: {}", featureFilePath);
        log.info("Loading data file: {}", dataFilePath);
        log.info("Loading validation file: {}", testFilePath);

        List<Feature> features = FeatureLoader.loadFeaturesFromFile(featureFilePath);
        List<Map<String, Comparable>> trainingDatas = DataLoader.loadDataFromFile(dataFilePath,features);
        List<Map<String, Comparable>> validationDatas = DataLoader.loadDataFromFile(testFilePath,features);

        List<Map<String, Comparable>> allData = new ArrayList<Map<String,Comparable>>(trainingDatas);
        allData.addAll(validationDatas);


        int i =0;
        log.info("\n\n===  FEATURES  ===");
        for (Feature feature : features) log.info("Feature Name: {} Values: {}",feature.getName(),feature.getValues());
        log.debug("\n\n===  TRAINING DATA  ===");
        for(Map<String,Comparable> data : trainingDatas ) log.debug("#{} Values: {}",i++, data.toString());
        log.debug("\n\n===  Validation DATA  ===");
        for(Map<String,Comparable> data : validationDatas ) log.debug("#{} Values: {}",i++, data.toString());

        log.info("\n\n===   Building Tree  ===");
        Feature target = features.remove(features.size() - 1);
        Timer.Context treeBuildTime = metrics.timer("tim").time();
        FeatureNode rootNode = TreeBuilder.buildTree(target,features,trainingDatas);
        log.info("Build time: {} (milliseconds)",treeBuildTime.stop()/1000000);

        log.info("\n\n===   Converting to Rules   ===");
        Timer.Context ruleConvertTime = metrics.timer("ruleConvert").time();
        List<Rule> rules = RuleBuilder.nodesToRules(rootNode.getLeafs());
        log.info("Rule Convert time: {} (milliseconds)",ruleConvertTime.stop()/1000000);

        log.info("\n\n===   Pruning Rules   ===");
        Timer.Context pruneTime = metrics.timer("pruneTime").time();
        List<Rule> postPruneRules= RuleBuilder.pruneRules(target,rules,allData);
        log.info("Rule prune time: {} (milliseconds)",pruneTime.stop()/1000000);

        log.info("\n\n===  Tree  ===");
        log.info("\n"+rootNode.toTreeString());

        log.info("\n\n===  Rules  ===");
        rules.forEach(r -> log.info(r.toString()));

        log.info("\n\n===  Post Prune Rules  ===");
        postPruneRules.forEach(r -> log.info(r.toString()));

        log.info("\n\n===   Tree  ===");
        Double trainingAccuracy = Examiner.getAccuracy(target,rootNode,trainingDatas);
        log.info("Tree Training accuracy: {}%",trainingAccuracy*100);
        Double validationAccuracy = Examiner.getAccuracy(target,rootNode,validationDatas);
        log.info("Tree Validation accuracy: {}%",validationAccuracy*100);

        log.info("\n\n===   Rule  ===");
        Double trainingRuleAccuracy = Examiner.getAccuracy(target,rules,trainingDatas);
        log.info("Rule Training accuracy: {}%",trainingRuleAccuracy*100);
        Double validationRuleAccuracy = Examiner.getAccuracy(target,rules,validationDatas);
        log.info("Rule Validation accuracy: {}%",validationRuleAccuracy*100);

        log.info("\n\n===   Post Prune Rule  ===");
        Double trainingPruneRuleAccuracy = Examiner.getAccuracy(target,postPruneRules,trainingDatas);
        log.info("Post Prune Rule Training accuracy: {}%",trainingPruneRuleAccuracy*100);
        Double validationPruneRuleAccuracy = Examiner.getAccuracy(target,postPruneRules,validationDatas);
        log.info("Post Prune Rule Validation accuracy: {}%",validationPruneRuleAccuracy*100);

    }



}
