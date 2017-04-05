package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Base class that all classifiers will extend, such as Rule and Hypothesis
 * Used to classify example data
 */
public abstract class Classifier extends Identity implements Predicate<Map<String,? extends Comparable>>
{
    private List<Feature<? extends Comparable>> targetFeatures;
    private List<Feature<? extends Comparable>> features;

    public abstract Map<String,? extends Comparable> classifyExample(Map<String,? extends Comparable> example);

    public abstract Predicate<Map<String,? extends Comparable>> getPredicate();

    public abstract int getNumberOfBits();

    @Override
    public boolean test(Map<String,? extends Comparable> stringComparableMap)
    {
        return getPredicate().test(stringComparableMap);
    }

    public boolean isCorrectClassification(Map<String, ? extends Comparable> example)
    {
        Map<String,? extends Comparable> prediction = classifyExample(example);
        if(PreCheck.isEmpty(prediction) || prediction.size() != targetFeatures.size())
            return false;

        for (String targetFeature : targetFeatures.stream().map(Feature::getName).collect(Collectors.toSet()))
            if(!prediction.get(targetFeature).equals(example.get(targetFeature)))return false;
        return true;
    }

    public void setTargetFeatures(List<Feature<? extends Comparable> > targetFeatures)
    {
        this.targetFeatures = targetFeatures;
    }

    public List<Feature<? extends Comparable>> getTargetFeatures()
    {
        return this.targetFeatures;
    }

    public void setFeatures(List<Feature<? extends Comparable> > features)
    {
        this.features = features;
    }

    public List<Feature<? extends Comparable>> getFeatures()
    {
        return this.features;
    }
}
