package com.ttoggweiler.cse5693.tree;

import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by ttoggweiler on 2/20/17.
 */
public class Rule
{
    FeatureNode leafNode;
    Predicate<Map<String, Comparable>> pathPredicate;

    public Rule(FeatureNode node)
    {
        PreCheck.ifNull("Unable to create rule from null Node",node);
        leafNode = node;
        pathPredicate = node.getPathPredicate();
    }

    public boolean test(Map<String, Comparable> value)
    {
        return pathPredicate.test(value);
    }

}
