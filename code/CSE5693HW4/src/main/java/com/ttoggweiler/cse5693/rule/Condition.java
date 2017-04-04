package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Conditions make up a Rule's {@link Rule} precondition set
 */
public class Condition implements Predicate<Map<String,? extends Comparable>>
{
    //todo string value of condition
    private Feature conditionFeature;
    private List<Predicate<Map<String,? extends Comparable>>> featurePredicates = new ArrayList<>();

    public Condition(Feature<? extends Comparable> feature)
    {
        this.setConditionFeature(feature);
        // If feature has no known values, assume continuous feature
        if(feature.isContinuous())
        {
            Comparable randValue = RandomUtil.selectRandomElement(feature.getValues());
            getConditionPredicates().add((map)-> map.get(feature.getName()).compareTo(RandomUtil.selectRandomElement(feature.getValues())) >= 0);
        }else{
            for (Predicate valuePredicate : feature.getValuePredicates())
                featurePredicates.add(RandomUtil.probability(.5) ? valuePredicate : valuePredicate.negate());
        }
    }

    @Override
    public boolean test(Map<String,? extends Comparable> stringComparableMap)
    {
        return getPredicate().test(stringComparableMap);
    }

    public List< Predicate< Map<String,? extends Comparable>>> getConditionPredicates()
    {
        return this.featurePredicates;
    }

    public void setConditionPredicates(List< Predicate< Map<String,? extends Comparable>>> predicateList)
    {
        this.featurePredicates = predicateList;
    }

    public Feature getConditionFeature()
    {
        return conditionFeature;
    }

    public void setConditionFeature(Feature conditionFeature)
    {
        this.conditionFeature = conditionFeature;
    }

    public Predicate<Map<String,? extends Comparable>> getPredicate()
    {
        Predicate<Map<String,? extends Comparable>> combinedPredicate = null;
        for (Predicate<Map<String,? extends Comparable>> featurePredicate : getConditionPredicates()) {
            if(combinedPredicate == null) combinedPredicate = featurePredicate;
            else combinedPredicate.and(featurePredicate);
        }
        return combinedPredicate;
    }
}
