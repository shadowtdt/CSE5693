package com.ttoggweiler.cse5693.predict;

import com.ttoggweiler.cse5693.feature.Feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a classifier that has multiple rules
 */
public class RuleSetClassifier extends Classifier
{
    private List<Rule> ruleList;

    public RuleSetClassifier(int ruleCount, List<Feature> features, List<Feature> targetFeatures)
    {
        this.setFeatures(features);
        this.setTargetFeatures(targetFeatures);
        ruleList = new ArrayList<>(ruleCount);
        for (int i = 0; i <= ruleCount; i++) {
            ruleList.add(new Rule(features,targetFeatures));
        }
    }

    @Override
    public Map<String,Comparable> classifyExample(Map<String,Comparable> example)
    {
        for (Rule rule : ruleList) {
            if(rule.isCorrectClassification(example))
                return rule.classifyExample(example);
        }
        return Collections.emptyMap();
    }

    @Override
    public String getClassifierString(boolean includeName)
    {
        return getRuleList().stream()
                .map(r -> r.getClassifierString(includeName))
                .collect(Collectors.joining(" OR\n","\n{","}"));
    }

    public void setRules(List<Rule> rules)
    {
        ruleList = rules;
    }

    public List<Rule> getRuleList()
    {
        return this.ruleList;
    }

}
