package com.ttoggweiler.cse5693.predict;

import com.ttoggweiler.cse5693.data.DataSet;
import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Base class that all classifiers will extend, such as Rule and RuleSetClassifier
 * Used to classify example data
 */
public abstract class Classifier extends Identity
{
    private List<Feature> targetFeatures;
    private List<Feature> features;
    private DataSet dataSet;

    public abstract void train(DataSet trainingSet);

    public abstract Map<String,Comparable> classifyExample(Map<String,Comparable> example);

    public abstract String getClassifierString(boolean includeName);

    public Comparable classifyExample(Feature target, Map<String, Comparable> example)
    {
        return classifyExample(example).get(target.name());
    }

    public boolean isCorrectClassification(Map<String,Comparable> example)
    {
        Map<String,Comparable> prediction = classifyExample(example);
        if(PreCheck.isEmpty(prediction) || prediction.size() != targetFeatures.size())
            throw new IllegalArgumentException("Missing target features in prediction");

        for (String targetFeature : targetFeatures.stream().map(Feature::name).collect(Collectors.toSet()))
            if(!prediction.get(targetFeature).equals(example.get(targetFeature)))
                return false;
        return true;
    }

    protected void initWithDataSet(DataSet data)
    {
        this.features = data.getFeatures();
        this.targetFeatures = data.getTargets();
        this.dataSet = data;
    }

    public void setTargetFeatures(List<Feature> targetFeatures)

    {
        this.targetFeatures = targetFeatures;
    }

    public List<Feature> getTargetFeatures()
    {
        return this.targetFeatures;
    }

    public void setFeatures(List<Feature> features)
    {
        this.features = features;
    }

    public List<Feature> getFeatures()
    {
        return this.features;
    }

    public Feature getFeature(UUID id)
    {
        return features.stream().filter(f -> f.getId().equals(id)).findAny()
                .orElse(targetFeatures.stream().filter(f -> f.getId().equals(id)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("No feature exists with id: "+ id.toString())));
    }

    public Feature getFeature(String name)
    {
        return features.stream().filter(f -> f.name().equals(name)).findAny()
                .orElse(targetFeatures.stream().filter(f -> f.name().equals(name)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("No feature exists with name: "+ name)));
    }

    @Override
    public String toString()
    {
        return getClassifierString(true);
    }
}
