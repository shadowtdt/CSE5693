package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Base class for Pre-Post condition
 * Conditions make up a Rule {@link Rule}
 */
public abstract class Condition implements Predicate<Map<String,Comparable>>
{
    private Feature conditionFeature;

    public Feature getConditionFeature()
    {
        return conditionFeature;
    }

    public void setConditionFeature(Feature conditionFeature)
    {
        this.conditionFeature = conditionFeature;
    }

}
