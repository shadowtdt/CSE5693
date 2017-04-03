package com.ttoggweiler.cse5693.rule;

import com.sun.org.apache.regexp.internal.RE;
import com.ttoggweiler.cse5693.feature.Feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Represents a classifier that has multiple rules
 */
public class Hypothesis extends Classifier
{
    private List<Rule> ruleList;

    public Hypothesis(int ruleCount, List<Feature> features, List<Feature> targetFeatures)
    {
        ruleList = new ArrayList<>(ruleCount);
        for (int i = 0; i < ruleCount; i++) {
            ruleList.add(new Rule(features,targetFeatures));
        }
    }

    public void addRule(Rule rule)
    {
        if(ruleList == null) ruleList = new ArrayList<>();
        ruleList.add(rule);
    }

    public List<Rule> getRuleList()
    {
        return this.ruleList;
    }

    public Predicate<Map<String,Comparable>> getPredicate()
    {
        Predicate<Map<String,Comparable>> combinedPredicate = null;
        for (Predicate<Map<String, Comparable>> featurePredicate : ruleList) {
            if(combinedPredicate == null) combinedPredicate = featurePredicate;
            else combinedPredicate.or(featurePredicate);
        }
        return combinedPredicate;
    }

    @Override
    public Comparable classifyExample(Map<String, Comparable> example)
    {
        for (Rule rule : ruleList) {
            if(rule.test(example))
                return rule.classifyExample(example);
        }
        return null;
    }
}
