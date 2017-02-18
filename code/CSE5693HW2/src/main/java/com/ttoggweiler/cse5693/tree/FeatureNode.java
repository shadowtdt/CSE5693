package com.ttoggweiler.cse5693.tree;

import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 2/17/17.
 */
public class FeatureNode extends Node<Feature>
{
    private Map<Predicate<Comparable>,FeatureNode> edges; // Decisions, predicate dictates which child based on input
    private Map<Comparable, Integer> targetFeatureDistribution;
    private String parentEdgeValue = "";

    public FeatureNode(Feature feature)
    {
        super(feature.getName(),null,feature);
    }

    public void setDecisionEdge(String edgeValue , Predicate<Comparable> condition, FeatureNode edge)
    {
        if(PreCheck.isEmpty(edges)) edges = new HashMap<>();
        edges.put(condition,edge);
        this.setChildNode(edge);
        edge.setParentEdgeValue(edgeValue);
        edge.setParentNode(this);
    }

    public void setTargetDistributions(Map<Comparable,Integer> distributions)
    {
        PreCheck.ifEmpty(()-> new IllegalArgumentException("Unable to set distributions that are null or empty"),distributions);
        this.targetFeatureDistribution = distributions;
    }

    public void setParentEdgeValue(String parentEdgeValue)
    {
        this.parentEdgeValue = parentEdgeValue;
    }

    public String getParentEdgeValue()
    {
        return this.parentEdgeValue;
    }
    public Map<Comparable,Integer> getTargetDistributions()
    {
        return targetFeatureDistribution;
    }

    public Map<Comparable,Integer> decide(Map<String,String> input)
    {
        if(hasChildren() && input.containsKey(getData().getName())) // if there are children and input contains this feature
        {
            String featureValue =  input.get(getData().getName()); // get the feature value from the input
            Optional<FeatureNode> oNextNode = edges.entrySet().stream()
                    .filter(e -> e.getKey().test(featureValue)) // Use predicates to test the value for matching children
                    .map(Map.Entry:: getValue)
                    .findAny(); // // TODO: ttoggweiler 2/17/17 handle multiple children

            if(oNextNode.isPresent())return oNextNode.get().decide(input); // Step to next node in tree and pass input to make a decision
        }

        // No children, input does not have this feature, no matching edge found
        return targetFeatureDistribution;
    }

    public String toString()
    {
        if(hasChildren())return getData().getName() + " = "+getParentEdgeValue();
        else return getData().getName()+" : "+targetFeatureDistribution.toString();
    }
}
