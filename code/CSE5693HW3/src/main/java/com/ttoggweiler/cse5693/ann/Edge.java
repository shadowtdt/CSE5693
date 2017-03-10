package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an edge in the network
 */
public class Edge extends Identity
{
    private static final Double DEFAULT_STARTING_WEIGHT = 0d;
    private Node sourceNode;
    private Node TargetNode;
    private List<Double> weightHistory;
    private EdgeResult mostRecentEdgeResult;

    public Edge(Node sourceNode, Node targetNode, Double startingWeight)
    {
        PreCheck.ifNull("Must provide a source and target node",sourceNode,targetNode);
        if(sourceNode.equals(targetNode))throw new IllegalArgumentException("Source and target node cannot be equal");
        this.sourceNode = sourceNode;
        this.TargetNode = targetNode;
        this.setWeight(PreCheck.defaultTo(startingWeight,DEFAULT_STARTING_WEIGHT));
        this.sourceNode.addOutputEdge(this);
        this.TargetNode.addInputEdge(this);
    }

    public void setWeight(Double newWeight)
    {
        if(weightHistory == null) weightHistory = new ArrayList<>();
        weightHistory.add(newWeight);
    }

    public Double getWeight()
    {
        return weightHistory.get(weightHistory.size()-1);
    }

    public List<Double> getWeightHistory()
    {
        return weightHistory;
    }
    
    public Node getSourceNode()
    {
        return sourceNode;
    }

    public Node getTargetNode()
    {
        return TargetNode;
    }
    
    public EdgeResult getMostRecentEdgeResult()
    {
        return mostRecentEdgeResult;
    }
    
    public EdgeResult feedForward(Double nodeInput)
    {
        Double result = multiplyInputByWeight(nodeInput);
        mostRecentEdgeResult = new EdgeResult(this.sourceNode.getId().toString(),this.TargetNode.getId().toString(),nodeInput,result,getWeight());
        TargetNode.addInputEdgeResult(mostRecentEdgeResult);
        return mostRecentEdgeResult;
    }
    
    public Double multiplyInputByWeight(Double nodeInput)
    {
        if(nodeInput == null)throw new NullPointerException("Edge unable to Feed-Forward with null input.");
        return nodeInput * getWeight();
    }

}
