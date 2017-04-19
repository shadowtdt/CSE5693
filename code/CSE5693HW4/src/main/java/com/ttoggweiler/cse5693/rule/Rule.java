package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 4/3/17.
 */
public class Rule extends Classifier
{

    // todo map string -> condition
    private List<Condition> preConditions;
    private List<Condition> postConditions;

    public Rule(Collection<Feature<? extends Comparable>> featureList, Collection<Feature<? extends Comparable>> targetFeatureList)
    {
        this.setFeatures(featureList.stream().collect(Collectors.toList()));
        this.setTargetFeatures(targetFeatureList.stream().collect(Collectors.toList()));
        this.setPreConditions(featureList.stream().map(f -> new Condition(f,false)).collect(Collectors.toList()));
        this.setPostConditions(targetFeatureList.stream().map(tf -> new Condition(tf,true)).collect(Collectors.toList()));
    }

    public Rule(Collection<Feature<? extends Comparable>> featureList, Collection<Feature<? extends Comparable>> targetFeatureList, List<Condition> preConditions, List<Condition> postConditions)
    {
        this.setFeatures(featureList.stream().collect(Collectors.toList()));
        this.setTargetFeatures(targetFeatureList.stream().collect(Collectors.toList()));
        this.setPreConditions(preConditions);
        this.setPostConditions(postConditions);
    }

    @Override
    public Map<String,? extends Comparable> classifyExample(Map<String, ? extends Comparable> example)
    {
        Map<String,Comparable> prediction = new HashMap<>();
        for (Condition postCondition : postConditions) {
            postCondition.getFeatureConditions().entrySet().stream()
                    .filter(Map.Entry :: getValue).findAny().ifPresent(entry ->
                    prediction.put(postCondition.getFeature().getName(),entry.getKey()));
        }
        return test(example) ? prediction : Collections.emptyMap();
    }

    @Override
    public int getNumberOfBits()
    {
        return preConditions.stream().mapToInt(c -> c.getFeatureConditions().size()).sum()
                + postConditions.stream().mapToInt(c -> c.getFeatureConditions().size()).sum();
    }

    @Override
    public Predicate<Map<String,? extends Comparable>> getPredicate()
    {
        Predicate<Map<String,? extends Comparable>> combinedPredicate = null;
        for (Predicate<Map<String,? extends Comparable>> prePredicate : preConditions) {
            if(combinedPredicate == null) combinedPredicate = prePredicate;
            else combinedPredicate.and(prePredicate);
        }

        return combinedPredicate;
    }

    @Override
    public String getClassifierString(boolean includeName)
    {
        return getPreConditions().stream()
                .map(c -> c.getConditionString(includeName))
                .collect(Collectors.joining(" AND ","IF "," THEN "))

                + getPostConditions().stream()
                .map(c -> c.getConditionString(includeName))
                .collect(Collectors.joining(" AND "));
    }

    public void setPreConditions(List<Condition> preConditions)
    {
        this.preConditions = new ArrayList<>(preConditions);
    }

    public void setPostConditions(List<Condition> postConditions)
    {
        this.postConditions = new ArrayList<>(postConditions);
    }

    public List<Condition> getPreConditions()
    {
        return preConditions;
    }

    public List<Condition> getPostConditions()
    {
        return postConditions;
    }

}
