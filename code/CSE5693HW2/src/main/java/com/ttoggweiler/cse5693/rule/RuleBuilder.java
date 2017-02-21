package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.learner.Examiner;
import com.ttoggweiler.cse5693.tree.Feature;
import com.ttoggweiler.cse5693.tree.FeatureNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 2/20/17.
 */
public class RuleBuilder
{
    private static Logger log = LoggerFactory.getLogger(RuleBuilder.class);

    public static List<Rule> nodesToRules(List<FeatureNode> nodes)
    {
        return nodes.stream()
                .map(n -> new Rule(n))
                .collect(Collectors.toList());
    }

    public static List<Rule> pruneRules(Feature target, List<Rule> rulesToPrune, List<Map<String, Comparable>> datas)
    {
        List<Rule> rules = new ArrayList<>(rulesToPrune);
        Double accuracy = Examiner.getAccuracy(target,rules,datas);
        Set<Rule> rulesToRemove = new HashSet<>();
        Set<Rule> removedRulesParents = new HashSet<>();
        for(Rule rule : rules)
        {
            Double newAccuracy = Examiner.getAccuracy(target,rules.stream().filter(r -> r!= rule).collect(Collectors.toList()), datas);
            log.debug("Accuracy Delta: {} -> {} for rule: {}",accuracy,newAccuracy,rule.toString());
            if(newAccuracy > accuracy) {
                rulesToRemove.add(rule);
                rule.getClassificationLeaf().getParentNode().ifPresent(n -> removedRulesParents.add(new Rule((FeatureNode)n)));
                log.info("Pruning rule {}",rule.toString());
            }
        }
        rules.removeAll(rulesToRemove);
        rules.addAll(removedRulesParents);
        log.info("Pruned {} rules.",rulesToRemove.size());
        return rules;
    }

}