package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.RandomUtil;

import java.util.ArrayList;
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

//    private Rule mom;
//    private Rule dad;
    // todo map string -> condition
    private List<Condition> preConditions;
//    private List<Condition> postConditions;
    private Map<String,Comparable> prediction = new HashMap<>();

//    public Rule(List<PreCondition> preConditionList, List<PostCondition> postConditionList, Rule mom, Rule dad)
//    {
//        preConditionList.forEach(this :: addPrecondition);
//        postConditionList.forEach(this::addPostCondition);
//        this.mom = mom;
//        this.dad = dad;
//    }

    public Rule(List<Feature<? extends Comparable>> featureList, List<Feature<? extends Comparable>> targetFeatureList)
    {
        this.setTargetFeatures(targetFeatureList);
        this.setFeatures(featureList);
        this.setPreConditions(featureList.stream().map(Condition::new).collect(Collectors.toList()));

        for (Feature<? extends Comparable> feature : targetFeatureList) {
            Comparable c = RandomUtil.selectRandomElement(feature.getValues());
            prediction.put(feature.getName(),c);
        }
        targetFeatureList.forEach(tf -> prediction.put(tf.getName(), RandomUtil.selectRandomElement(tf.getValues())));
    }



    @Override
    public Map<String,? extends Comparable> classifyExample(Map<String, ? extends Comparable> example)
    {
        return test(example) ? prediction : Collections.emptyMap();
    }

    @Override
    public int getNumberOfBits()
    {
        return preConditions.stream().mapToInt(c -> c.getConditionPredicates().size()).sum();
                // fixme + getTargetFeatures().size();
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

    public void setPreConditions(List<Condition> preConditions)
    {
        this.preConditions = preConditions;
    }

//    public void addPostCondition(Condition postCondition)
//    {
//        if(postConditions == null) postConditions = new ArrayList<>();
//        postConditions.add(postCondition);
//    }

    public List<Condition> getPreConditions()
    {
        return preConditions;
    }

//    public List<Condition> getPostConditions()
//    {
//        return postConditions;
//    }

}
