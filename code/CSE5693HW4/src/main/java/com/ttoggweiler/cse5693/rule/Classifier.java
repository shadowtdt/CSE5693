package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Base class that all classifiers will extend, such as Rule and Hyothesis
 * Used to classify example data
 */
public abstract class Classifier extends Identity
{
    private List<Feature> targetFeatures;

    public abstract Comparable classifyExample(Map<String,Comparable> example);

    public abstract boolean isCorrectClassification(Map<String,Comparable> example);

    public void setTargetFeatures(List<Feature> targetFeatures)
    {
        this.targetFeatures = targetFeatures;
    }

    public List<Feature> getTargetFeatures()
    {
        return this.targetFeatures;
    }

}
