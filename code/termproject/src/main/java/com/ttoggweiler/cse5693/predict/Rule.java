package com.ttoggweiler.cse5693.predict;

import com.ttoggweiler.cse5693.data.DataSet;
import com.ttoggweiler.cse5693.feature.Feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 4/3/17.
 */
public class Rule extends Classifier
{
    @Deprecated // use classifier fields/methods
    private List<Feature> targetFeatures;
    @Deprecated // use classifier fields/methods
    private List<Feature> features;

    // todo map string -> condition
    private List<Condition> preConditions;
    private List<Condition> postConditions;

    public Rule(Collection<Feature> featureList, Collection<Feature> targetFeatureList)
    {
        this.setFeatures(featureList.stream().collect(Collectors.toList()));
        this.setTargetFeatures(targetFeatureList.stream().collect(Collectors.toList()));
        this.setPreConditions(featureList.stream().map(f -> new Condition(f,false)).collect(Collectors.toList()));
        this.setPostConditions(targetFeatureList.stream().map(tf -> new Condition(tf,true)).collect(Collectors.toList()));
    }

    public Rule(Collection<Feature> featureList, Collection<Feature> targetFeatureList, List<Condition> preConditions, List<Condition> postConditions)
    {
        this.setFeatures(featureList.stream().collect(Collectors.toList()));
        this.setTargetFeatures(targetFeatureList.stream().collect(Collectors.toList()));
        this.setPreConditions(preConditions);
        this.setPostConditions(postConditions);
    }

    @Override
    public void train(DataSet trainingSet)
    {
        throw new IllegalStateException("Not Impli");
    }

    @Override
    public Map<String,Comparable> classifyExample(Map<String,Comparable> example)
    {
        Map<String,Comparable> prediction = new HashMap<>();
        for (Condition postCondition : postConditions) {
            postCondition.getFeatureConditions().entrySet().stream()
                    .filter(Map.Entry :: getValue).findAny().ifPresent(entry ->
                    prediction.put(postCondition.getFeature().name(),entry.getKey()));
        }
        return prediction;
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

}
