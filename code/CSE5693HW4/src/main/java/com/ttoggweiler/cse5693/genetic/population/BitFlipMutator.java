package com.ttoggweiler.cse5693.genetic.population;

import com.ttoggweiler.cse5693.genetic.GeneticSearch;
import com.ttoggweiler.cse5693.rule.Condition;
import com.ttoggweiler.cse5693.rule.Hypothesis;
import com.ttoggweiler.cse5693.rule.Rule;
import com.ttoggweiler.cse5693.util.RandomUtil;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.SortedMap;
import java.util.logging.Logger;

/**
 * Mutator what will flip random bits based on a provided mutation rate
 */
public class BitFlipMutator implements Mutator
{
    private org.slf4j.Logger log = LoggerFactory.getLogger(BitFlipMutator.class);

    @Override
    public Hypothesis mutate(Hypothesis hypothesis)
    {
        boolean validMutation = false;
        //log.info("Before Mutate {}",hypothesis.getClassifierString(false));
        while(! validMutation) {
            boolean preOrPost = RandomUtil.probability(1);
            Rule randomRule = RandomUtil.selectRandomElement(hypothesis.getRuleList());
            List<Condition> cons = randomRule.getPreConditions();
            int condIndex = RandomUtil.rand.nextInt(cons.size());
            Condition randomCondition = cons.get(condIndex);

            SortedMap<Comparable, Boolean> values = randomCondition.getFeatureConditions();
            Comparable valueKey = RandomUtil.selectRandomElement(values.keySet());

            Condition newCondition = new Condition(randomCondition);
            newCondition.getFeatureConditions().replace(valueKey, !values.get(valueKey));
            cons.add(condIndex,newCondition);
            validMutation = randomCondition.isValidCondition();

        }
        //log.info("After Mutate: {}",hypothesis.getClassifierString(false));

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
