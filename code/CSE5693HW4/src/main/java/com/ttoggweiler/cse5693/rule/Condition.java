package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.PreCheck;
import com.ttoggweiler.cse5693.util.RandomUtil;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * Conditions make up a Rule's {@link Rule} precondition set
 */
public class Condition implements Predicate<Map<String,? extends Comparable>>
{
    //todo string value of condition
    private Feature conditionFeature;
    //private List<Predicate<Map<String,? extends Comparable>>> featurePredicates = new ArrayList<>();
    private SortedMap<Comparable,Boolean> featureConditions;


    public Condition(Feature<? extends Comparable> feature)
    {
        this.setFeature(feature);
        this.featureConditions = new TreeMap<>();

        if(feature.isContinuous())
        {
            Comparable randValue = PreCheck.notEmpty(feature.getValues())
                    ? RandomUtil.selectRandomElement(feature.getValues())
                    : RandomUtil.rand.nextFloat()*100;
            featureConditions.put(randValue,Boolean.TRUE);
        }else{
            for (Comparable featureValue : feature.getValues())
                featureConditions.put(featureValue, RandomUtil.probability(.5)
                        ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public Condition(Feature<? extends Comparable> feature,SortedMap<Comparable,Boolean> valueConditions)
    {
        this.setFeature(feature);
        this.setFeatureConditions(valueConditions);
    }

    @Override
    public boolean test(Map<String,? extends Comparable> stringComparableMap)
    {
        Comparable exampleValue = stringComparableMap.get(conditionFeature.getName());
        return conditionFeature.isContinuous()
                ? featureConditions.firstKey().compareTo(exampleValue) >= 0
                : featureConditions.get(exampleValue);
    }

    public boolean isValidCondition(boolean isTargetFeaure)
    {
        return isTargetFeaure
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

    public Feature getConditionFeature()
    {
        return conditionFeature;
    }

    public void setFeature(Feature conditionFeature)
    {
        this.conditionFeature = conditionFeature;
    }
}
