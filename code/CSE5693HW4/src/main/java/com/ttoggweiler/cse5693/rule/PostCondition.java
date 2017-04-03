package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.PreCheck;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Represents the post-condition for a classifier
 */
public class PostCondition extends Condition
{
    private List< Predicate< Map<String,Comparable>>> featurePredicates;
    private List<Comparable> values;
    private Random rand = new Random();

    public PostCondition(Feature feature)
    {
        this.setConditionFeature(feature);
        // If feature has no known values, assume continuous feature
        if(PreCheck.isEmpty(feature.getValues()))
        {
            throw new IllegalArgumentException("Continuous valued post-conditions for rule based classifiers are not supported at this time.");
        }else{
            for (Object value : feature.getValues()) {
                Predicate valuePredicate = feature.getPredicateForValue((Comparable) value);
                if(rand.nextDouble() > 0.05) {
                    featurePredicates.add(valuePredicate);
                    values.add((Comparable) value);
                }
                else
                    featurePredicates.add(valuePredicate.negate());
            }
        }
    }

    public Predicate<Map<String,Comparable>> getPredicate()
    {
        Predicate<Map<String,Comparable>> combinedPredicate = null;
        for (Predicate<Map<String, Comparable>> featurePredicate : featurePredicates) {
            if(combinedPredicate == null) combinedPredicate = featurePredicate;
            else combinedPredicate.and(featurePredicate);
        }
        return combinedPredicate;
    }

    public List< Predicate< Map<String,Comparable>>> getFeaturePredicates()
    {
        return this.featurePredicates;
    }

    public List<Comparable> getValues()
    {
        return this.values;
    }

    @Override
    public boolean test(Map<String, Comparable> stringComparableMap)
    {
        return getPredicate().test(stringComparableMap);
    }
}
