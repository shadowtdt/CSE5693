package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a layer of Nodes in the network
 */
public class Layer extends Identity
{
    private Boolean isInputLayer;
    private Set<Node> nodesInLayer;
    private LayerResult mostRecentLayerResult;

    public Layer(String name, Boolean isInput, int numberOfNodes)
    {
        if(isInput!=null) this.isInputLayer = isInput;
        this.setName(name);
        nodesInLayer = new HashSet<>(numberOfNodes);
        for (int i = 0; i < numberOfNodes; i++) {
            nodesInLayer.add(new Node("N"+i,isInput));
        }
    }

    public Set<Node> getNodes()
    {
        return this.nodesInLayer;
    }

    public LayerResult getMostRecentLayerResult()
    {
        return mostRecentLayerResult;
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

    public boolean isInputLayer()
    {
        return (isInputLayer !=null) && isInputLayer;
    }

    public boolean isOutputLayer()
    {
        return (isInputLayer !=null) && !isInputLayer;
    }

    public boolean inHiddenLayer()
    {
        return isInputLayer == null;
    }

}
