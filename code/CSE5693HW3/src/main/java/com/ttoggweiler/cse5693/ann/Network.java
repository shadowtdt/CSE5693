package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.loader.Feature;
import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by ttoggweiler on 3/7/17.
 */
public class Network extends Identity
{
    private static Logger log = LoggerFactory.getLogger(Network.class);

    private List<Layer> layersInNetwork;

    private double learningRate;
    private double momentum;
    private List<Feature> inputFeatures;
    private List<Feature> outputFeatures;

    public Network(double learningRate, double momentum, List<Feature> inputFeatures, List<Feature> outputFeatures, Integer... hiddenLayerSizes)
    {
        this.learningRate = learningRate;
        this.momentum = momentum;
        this.inputFeatures = inputFeatures;
        this.outputFeatures = outputFeatures;
        layersInNetwork = createLayers(inputFeatures,outputFeatures,hiddenLayerSizes);
    }

    public void train(List<Map<String, Comparable>> trainingExamples)
    {
        Layer inputLayer = layersInNetwork.get(0);
        Layer outputLayer = layersInNetwork.get(layersInNetwork.size()-1);
        for (int i = 0; i < 5000; i++) {
            log.info("== Iteration {} ==",i);
            for (Map<String, Comparable> trainingExample : trainingExamples) {
                ListIterator<Layer> layerItr = layersInNetwork.listIterator();

                inputLayer.setNodeValues(convertExampleValuesToDouble(trainingExample,inputFeatures));
                while(layerItr.hasNext())layerItr.next().feedForward();

                outputLayer.setNodeValues(convertExampleValuesToDouble(trainingExample,outputFeatures));
                while(layerItr.hasPrevious())layerItr.previous().backPropagate();

                while(layerItr.hasNext())layerItr.next().updateWeights(0.1);
                log.info("== Example Result ==");
                for (Node node : outputLayer.getNodes()) {
                    Double prediction = node.getLastNodeResult().getValue();
                    Double error = node.getLastNodeResult().getError();
                    Comparable actual = trainingExample.get(node.getName());
                    log.info("O: {} vs T: {}({}) E: {}",prediction,actual,outputFeatures.get(0).getDoubleForFeatureValue(actual),error);
                }
            }
        }
    }

    public Layer getInputLayer()
    {
        return layersInNetwork.get(0);
    }

    public Layer getOutputLayer()
    {
        return layersInNetwork.get(layersInNetwork.size()-1);
    }

    public String getTopologyString()
    {
        StringBuilder topologyStringBuilder = new StringBuilder("\n-- Network: "+getName()+" --\n");
        for(Layer layer : layersInNetwork)
        {
            topologyStringBuilder.append("\t");
            topologyStringBuilder.append(layer.getTopologyString());
            topologyStringBuilder.append("\n");
        }
        return topologyStringBuilder.toString();
    }

    private static Map<String,Double> convertExampleValuesToDouble(Map<String, Comparable> inputs, List<Feature> inputFeatures)
    {
        Map<String, Double> trainingExampleWithDoubleValues = new HashMap<>(inputFeatures.size());
        for (Feature inputFeature : inputFeatures) {
            Double doubleValue = inputFeature.getDoubleForFeatureValue(inputs.get(inputFeature.getName()));
            trainingExampleWithDoubleValues.put(inputFeature.getName(),doubleValue);
        }
        return trainingExampleWithDoubleValues;
    }

    private static List<Layer> createLayers(List<Feature> inputFeatures, List<Feature> outputFeatures, Integer...hiddenSizes)
    {
        // If no hidden sizes are provided, assume single hidden layer with half of input node count
        List<Layer> layers = new ArrayList<>(hiddenSizes.length+2);

        // Create the layers
        layers.add(createFeatureLayer("Input",inputFeatures,true));
        layers.addAll(createHiddenLayers(hiddenSizes));
        layers.add(createFeatureLayer("Output",outputFeatures,false));

        // Connect all the nodes to each other
        for (int i = 0; i < layers.size()-1; i++) connectLayers(layers.get(i),layers.get(i+1));
        return layers;
    }

    private static List<Layer> createHiddenLayers(Integer ... hiddenSizes)
    {
        if(PreCheck.isEmpty(hiddenSizes))hiddenSizes = new Integer[]{5};
        List<Layer> layers = new ArrayList<>(hiddenSizes.length);
        for (int i = 0; i < hiddenSizes.length; i++) {
            if(hiddenSizes[i] < 1) continue;
            layers.add(new Layer("H"+i,null,hiddenSizes[i]));
        }
        return layers;
    }

    private static Layer createFeatureLayer(String layerName, List<Feature> features,boolean inputLayer)
    {
        Layer layer = new Layer(layerName,inputLayer,features.size());
        Iterator<Node> nodeItr = layer.getNodes().iterator();
        for (Feature feature : features) {
            nodeItr.next().setName(feature.getName());
        }
        return layer;
    }

    private static void connectLayers(Layer layerOne, Layer layerTwo)
    {
        for (Node lOneNode: layerOne.getNodes()) {
            int count = 0;
            for (Node lTwoNode : layerTwo.getNodes()) {
                new Edge("E"+count++,lOneNode,lTwoNode, (new Random().nextDouble() -0.5) / 2);
            }
        }
    }

}
