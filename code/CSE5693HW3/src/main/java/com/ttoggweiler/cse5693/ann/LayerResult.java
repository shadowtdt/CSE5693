package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents all inputs and outputs for a given layer
 */
public class LayerResult extends Identity
{
    private Set<NodeResult> nodeResults;

    public LayerResult(Set<NodeResult> nodeResults)
    {
        PreCheck.ifEmpty(()->new IllegalArgumentException("Unable to add null node result to layer result"),nodeResults);
        this.nodeResults = nodeResults;
    }

    public Set<NodeResult> getNodeResults()
    {
        return nodeResults;
    }
}
