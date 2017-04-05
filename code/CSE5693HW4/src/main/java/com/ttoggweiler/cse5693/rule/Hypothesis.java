package com.ttoggweiler.cse5693.rule;

import com.sun.org.apache.regexp.internal.RE;
import com.ttoggweiler.cse5693.feature.Feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Represents a classifier that has multiple rules
 */
public class Hypothesis extends Classifier
{
    private List<Rule> ruleList;
    public Hypothesis(int ruleCount, List<Feature<? extends Comparable> > features, List<Feature<? extends Comparable> > targetFeatures)
    {
        this.setFeatures(features);
        this.setTargetFeatures(targetFeatures);
        ruleList = new ArrayList<>(ruleCount);
        for (int i = 0; i <= ruleCount; i++) {
            ruleList.add(new Rule(features,targetFeatures));
        }
    }

    @Override
    public Map<String,? extends Comparable> classifyExample(Map<String, ? extends Comparable> example)
    {
        for (Rule rule : ruleList) {
            if(rule.test(example))
                return rule.classifyExample(example);
        }
        return Collections.emptyMap();
    }

    @Override
    public int getNumberOfBits()
    {
        return ruleList.stream().mapToInt(Rule::getNumberOfBits).sum();
    }

    public void setRules(List<Rule> rules)
    {
        ruleList = rules;
    }

    public List<Rule> getRuleList()
    {
        return this.ruleList;
    }

    @Override
    public Predicate<Map<String,? extends Comparable>> getPredicate()
    {
        Predicate<Map<String,? extends Comparable>> combinedPredicate = null;
        for (Predicate<Map<String, ? extends Comparable>> featurePredicate : ruleList) {
            if(combinedPredicate == null) combinedPredicate = featurePredicate;
            else combinedPredicate.or(featurePredicate);
        }
        return combinedPredicate;
    }
}
