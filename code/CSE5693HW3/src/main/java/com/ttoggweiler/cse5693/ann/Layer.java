package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.Identity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a layer of Nodes in the network
 */
public class Layer extends Identity
{
    private Boolean isInputLayer;
    private List<Node> nodesInLayer;
    private LayerResult mostRecentLayerResult;

    public Layer(String name, Boolean isInput, int numberOfNodes)
    {
        if(isInput!=null) this.isInputLayer = isInput;
        this.setName(name);
        nodesInLayer = new ArrayList<>(numberOfNodes);
        for (int i = 0; i < numberOfNodes; i++) {
            nodesInLayer.add(new Node("N"+i,isInput));
        }
    }

    public List<Node> getNodes()
    {
        return this.nodesInLayer;
    }

    public LayerResult getMostRecentLayerResult()
    {
        return mostRecentLayerResult;
    }

    public void setNodeValues(Map<String, Double> inputValues)
    {
        if(isHiddenLayer())throw new IllegalArgumentException("Only input and output layers can set values");
        for (Node node : nodesInLayer) {
            node.setNodeValue(inputValues.get(node.getName()));
        }
    }

    public LayerResult feedForward()
    {
        Set<NodeResult> nodeResults = new HashSet<>(nodesInLayer.size());
        for (Node node : nodesInLayer) {
            NodeResult nodeResult = node.feedForward();
            nodeResults.add(nodeResult);
        }
        mostRecentLayerResult = new LayerResult(nodeResults);
        return mostRecentLayerResult;
    }

    public void backPropagate()
    {
        if(isInputLayer())return;
        for (Node node : nodesInLayer) {
            node.backPropagate();
        }
    }

    public void updateWeights(Double learningRate)
    {
        for (Node node : nodesInLayer) {
            node.updateWeights(learningRate);
        }
    }

    public boolean isInputLayer()
    {
        return (isInputLayer !=null) && isInputLayer;
    }

    public boolean isOutputLayer()
    {
        return (isInputLayer !=null) && !isInputLayer;
    }

    public boolean isHiddenLayer()
    {
        return isInputLayer == null;
    }

    public String getTopologyString()
    {
        StringBuilder topologyStringBuilder = new StringBuilder("-Layer: "+getName()+"\n");
        for(Node node : nodesInLayer)
        {
            topologyStringBuilder.append("\t\t");
            topologyStringBuilder.append(node.getTopologyString());
            topologyStringBuilder.append("\n");
        }
        return topologyStringBuilder.toString();
    }
}
