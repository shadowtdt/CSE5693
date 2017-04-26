package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.data.DataSet;
import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.predict.Classifier;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by ttoggweiler on 3/7/17.
 */
public class Network extends Classifier
{
    public static double DEFAULT_LEARNING_RATE = .01;
    public static double DEFAULT_MOMENTUM = .85;
    public static double DEFAULT_MIN_ACCURACY_THRESHOLD = .99;
    public static int DEFAULT_MAX_ITERATIONS = 1000;
    public static int DEFAULT_MIN_ITERATIONS = DEFAULT_MAX_ITERATIONS/10;
    private static int[] DEFAULT_LAYER_SIZES = new int[]{10};

    private static Logger log = LoggerFactory.getLogger(Network.class);

    private List<Layer> layersInNetwork;

    private double learningRate;
    private double accuracyThreshold = .999; // 0-1 accuracy%
    private double momentum;
    private int iterations;
    private int[] hiddenLayerSizes;

    @Deprecated // using classifier inherited
    private List<Feature> outputFeatures;
    private DataSet trainingSet;

    public Network(List<Feature> outputFeatures, int iterations, double learningRate, double momentum, double accuracyThreshold, int... hiddenLayerSizes)
    {
        this.outputFeatures = outputFeatures; // TODO: ttoggweiler 4/23/17 remove
        this.learningRate = PreCheck.defaultTo(learningRate,DEFAULT_LEARNING_RATE);
        this.momentum = PreCheck.defaultTo(momentum,DEFAULT_MOMENTUM);
        this.iterations = PreCheck.defaultTo(iterations,DEFAULT_MAX_ITERATIONS);
        this.accuracyThreshold = PreCheck.defaultTo(accuracyThreshold,DEFAULT_MIN_ACCURACY_THRESHOLD);
        this.hiddenLayerSizes = PreCheck.defaultTo(hiddenLayerSizes,DEFAULT_LAYER_SIZES);
    }

    @Override
    public void train(DataSet trainingSet)
    {
        initWithDataSet(trainingSet);
        layersInNetwork = createLayers(trainingSet.getFeatures(), getTargetFeatures(), hiddenLayerSizes);

        this.trainingSet = trainingSet;
        Layer inputLayer = layersInNetwork.get(0);
        Layer outputLayer = layersInNetwork.get(layersInNetwork.size() - 1);
        DoubleSummaryStatistics stat = new DoubleSummaryStatistics();

        for (int i = 0; i < iterations+1; i++) {
            double correctPredictions = 0;

            DoubleSummaryStatistics cycleError = new DoubleSummaryStatistics();
            for (Map<String, Comparable> trainingExample : trainingSet) {
                ListIterator<Layer> layerItr = layersInNetwork.listIterator();
                Map<String, Double> values_double = trainingSet.convertValuesToDouble(trainingExample);

                // FeedForward
                inputLayer.setNodeValues(values_double);
                while (layerItr.hasNext()) layerItr.next().feedForward();
                // BackPropagation
                outputLayer.setNodeValues(values_double);
                while (layerItr.hasPrevious()) layerItr.previous().backPropagate();
                // Weight update
                while (layerItr.hasNext()) layerItr.next().updateWeights(learningRate, momentum);

                Feature outputFeature = outputFeatures.get(0);
                double worstError = 0;
                cycleError.combine(outputLayer.getNodes().stream()
                        .map(Node::getLastNodeResult)
                        .mapToDouble(NodeResult::getValue).summaryStatistics());

                for (Node node : outputLayer.getNodes()) {
                    Comparable actualValue = trainingExample.get(node.name());
                    Double actualReal = outputFeature.mapFeatureValueToRealNumber(actualValue);
                    Double predictionReal = node.getLastNodeResult().getValue();
                    Comparable predictionValue = outputFeature.mapRealNumberToFeatureValue(predictionReal);

                    Double error = node.getLastNodeResult().getError();
                    if (error > worstError) worstError = error;
                    if (predictionValue.equals(actualValue)) {
                        correctPredictions++;
                        //log.debug("Actual: {}_{}, Prediction: {}_{}, Error: {}", actualValue, actualReal, predictionValue, predictionReal, error);
                    } else {
                        //log.warn("Actual: {}_{}, Prediction: {}_{} Error: {}", actualValue, actualReal, predictionValue, predictionReal,error);
                    }

                }
            }

            // log.debug("#{}Itr Error: {}", i, cycleError.toString().split("\\{")[1]);
            stat.combine(cycleError);

            double accuracy = ((correctPredictions + 0d )/ trainingSet.size()) / outputFeatures.size();

            if (i % 100 == 0 ) {
                log.info("#{} @ {}% EStat: {}", i, accuracy*100
                        , cycleError.toString().split("\\{")[1]); // remove doubleDis header
                // log.debug("All Error stat: {}", stat.toString());
            }
            if (accuracy >= accuracyThreshold && i > DEFAULT_MIN_ITERATIONS) {
                log.debug("** Passed Threshold: #{}Itr @ {}% EStat: {}", i, accuracy * 100,
                        cycleError.toString().split("\\{")[1]); // remove doubleDis header
                break;
            }
        }
    }

