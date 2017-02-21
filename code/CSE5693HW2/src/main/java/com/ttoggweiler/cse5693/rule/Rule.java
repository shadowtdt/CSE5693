package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.tree.FeatureNode;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by ttoggweiler on 2/20/17.
 */
public class Rule
{
    FeatureNode leafNode;
    Predicate<Map<String, Comparable>> pathPredicate;
    String predicateString;
    public Rule(FeatureNode node)
    {
        PreCheck.ifNull("Unable to create rule from null Node",node);
        leafNode = node;
        pathPredicate = node.getPathPredicate();
        predicateString = leafNode.toPathString(true);

    }

    public boolean test(Map<String, Comparable> value)
    {
        return pathPredicate.test(value);
    }

    public Map<Comparable,Integer> getClassificationDistribution()
    {
        return leafNode.getTargetDistributions();
    }
    public Comparable getClassification(Map<String,Comparable> input)
    {
        return leafNode.getMostCommonValue();
    }

    public FeatureNode getClassificationLeaf()
    {
        return leafNode;
    }

    public String toString()
    {
        return predicateString;
    }
}
