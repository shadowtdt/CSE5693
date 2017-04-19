package com.ttoggweiler.cse5693.tree;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.PreCheck;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represents a node in decision tree
 */
public class FeatureNode extends Node<Feature>
{
    private int predictionTreeDepthLimit = Integer.MAX_VALUE;
    private Map<Predicate<Map<String, Comparable>>,FeatureNode> edges; // Decisions, predicate dictates which child based on input
    private Map<Comparable, Integer> targetFeatureDistribution;
    private String parentEdgeName = "";
    private Predicate<Map<String, Comparable>> parentPredicate;
    private Set<Feature> remainingFeatures;
    private Set<Comparable> edgeValues = new HashSet<>();

    public FeatureNode(Feature feature, Collection<Feature> remainingFeatures)
    {
        super(feature.getName(),null,feature);
        this.remainingFeatures = new HashSet<>(remainingFeatures);
    }


    public void addEdge(String edgeName, Comparable edgeValue, FeatureNode edge, boolean negateValue)
    {
        if(PreCheck.isEmpty(edges)) edges = new HashMap<>();
        Predicate<Map<String, Comparable>> edgePredicate = negateValue
                ? getData().getPredicateForValue(edgeValue).negate()
                : getData().getPredicateForValue(edgeValue);
        edgeValues.add(edgeValue);
        edges.put(edgePredicate,edge);
        this.setChildNode(edge);
        edge.setParentEdge(edgeName,edgePredicate);
        edge.setParentNode(this);
    }

    public void setTargetDistributions(Map<Comparable,Integer> distributions)
    {
       // PreCheck.ifEmpty(()-> new IllegalArgumentException("Unable to set distributions that are null or empty"),distributions);
        this.targetFeatureDistribution = PreCheck.isEmpty(distributions)
                ? Collections.EMPTY_MAP
                : distributions;
    }

    public void setParentEdge(String parentEdgeValue, Predicate<Map<String, Comparable>> parentEdgePredicate)
    {
        this.parentEdgeName = parentEdgeValue;
        this.parentPredicate = parentEdgePredicate;
    }

    public String getParentEdgeName()
    {
        return this.parentEdgeName;
    }

    public Map<Comparable,Integer> getTargetDistributions()
    {
        return targetFeatureDistribution;
    }

    public Predicate<Map<String, Comparable>> getPredicateForEdge(FeatureNode edge)
    {
        return edges.entrySet().stream()
                .filter(e -> e.getValue().equals(edge))
                .map(Map.Entry :: getKey)
                .findAny().orElse(null);
    }

    public Map<Comparable,Integer> getClassificationDistribution(Map<String,Comparable> input)
    {
        return getClassificationLeaf(input).getTargetDistributions();
        // No children, input does not have this feature, no matching edge found
    }

    public Comparable getClassification(Map<String,Comparable> input)
    {
        return this.getClassificationLeaf(input).getMostCommonValue();
    }

    public FeatureNode getClassificationLeaf(Map<String,Comparable> input)
    {
        if(distanceFromRoot() >= ((FeatureNode)getRootNode()).getPredictionDepth())
            return this;
        if(hasChildren() && input.containsKey(getData().getName())) // if there are children and input contains this feature
        {
            Optional<FeatureNode> oNextNode = edges.entrySet().stream()
                    .filter(e -> e.getKey().test(input)) // Use predicates to test the value for matching children
                    .map(Map.Entry:: getValue)
                    .findAny(); // TODO: ttoggweiler 2/17/17 handle multiple children or error

            // Step to next branch in tree and pass input to make a decision
            if(oNextNode.isPresent())return oNextNode.get().getClassificationLeaf(input);
        }
        return this;
    }

    public Comparable getMostCommonValue()
    {
        return getTargetDistributions().entrySet().stream()
            .max((a,b) -> a.getValue() > b.getValue()? 1 : -1)
            .map(Map.Entry :: getKey)
            .orElse(null);
    }

    public Predicate<Map<String, Comparable>> getParentEdgePredicate()
    {
        return parentPredicate;
    }

    public Predicate<Map<String, Comparable>> getPathPredicate()
    {
        if(getParentNode().isPresent())
        {
            FeatureNode parent = getParentNode().map(n -> ((FeatureNode) n)).get();
            return  parent.getPathPredicate().and(getParentEdgePredicate());
        }
        return (x) -> true;
    }

    public String toTreeString()
    {
        String treeStr = "";
        //for (int i = 1; i < distanceFromRoot(); i++) treeStr += "|\t";
        treeStr += getParentNode().map(n -> n.getName() + getParentEdgeName()).orElse("Root");
        if(PreCheck.notEmpty(edges))
        {
            for (Map.Entry<Predicate<Map<String, Comparable>>, FeatureNode> edge : edges.entrySet()) {
                treeStr += "\n";

                    for (int i = 0; i < distanceFromRoot(); i++) treeStr += "|\t";
                    treeStr += edge.getValue().toTreeString();

            }
            return treeStr;
        }else return  treeStr + " : " +targetFeatureDistribution;//" : " + targetFeatureDistribution.toString();
    }

    public String toPathTree()
    {
        String treeStr = getParentNode().map(n -> ((FeatureNode) n)).map(n -> n.toPathTree()).orElse("");
        treeStr += "\n";
        for (int i = 1; i < distanceFromRoot(); i++) treeStr += "|\t";
        treeStr += getParentNode().map(n -> n.getName() + getParentEdgeName()).orElse("Root");
        if(!hasChildren()) treeStr += " : " + targetFeatureDistribution;
        return treeStr;
    }

    public String toPathString(boolean leaf)
    {
        String treeStr = getParentNode().map(n -> ((FeatureNode) n)).map(n -> n.toPathString(false)).orElse("");
        if(PreCheck.notEmpty(treeStr)) treeStr+= " AND ";
        treeStr += this.parentEdgeString(leaf);
        return treeStr;
    }

    public String parentEdgeString(Boolean includeDist)
    {
        String edgeString = getParentNode().map(n -> n.getName() + getParentEdgeName()).orElse("");
        if(includeDist) edgeString += " : "+getTargetDistributions();
        return edgeString;
    }

    public List<FeatureNode> getLeafs()
    {
        ArrayList<FeatureNode> leafs = new ArrayList<>();
        if (getChildrenNodes().isPresent()) {
            for (Node n : getChildrenNodes().get()) {
                leafs.addAll(((FeatureNode) n).getLeafs());
            }
        } else
            leafs.add(this);

        return leafs;
    }

    public Set<Feature> getFeatures()
    {
        return new HashSet<>(remainingFeatures);
    }

    public Set<Comparable> getEdgeValues()
    {
        return new HashSet<>(edgeValues);
    }

    public boolean hasFeatureValueBeenUsedBefore(Feature feature, Comparable value)
    {
        return (getData().equals(feature) && edgeValues.contains(value))
                || (getParentNode().isPresent() && ((FeatureNode)getParentNode().get()).hasFeatureValueBeenUsedBefore(feature,value));
    }

    public void addEdgeValue(Comparable val)
    {
        edgeValues.add(val);
    }

    public void setPredictionDepth(int depth)
    {
        if(depth > 0) predictionTreeDepthLimit = depth;
    }

    public int getPredictionDepth()
    {
        return getRootNode().equals(this)
                ? predictionTreeDepthLimit
                : ((FeatureNode)getRootNode()).getPredictionDepth();
    }


}