    @Override
    public Map<String, Comparable> classifyExample(Map<String, Comparable> example)
    {
        Layer inputLayer = layersInNetwork.get(0);
        Layer outputLayer = layersInNetwork.get(layersInNetwork.size() - 1);
        ListIterator<Layer> layerItr = layersInNetwork.listIterator();

        inputLayer.setNodeValues(trainingSet.convertValuesToDouble(example));
        while (layerItr.hasNext()) layerItr.next().feedForward();

        Map<String,Comparable> predictionMap = new HashMap<>();
        for (Node node : outputLayer.getNodes()) {
            Feature outputFeature = getFeature(node.name());
            Double prediction = node.getLastNodeResult().getValue();
            Comparable predictionFromDouble = outputFeature.mapRealNumberToFeatureValue(prediction); // todo   test
            predictionMap.put(outputFeature.name(),predictionFromDouble);
        }
        return predictionMap;
    }

    @Override
    public String getClassifierString(boolean includeName)
    {
        return getTopologyString();
    }

    public Double getAccuracy(DataSet trainingExamples)
    {
        double correctPredictions = 0;
        Layer inputLayer = layersInNetwork.get(0);
        Layer outputLayer = layersInNetwork.get(layersInNetwork.size() - 1);
        for (Map<String, Comparable> trainingExample : trainingExamples) {
            ListIterator<Layer> layerItr = layersInNetwork.listIterator();
            inputLayer.setNodeValues(trainingExamples.convertValuesToDouble(trainingExample));
            while (layerItr.hasNext()) layerItr.next().feedForward();

            Feature outputFeature = outputFeatures.get(0); // fixme wrong?

//            StringBuilder inputs = new StringBuilder("\nData: ");
//            trainingExample.forEach((k, v) -> inputs.append(k + "(" + v + "), "));
//            for (Layer layer : layersInNetwork) {
//                inputs.append("\nLayer(" + layer.name() + "): ");
//                layer.getNodes().forEach(node ->
//                        inputs.append(node.name() + "(" + outputFeature.mapRealNumberToFeatureValue(node.getLastNodeResult().getValue()) + " : " + node.getLastNodeResult().getValue() + "), "));
//            }
//            log.info(inputs.toString());

            for (Node node : outputLayer.getNodes()) {
                Double prediction = node.getLastNodeResult().getValue();
                Comparable predictionFromDouble = outputFeatures.get(0).mapRealNumberToFeatureValue(prediction);
                Double error = node.getLastNodeResult().getError();
                Comparable actual = trainingExample.get(node.name());
                //log.info("{} Prediction: {} ({}) Actual: {} ({})",node.name(),prediction,predictionFromDouble,outputFeature.mapFeatureValueToRealNumber(actual),actual);
                if (predictionFromDouble.equals(actual)) correctPredictions++;
            }
        }
        return (correctPredictions / trainingExamples.size()) / outputFeatures.size(); //fixme wrong
    }


    private static Map<String, Double> convertExampleValuesToDouble(Map<String, Comparable> inputs, List<Feature> inputFeatures)
    {
        Map<String, Double> trainingExampleWithDoubleValues = new HashMap<>(inputFeatures.size());
        for (Feature inputFeature : inputFeatures) {
            Double doubleValue = inputFeature.mapFeatureValueToRealNumber(inputs.get(inputFeature.name()));
            trainingExampleWithDoubleValues.put(inputFeature.name(), doubleValue);
        }
        return trainingExampleWithDoubleValues;
    }

    /* Building */
    private static List<Layer> createLayers(List<Feature> inputFeatures, List<Feature> outputFeatures, int... hiddenSizes)
    {
        // If no hidden sizes are provided, assume single hidden layer with half of input node count
        List<Layer> layers = new ArrayList<>(hiddenSizes.length + 2);

        // Create the layers
        layers.add(createFeatureLayer("Input", inputFeatures, true));
        layers.addAll(createHiddenLayers(hiddenSizes));
        layers.add(createFeatureLayer("Output", outputFeatures, false));

        // Connect all the nodes to each other
        for (int i = 0; i < layers.size() - 1; i++) connectLayers(layers.get(i), layers.get(i + 1));
        return layers;
    }

    private static List<Layer> createHiddenLayers(int ... hiddenSizes)
    {
        if (PreCheck.isEmpty(hiddenSizes)) hiddenSizes = new int[]{5};
        List<Layer> layers = new ArrayList<>(hiddenSizes.length);
        for (int i = 0; i < hiddenSizes.length; i++) {
            if (hiddenSizes[i] < 1) continue;
            layers.add(new Layer("H" + i, null, hiddenSizes[i]));
        }
        return layers;
    }

    private static Layer createFeatureLayer(String layerName, List<Feature> features, boolean inputLayer)
    {
        Layer layer = new Layer(layerName, inputLayer, features.size());
        Iterator<Node> nodeItr = layer.getNodes().iterator();
        for (Feature feature : features) {
            nodeItr.next().setName(feature.name());
        }
        return layer;
    }

    private static void connectLayers(Layer layerOne, Layer layerTwo)
    {
        for (Node lOneNode : layerOne.getNodes()) {
            int count = 0;
            for (Node lTwoNode : layerTwo.getNodes()) {
                new Edge("E" + count++, lOneNode, lTwoNode, (new Random().nextDouble() - 0.5));
            }
        }
    }

    /* Field methods */
    public Layer getInputLayer()
    {
        return layersInNetwork.get(0);
    }

    public Layer getOutputLayer()
    {
        return layersInNetwork.get(layersInNetwork.size() - 1);
    }

    public String getTopologyString()
    {
        StringBuilder topologyStringBuilder = new StringBuilder("\n-- Network: " + name() + " --\n");
        for (Layer layer : layersInNetwork) {
            topologyStringBuilder.append("\t");
            topologyStringBuilder.append(layer.getTopologyString());
            topologyStringBuilder.append("\n");
        }
        return topologyStringBuilder.toString();
    }
}
