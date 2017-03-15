package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an edge in the network
 */
public class Edge extends Identity
{
    private static Logger log = LoggerFactory.getLogger(Edge.class);

    private static final Double DEFAULT_STARTING_WEIGHT = 0d;
    private Node sourceNode;
    private Node targetNode;
    private List<Double> weightHistory;
    private EdgeResult lastEdgeResult;

    public Edge(String name, Node sourceNode, Node targetNode, Double startingWeight)
    {
        PreCheck.ifNull("Must provide a source and target node",sourceNode,targetNode);
        if(sourceNode.equals(targetNode))throw new IllegalArgumentException("Source and target node cannot be equal");
        setName(name);
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.setWeight(PreCheck.defaultTo(startingWeight,DEFAULT_STARTING_WEIGHT));
        this.sourceNode.addOutputEdge(this);
        this.targetNode.addInputEdge(this);
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
        return targetNode;
    }
    
    public EdgeResult getLastEdgeResult()
    {
        return lastEdgeResult;
    }
    
    public EdgeResult feedForward(Double nodeInput)
    {
        lastEdgeResult = new EdgeResult(sourceNode,targetNode,nodeInput,getWeight());
        targetNode.addInputEdgeResult(lastEdgeResult);
        return lastEdgeResult;
    }

    public void updateWeights(Double learningRate)
    {
        Double weightDelta = (learningRate) * sourceNode.getLastNodeResult().getValue() * targetNode.getLastNodeResult().getError();
        Double newWeight = getWeight() + weightDelta;
        //log.debug("{}: {} -> {}",getName(),getWeight(),newWeight);
        this.setWeight(newWeight);
    }

    public String toString()
    {
        String edgeString =  "-Edge: "+getName()+ " --> "+ targetNode.getName() + " W("+getWeight()+")";
        if (lastEdgeResult != null && lastEdgeResult.getError() != null)
            edgeString += " WE("+ lastEdgeResult.getWeightedError()+")";
        return edgeString;
    }

}
