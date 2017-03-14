package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.ANNRunner;
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
    private Node TargetNode;
    private List<Double> weightHistory;
    private EdgeResult mostRecentEdgeResult;

    public Edge(String name, Node sourceNode, Node targetNode, Double startingWeight)
    {
        PreCheck.ifNull("Must provide a source and target node",sourceNode,targetNode);
        if(sourceNode.equals(targetNode))throw new IllegalArgumentException("Source and target node cannot be equal");
        setName(name);
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

    public void updateWeights(Double learningRate)
    {
        Double weightDelta = learningRate * mostRecentEdgeResult.getError() * mostRecentEdgeResult.getOutput();
        Double newWeight = getWeight()  + weightDelta;
        //log.debug("{}: {} -> {}",getName(),getWeight(),newWeight);
        this.setWeight(newWeight);
    }

    public Double multiplyInputByWeight(Double nodeInput)
    {
        if(nodeInput == null)throw new NullPointerException("Edge unable to Feed-Forward with null input.");
        return nodeInput * getWeight();
    }

    public String toString()
    {
        String edgeString =  "-Edge: "+getName()+ " --> "+TargetNode.getName() + " W("+getWeight()+")";
        if (mostRecentEdgeResult != null && mostRecentEdgeResult.getError() != null)
            edgeString += " WE("+mostRecentEdgeResult.getWeightedError()+")";
        return edgeString;
    }
}
