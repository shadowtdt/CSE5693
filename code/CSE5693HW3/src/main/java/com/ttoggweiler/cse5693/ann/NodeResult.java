package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.Set;

/**
 * Represents Sum( X_ji * W_ji ) * Squashing function(sigmoid) = X_jk
 * aka: InputNodes-To-OutputNodes calculation
 */
public class NodeResult
{
    private Double value;
    private Set<EdgeResult> inputEdgeResults;
    private Set<EdgeResult> outputEdgeResults;

    public NodeResult(Double value, Set<EdgeResult> inputEdgeResults, Set<EdgeResult> outputEdgeResults)
    {
        PreCheck.ifNull("Unable to create node result with null value",value);
        //PreCheck.ifEmpty(()->new IllegalArgumentException("Unable to create node result with null or empty input/outputs"),inputEdgeResults,outputEdgeResults);
        this.value = value;
        this.inputEdgeResults = inputEdgeResults;
        this.outputEdgeResults = outputEdgeResults;
    }

    public Set<EdgeResult> getInputEdgeResults()
    {
        return inputEdgeResults;
    }

    public Set<EdgeResult> getOutputEdgeResults()
    {
        return outputEdgeResults;
    }

    public Double getValue()
    {
        return this.value;
    }
}
