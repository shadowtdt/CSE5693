package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.MoreMath;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a node in the network
 */
public class Node extends Identity
{
    private Boolean isInputNode;
    private Set<Edge> inputEdges;
    private Set<Edge> outputEdges;

    private NodeResult mostRecentNodeResult;
    private Set<EdgeResult> currentInputEdgeResults;

    public Node(String name, Boolean isInput)
    {
        if(isInput!=null) this.isInputNode = isInput;
        this.setName(name);
    }

    public Set<Edge> getInputEdges()
    {
        return inputEdges;
    }

    public Set<Edge> getOutputEdges()
    {
        return outputEdges;
    }

    public NodeResult getMostRecentNodeResult()
    {
        return mostRecentNodeResult;
    }

    public void addInputEdge(Edge inputEdge)
    {
        if(inputEdge == null) throw new NullPointerException("Unable to set null input edge on Node");
        if(inputEdges == null)inputEdges = new HashSet<>();
        inputEdges.add(inputEdge);
    }

    public void addOutputEdge(Edge outputEdge)
    {
        if(outputEdge == null) throw new NullPointerException("Unable to set null output edge on Node");
        if(outputEdges == null)outputEdges = new HashSet<>();
        outputEdges.add(outputEdge);
    }

    public void addInputEdgeResult(EdgeResult inputEdgeResult)
    {
        PreCheck.ifNull("Input result cannot be null.",inputEdgeResult);
        if(currentInputEdgeResults == null)currentInputEdgeResults = new HashSet<>(inputEdges.size());
        currentInputEdgeResults.add(inputEdgeResult);
    }

    public NodeResult feedForward()
    {
        if (!isOutputNode()) {
            Double sumOfInputs = calculateSumOfInputEdgeResults();
            Double sigmoidResult = MoreMath.sigmoidFunction(sumOfInputs);
            Set<EdgeResult> outputEdgeResults = null;
            outputEdgeResults = new HashSet<>(outputEdges.size());
            for (Edge outputEdge : outputEdges) {
                EdgeResult edgeResult = outputEdge.feedForward(sigmoidResult);
                outputEdgeResults.add(edgeResult);
            }
            mostRecentNodeResult = new NodeResult(sigmoidResult,currentInputEdgeResults,outputEdgeResults);
        }
        return mostRecentNodeResult;
    }

    private Double calculateSumOfInputEdgeResults()
    {
        PreCheck.ifEmpty(()->new IllegalStateException("Current inputs is null or empty"),currentInputEdgeResults);
        if(currentInputEdgeResults.size() != inputEdges.size() && !isInputNode()) throw new IllegalArgumentException("Unable to feed forward, input size does not match the number of input nodes");
        Double sum = 0d;
        for(EdgeResult edgeResult : currentInputEdgeResults) sum += edgeResult.getOutput();
        return sum;
    }

    public boolean isInputNode()
    {
        return (isInputNode !=null) &&  isInputNode;
    }

    public boolean isOutputNode()
    {
        return (isInputNode !=null) && !isInputNode;
    }

    public boolean inHiddenNode()
    {
        return isInputNode == null;
    }
}
