package com.ttoggweiler.cse5693.tree;

import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by ttoggweiler on 2/17/17.
 */
public class FeatureNode extends Node<Feature>
{
    private static Logger log = LoggerFactory.getLogger(FeatureNode.class);

    private Map<Predicate<Map<String, Comparable>>,FeatureNode> edges; // Decisions, predicate dictates which child based on input
    private Map<Comparable, Integer> targetFeatureDistribution;
    private String parentEdgeName = "";
    private Predicate<Map<String, Comparable>> parentPredicate;

    public FeatureNode(Feature feature)
    {
        super(feature.getName(),null,feature);
    }

    public void addEdge(String edgeName, Comparable edgeValue, FeatureNode edge)
    {
        if(PreCheck.isEmpty(edges)) edges = new HashMap<>();
        Predicate<Map<String, Comparable>> edgePredicate = this.getData().getPredicateForValue(edgeValue);
        edges.put(edgePredicate,edge);
        this.setChildNode(edge);
        edge.setParentEdge(edgeName,edgePredicate);
        edge.setParentNode(this);
    }

    public void addNegativeEdge(String edgeName, Comparable edgeValue, FeatureNode negativeEdge)
    {
        if(PreCheck.isEmpty(edges)) edges = new HashMap<>();
        Predicate<Map<String, Comparable>> edgePredicate = this.getData().getPredicateForValue(edgeValue).negate();

        edges.put(edgePredicate,negativeEdge);
        this.setChildNode(negativeEdge);
        negativeEdge.setParentEdge(edgeName,edgePredicate);
        negativeEdge.setParentNode(this);
    }

    public void setTargetDistributions(Map<Comparable,Integer> distributions)
    {
        PreCheck.ifEmpty(()-> new IllegalArgumentException("Unable to set distributions that are null or empty"),distributions);
        this.targetFeatureDistribution = distributions;
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
        if(hasChildren() && input.containsKey(getData().getName())) // if there are children and input contains this feature
        {
            Optional<FeatureNode> oNextNode = edges.entrySet().stream()
                    .filter(e -> e.getKey().test(input)) // Use predicates to test the value for matching children
                    .map(Map.Entry:: getValue)
                    .findAny(); // // TODO: ttoggweiler 2/17/17 handle multiple children or error

            if(oNextNode.isPresent())return oNextNode.get().getClassificationLeaf(input); // Step to next node in tree and pass input to make a decision
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
}
