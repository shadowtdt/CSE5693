package com.ttoggweiler.cse5693;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.ttoggweiler.cse5693.ann.Network;
import com.ttoggweiler.cse5693.loader.DataLoader;
import com.ttoggweiler.cse5693.loader.Feature;
import com.ttoggweiler.cse5693.loader.FeatureLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by ttoggweiler on 3/7/17.
 */
public class ANNRunner
{
    private static Logger log = LoggerFactory.getLogger(ANNRunner.class);
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
        if (args.length > 0)
            if (args.length < 3)
                throw new IllegalArgumentException("Must provide files for features, training and validation. Found: " + Arrays.toString(args));
            else {
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
        List<Map<String, Comparable>> trainingDatas = DataLoader.loadDataFromFile(dataFilePath, features);
        List<Map<String, Comparable>> validationDatas = DataLoader.loadDataFromFile(testFilePath, features);

        List<Map<String, Comparable>> allData = new ArrayList<Map<String, Comparable>>(trainingDatas);
        allData.addAll(validationDatas);

        Network ann = new Network(0.1,0.1,features,features,5);
        int i = 0;
    }
}
