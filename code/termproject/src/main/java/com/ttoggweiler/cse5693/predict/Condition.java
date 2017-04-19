package com.ttoggweiler.cse5693.predict;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.PreCheck;
import com.ttoggweiler.cse5693.util.RandomUtil;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Conditions make up a Rule's {@link Rule} pre/post condition sets
 */
public class Condition implements Predicate<Map<String,? extends Comparable>>
{
    private Feature conditionFeature;
    boolean isTargetCondition;
    private SortedMap<Comparable,Boolean> featureConditions;


    public Condition(Feature feature, boolean isTargetCondition)
    {
        this.setFeature(feature);
        this.isTargetCondition = isTargetCondition;
        while(!this.isValidCondition()) {
            this.featureConditions = new TreeMap<>();
            if (feature.isContinuous()) {
                Comparable randValue = PreCheck.notEmpty(feature.getValues())
                        ? RandomUtil.selectRandomElement(feature.getValues())
                        : RandomUtil.rand.nextFloat() * 10;
                featureConditions.put(randValue,  Boolean.TRUE);
            } else {
                for (Comparable featureValue : feature.getValues())
                    featureConditions.put(featureValue, RandomUtil.probability(.2)
                            ? Boolean.TRUE : Boolean.FALSE);
            }
        }
    }

    public Condition(Feature feature, boolean isTargetCondition, SortedMap<Comparable,Boolean> featureConditions)
    {
        this.setFeature(feature);
        this.isTargetCondition = isTargetCondition;
        this.featureConditions = new TreeMap<>(featureConditions);
    }

    public Condition(Condition condition)
    {
        this.setFeature(condition.getFeature());
        this.isTargetCondition = condition.isTargetCondition();
        this.featureConditions = new TreeMap<>(condition.getFeatureConditions());
    }

    @Override
    public boolean test(Map<String,? extends Comparable> stringComparableMap)
    {
        Comparable exampleValue = stringComparableMap.get(conditionFeature.getName());
        return conditionFeature.isContinuous()
                ? (featureConditions.firstKey().compareTo(exampleValue) >= 0) == featureConditions.get(featureConditions.firstKey())
                : featureConditions.get(exampleValue);
    }

    public boolean isValidCondition()
    {
        if(PreCheck.isEmpty(featureConditions))return false;
        return this.isTargetCondition
                ? Collections.frequency(featureConditions.values(),Boolean.TRUE) == 1 // Target conditions can have only one prediction
                : Collections.frequency(featureConditions.values(),Boolean.TRUE) > 0; // Feature conditions must have at lease one allowed value
    }

    public SortedMap<Comparable,Boolean> getFeatureConditions()
    {
        return this.featureConditions;
    }

    public void setFeatureConditions(SortedMap<Comparable,Boolean> featureConditions)
    {
        this.featureConditions = featureConditions;
    }

    public boolean isTargetCondition()
    {
        return this.isTargetCondition;
    }

    public Feature getFeature()
    {
        return conditionFeature;
    }

    public void setFeature(Feature conditionFeature)
    {
        this.conditionFeature = conditionFeature;
    }

    public String getConditionString(boolean includeName)
    {
        return featureConditions.entrySet().stream()
                .filter(Map.Entry :: getValue)
                .map(e -> conditionFeature.isContinuous()
                        ? "<" + e.getKey().toString() //+ "=" + e.getValue()
                        : e.getKey().toString() //+ "=" + e.getValue()
                ).collect(Collectors.joining(isTargetCondition?" AND " : " OR "
                        ,conditionFeature.getName()+"{","}"));

    }
}
