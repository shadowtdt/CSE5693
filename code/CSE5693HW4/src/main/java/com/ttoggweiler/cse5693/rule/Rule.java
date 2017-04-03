package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by ttoggweiler on 4/3/17.
 */
public class Rule extends Classifier implements Predicate<Map<String,Comparable>>
{

//    private Rule mom;
//    private Rule dad;
    private List<PreCondition> preConditions;
    private List<PostCondition> postConditions;

//    public Rule(List<PreCondition> preConditionList, List<PostCondition> postConditionList, Rule mom, Rule dad)
//    {
//        preConditionList.forEach(this :: addPrecondition);
//        postConditionList.forEach(this::addPostCondition);
//        this.mom = mom;
//        this.dad = dad;
//    }

    public Rule(List<Feature> featureList, List<Feature> targetFeatureList)
    {
        featureList.forEach(feature -> this.addPrecondition(new PreCondition(feature)));
        targetFeatureList.forEach(targetFeature -> this.addPostCondition(new PostCondition(targetFeature)));
        this.setTargetFeatures(targetFeatureList);
    }

    @Override
    public Comparable classifyExample(Map<String, Comparable> example)
    {
        if(this.test(example))return getPostConditions().get(0).getValues().get(0);// fixme, first condition and value
        return null;
    }

    public void addPrecondition(PreCondition preCondition)
    {
        if(preConditions == null) preConditions = new ArrayList<>();
        preConditions.add(preCondition);
    }

    public void addPostCondition(PostCondition postCondition)
    {
        if(postConditions == null) postConditions = new ArrayList<>();
        postConditions.add(postCondition);
    }

    public Predicate<Map<String,Comparable>> getPreconditionPredicate()
    {
        Predicate<Map<String,Comparable>> combinedPredicate = null;
        for (Predicate<Map<String, Comparable>> prePredicate : preConditions) {
            if(combinedPredicate == null) combinedPredicate = prePredicate;
            else combinedPredicate.and(prePredicate);
        }
        return combinedPredicate;
    }

    public List<PreCondition> getPreConditions()
    {
        return preConditions;
    }

    public List<PostCondition> getPostConditions()
    {
        return postConditions;
    }
    @Override
    public boolean test(Map<String, Comparable> stringComparableMap)
    {
        return getPreconditionPredicate().test(stringComparableMap);
    }
}
