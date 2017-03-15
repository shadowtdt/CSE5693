package com.ttoggweiler.cse5693;

import com.codahale.metrics.MetricRegistry;
import com.ttoggweiler.cse5693.ann.Network;
import com.ttoggweiler.cse5693.loader.DataLoader;
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
    /* Input file selectors */
    public static final String INPUT_TENNIS = "tennis";
    public static final String INPUT_IRIS = "iris";
    public static final String INPUT_IDENTITY = "identity";
    public static final String INPUT_BOOL = "bool";
    public static final String INPUT_IRIS_NOISY = "iris-noisy";
    
    private static Logger log = LoggerFactory.getLogger(ANNRunner.class);
    private static final MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) throws Exception
    {
        
        String inputType = INPUT_IDENTITY;
        String featureFilePath;
        String dataFilePath;
        String testFilePath;
        switch(inputType)
        {
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

        log.debug("Args: {}", Arrays.toString(args));
        if (args.length > 0)
            if (args.length < 3)
                throw new IllegalArgumentException("Must provide files for features, training and validation. Found: " + Arrays.toString(args));
            else {
                featureFilePath = args[0].trim();
                dataFilePath = args[1].trim();
                testFilePath = args[2];
            }


        log.info("=== CSE5693-HW3 Artificial-Neural-Network Runner ====");


        log.info("\n\n===  FILES  ===");
        log.info("Loading feature file: {}", featureFilePath);
        log.info("Loading data file: {}", dataFilePath);
        log.info("Loading validation file: {}", testFilePath);

        FeatureLoader featLoader = new FeatureLoader(featureFilePath);

        List<Map<String, Comparable>> trainingDatas = DataLoader.loadDataFromFile(dataFilePath, featLoader.getAllFeatures());
        List<Map<String, Comparable>> validationDatas = DataLoader.loadDataFromFile(testFilePath, featLoader.getAllFeatures());

        List<Map<String, Comparable>> allData = new ArrayList<>(trainingDatas);
        allData.addAll(validationDatas);

        Network ann = new Network(0.1,0.1,featLoader.getArgumentFeatures(),featLoader.getTargetFeatures(),3);
        ann.setName(inputType);
        log.info("== ANN Topology ==");
        log.info(ann.getTopologyString());

        ann.train(trainingDatas);
        log.info(ann.getTopologyString());

        int i = 0;
    }
}
