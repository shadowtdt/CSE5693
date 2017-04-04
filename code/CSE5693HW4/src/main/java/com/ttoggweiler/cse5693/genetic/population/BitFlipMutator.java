package com.ttoggweiler.cse5693.genetic.population;

import com.ttoggweiler.cse5693.rule.Condition;
import com.ttoggweiler.cse5693.rule.Hypothesis;
import com.ttoggweiler.cse5693.rule.Rule;

import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Mutator what will flip random bits based on a provided mutation rate
 */
public class BitFlipMutator implements Mutator
{
    private static final Random rand = new Random();
    @Override
    public Hypothesis mutate(Hypothesis hypothesis)
    {
        return hypothesis;
//
//        for (Rule rule : hypothesis.getRuleList()) {
//            for (Condition condition : rule.getPreConditions()) {
//                for (Predicate<Map<String, ? extends Comparable>> mapPredicate : condition.getConditionPredicates()) {
//
//                }
//            }
//        }
//
//
//        int bitsPerRule = hypothesis.getRuleList().get(0).getNumberOfBits();
//        int totalBits = bitsPerRule * hypothesis.getRuleList().size();
//        int numBitsToFlip = (int)Math.round(totalBits * mutationPercent);
//        if(numBitsToFlip < 1) numBitsToFlip = 1;
//
//
//        for (int i = 0; i < numBitsToFlip; i++) {
//            int randInt = rand.nextInt(100);
//            int ruleIndex = randInt % hypothesis.getRuleList().size();
//            int conditionIndex = randInt % bitsPerRule;
//            int bitIndex = ruleIndex %
//
//            Rule rule = hypothesis.getRuleList().get(ruleIndex);
//            if(conditionIndex > rule.getPreConditions().size())
//            {
//                conditionIndex -= rule.getPreConditions().size();
//                PostCondition post = rule.getPostConditions().get(conditionIndex);
//
//            }
//        }
    }
}
