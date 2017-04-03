package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Created by ttoggweiler on 4/3/17.
 */
public class PreCondition extends Condition
{
    private List< Predicate< Map<String,Comparable>>> featurePredicates;
    private Random rand = new Random();

    public PreCondition(Feature feature)
    {
        this.setConditionFeature(feature);
        // If feature has no known values, assume continuous feature
        if(PreCheck.isEmpty(feature.getValues()))
        {
            featurePredicates = new ArrayList<>(1);
            // Generate a random number for the single predicate that will represent this precondition
            Double randomDouble = rand.nextGaussian() * 100;
            featurePredicates.add((map)-> {
                Comparable value = map.get(feature.getName());
                return (value.compareTo(randomDouble) > 0);
            });
        }else{
            for (Object value : feature.getValues()) {
                Predicate valuePredicate = feature.getPredicateForValue((Comparable) value);
                if(rand.nextDouble() > 0.05)
                    featurePredicates.add(valuePredicate);
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
            else combinedPredicate.or(featurePredicate);
        }
        return combinedPredicate;
    }

    public List< Predicate< Map<String,Comparable>>> getFeaturePredicates()
    {
        return this.featurePredicates;
    }

    @Override
    public boolean test(Map<String, Comparable> stringComparableMap)
    {
        return getPredicate().test(stringComparableMap);
    }
}
