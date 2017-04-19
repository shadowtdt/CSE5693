package com.ttoggweiler.cse5693.genetic.population;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.genetic.GeneticSearch;
import com.ttoggweiler.cse5693.rule.Condition;
import com.ttoggweiler.cse5693.rule.Hypothesis;
import com.ttoggweiler.cse5693.rule.Rule;
import com.ttoggweiler.cse5693.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 4/3/17.
 */
public class OffspringFactory
{
    private static Logger log = LoggerFactory.getLogger(OffspringFactory.class);

    public static Collection<Hypothesis> singlePointRuleCross(Collection<Hypothesis> parents)
    {
        if(parents.size() % 2 !=  0)
            parents.remove(parents.iterator().next());
            //throw new IllegalArgumentException("Unable to generate offspring with odd number of parents! ("+parents.size()+")");

        List<Hypothesis> unorderedParents = parents.stream().collect(Collectors.toList());
        Collections.shuffle(unorderedParents);

        Collection<Hypothesis> offspring = new HashSet<>();
        Iterator<Hypothesis> itr = unorderedParents.iterator();
        while(itr.hasNext())
        {
            Hypothesis mom = itr.next();
            Hypothesis dad = itr.next();

            boolean validOffSpring = false;
            while(!validOffSpring) {
                int momSplitIndex = RandomUtil.rand.nextInt(mom.getRuleList().size()) + 1;
                int dadSplitIndex = RandomUtil.rand.nextInt(dad.getRuleList().size()) + 1;

                List<Rule> daughterRules = new ArrayList<>();
                List<Rule> sonRules = new ArrayList<>();
                daughterRules.addAll(mom.getRuleList().subList(0, momSplitIndex));
                sonRules.addAll(dad.getRuleList().subList(0, dadSplitIndex));

                if (dad.getRuleList().size() > dadSplitIndex + 1)
                    daughterRules.addAll(dad.getRuleList().subList(dadSplitIndex + 1, dad.getRuleList().size()));
                if (mom.getRuleList().size() > momSplitIndex + 1)
                    sonRules.addAll(mom.getRuleList().subList(momSplitIndex + 1, mom.getRuleList().size()));

                Hypothesis daughter = new Hypothesis(0, mom.getFeatures(), mom.getTargetFeatures());
                daughter.setRules(daughterRules);

                Hypothesis son = new Hypothesis(0, dad.getFeatures(), dad.getTargetFeatures());
                son.setRules(sonRules);

                //Because rules are not modified
                validOffSpring = true;
                offspring.add(daughter);
                offspring.add(son);
            }

        }
        return offspring;
    }

    public static Collection<Hypothesis> randomPointConditionCross(Collection<Hypothesis> parents)
    {
        if(parents.size() % 2 !=  0)
            parents.remove(parents.iterator().next());

        ListIterator<Hypothesis> itr = getShuffledIterator(parents);
        Collection<Hypothesis> offspring = new HashSet<>();
        while(itr.hasNext())
        {
            Hypothesis mom = itr.next();
            Hypothesis dad = itr.next();

            //log.info("Mom {}",mom.getClassifierString(false));
            //log.info("Dad {}",dad.getClassifierString(false));

            boolean validOffSpring = false;
            while(!validOffSpring) {
                List<Rule> allRules = new ArrayList<>();
                int conditionCount = mom.getFeatures().size() + mom.getTargetFeatures().size();
                allRules.addAll(mom.getRuleList());
                allRules.addAll(dad.getRuleList());

                Map<String,List<Condition>> preMap = new HashMap<>();
                Map<String,List<Condition>> postMap = new HashMap<>();

                // Collect conditions by what features they represent
                for (Rule rule : allRules) {
                    for (Condition condition : rule.getPreConditions()) {
                        preMap.putIfAbsent(condition.getFeature().getName(),new ArrayList<>(allRules.size()));
                        preMap.get(condition.getFeature().getName()).add(condition);
                    }
                    for (Condition condition : rule.getPostConditions()) {
                        postMap.putIfAbsent(condition.getFeature().getName(),new ArrayList<>(allRules.size()));
                        postMap.get(condition.getFeature().getName()).add(condition);
                    }
                }

                preMap.replaceAll((k,v) -> RandomUtil.shuffle(v));
                postMap.replaceAll((k,v) -> RandomUtil.shuffle(v));

                // Create new rules by selecting index of condition map
                List<Rule> newRules = new ArrayList<>();
                for (int i = 0; i < allRules.size(); i++) {
                    List<Condition> newPre = new ArrayList<>();
                    List<Condition> newPost = new ArrayList<>();
                    for (Map.Entry<String, List<Condition>> entry : preMap.entrySet()) {
                        newPre.add(new Condition(entry.getValue().get(i)));
                    }
                    for (Map.Entry<String, List<Condition>> entry : postMap.entrySet()) {
                        newPost.add(new Condition(entry.getValue().get(i)));
                    }
                    newRules.add(new Rule(mom.getFeatures(),mom.getTargetFeatures(),newPre,newPost));
                }

                Hypothesis daughter = new Hypothesis(0, mom.getFeatures(), mom.getTargetFeatures());
                daughter.setRules(newRules.subList(0,newRules.size()/2));

                Hypothesis son = new Hypothesis(0, dad.getFeatures(), dad.getTargetFeatures());
                son.setRules(newRules.subList((newRules.size()/2),newRules.size()));

                //Because rules are not modified
                validOffSpring = true;
                offspring.add(daughter);
                offspring.add(son);
                //log.info("Daughter: {}",daughter.getClassifierString(false));
                //log.info("Son: {}",son.getClassifierString(false));

            }

        }
        return offspring;
    }

    public static ListIterator<Hypothesis> getShuffledIterator(Collection<Hypothesis> collection)
    {
        return RandomUtil.shuffle(collection).listIterator();
    }
}
