package com.ttoggweiler.cse5693.predict;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Base class that all classifiers will extend, such as Rule and RuleSetClassifier
 * Used to classify example data
 */
public abstract class Classifier extends Identity
{
    private List<Feature> targetFeatures;
    private List<Feature> features;

    public abstract Map<String,Comparable> classifyExample(Map<String,Comparable> example);

    public abstract String getClassifierString(boolean includeName);

    public boolean isCorrectClassification(Map<String,Comparable> example)
    {
        Map<String,Comparable> prediction = classifyExample(example);
        if(PreCheck.isEmpty(prediction) || prediction.size() != targetFeatures.size()) return false;

        for (String targetFeature : targetFeatures.stream().map(Feature::getName).collect(Collectors.toSet()))
            if(!prediction.get(targetFeature).equals(example.get(targetFeature)))return false;
        return true;
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

    @Override
    public String toString()
    {
        return getClassifierString(true);
    }
}
