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
    private Double input;
    private Double output;
    private Double weight;
    private Double error;

    public EdgeResult(String sourceNodeID, String targetNodeID,Double input, Double output,Double weight)
    {
        PreCheck.ifNull("Unable to create edge result with incomplete information",sourceNodeID,targetNodeID,input,output,weight);
        this.sourceNodeID = sourceNodeID;
        this.targetNodeID = targetNodeID;
        this.input = input;
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

    public Double getInput()
    {
        return input;
    }

    public void setInput(Double input)
    {
        this.input = input;
    }

    public Double getOutput()
    {
        return output;
    }

    public void setOutput(Double output)
    {
        this.output = output;
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
        return getError() * getWeight();
    }
}
