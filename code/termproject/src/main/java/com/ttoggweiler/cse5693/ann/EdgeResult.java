package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.PreCheck;

/**
 * Represents X_ji * Weight_ji = X_jk calculation
 * aka: Node-To-Node calc
 */
public class EdgeResult
{
    private String sourceNodeID;
    private String targetNodeID;
    private Double output;
    private Double weight;
    private Double error;

    public EdgeResult(Node sourceNode, Node targetNode, Double output, Double weight)
    {
        PreCheck.ifNull("Unable to create edge result with incomplete information",sourceNode,targetNode, output,weight);
        this.sourceNodeID = sourceNode.getId().toString();
        this.targetNodeID = targetNode.getId().toString();
        this.output = output;
        this.weight = weight;
    }

    public String getSourceNodeID()
    {
        return sourceNodeID;
    }

    public void setSourceNodeID(String sourceNodeID)
    {
        this.sourceNodeID = sourceNodeID;
    }

    public String getTargetNodeID()
    {
        return targetNodeID;
    }

    public void setTargetNodeID(String targetNodeID)
    {
        this.targetNodeID = targetNodeID;
    }

    public Double getOutput()
    {
        return output;
    }

    public void setOutput(Double input)
    {
        this.output = input;
    }

    public Double getWeightedOutput()
    {
        return output * weight;
    }

    public Double getWeight()
    {
        return weight;
    }

    public void setWeight(Double weight)
    {
        this.weight = weight;
    }

    public Double getError()
    {
        return this.error;
    }

    public void setError(Double errorValue)
    {
        PreCheck.ifNull("Null error value is not allowed",errorValue);
        this.error = errorValue;
    }

    public Double getWeightedError()
    {
        return error * weight;
    }


}
