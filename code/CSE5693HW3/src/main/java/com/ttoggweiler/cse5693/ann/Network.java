package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.loader.Feature;
import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ttoggweiler on 3/7/17.
 */
public class Network extends Identity
{
    private List<Layer> layers;

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
        layers = createLayers(inputFeatures.size(),outputFeatures.size(),hiddenLayerSizes);
    }

    public static List<Layer> createLayers(int inputSize, int outputSize, Integer...hiddenSizes)
    {
        PreCheck.ifEmpty(()-> new IllegalArgumentException("Must have at least one hidden layer"),hiddenSizes);
        List<Layer> layers = new ArrayList<>(hiddenSizes.length+2);

        layers.add(new Layer("InputLayer",true,inputSize));
        for (int i = 0; i < hiddenSizes.length; i++) {
            if(hiddenSizes[i] < 1) continue;
            layers.add(new Layer("H"+i,null,hiddenSizes[i]));
        }
        layers.add(new Layer("Output",false,outputSize));

        for (int i = 0; i < layers.size()-1; i++) {
            connectLayers(layers.get(i),layers.get(i+1));
        }
        return layers;
    }

    public static void connectLayers(Layer layerOne, Layer layerTwo)
    {
        for (Node lOneNode: layerOne.getNodes()) {
            for (Node lTwoNode : layerTwo.getNodes()) {
                new Edge(lOneNode,lTwoNode,0.05);
            }
        }
    }

}
