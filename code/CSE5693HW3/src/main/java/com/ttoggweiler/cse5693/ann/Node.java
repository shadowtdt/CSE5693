package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.MoreMath;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a node in the network
 */
public class Node extends Identity
{
    private Boolean isInputNode;
    private List<Edge> inputEdges;
    private List<Edge> outputEdges;

    private NodeResult mostRecentNodeResult;
    private Set<EdgeResult> currentInputEdgeResults;
    private Double nodeValue; // inputNode: input value; outputNode: actual value;

    public Node(String name, Boolean isInput)
    {
        if (isInput != null) this.isInputNode = isInput;
        this.setName(name);
    }

    public List<Edge> getInputEdges()
    {
        return inputEdges;
    }

    public List<Edge> getOutputEdges()
    {
        return outputEdges;
    }

    public NodeResult getMostRecentNodeResult()
    {
        return mostRecentNodeResult;
    }

    public void addInputEdge(Edge inputEdge)
    {
        if (inputEdge == null) throw new NullPointerException("Unable to set null input edge on Node");
        if (inputEdges == null) inputEdges = new ArrayList<>();
        inputEdges.add(inputEdge);
    }

    public void addOutputEdge(Edge outputEdge)
    {
        if (outputEdge == null) throw new NullPointerException("Unable to set null output edge on Node");
        if (outputEdges == null) outputEdges = new ArrayList<>();
        outputEdges.add(outputEdge);
    }

    public void addInputEdgeResult(EdgeResult inputEdgeResult)
    {
        PreCheck.ifNull("Input result cannot be null.", inputEdgeResult);
        if (currentInputEdgeResults == null) currentInputEdgeResults = new HashSet<>(inputEdges.size());
        currentInputEdgeResults.add(inputEdgeResult);
    }

    public NodeResult feedForward()
    {
        Double sumOfInputs = calculateSumOfInputEdgeResults();
        Double result = sumOfInputs;
        Set<EdgeResult> outputEdgeResults = null;
        if (!isOutputNode()) {
            result = MoreMath.sigmoidFunction(sumOfInputs);
            outputEdgeResults = new HashSet<>(outputEdges.size());
            for (Edge outputEdge : outputEdges) {
                EdgeResult edgeResult = outputEdge.feedForward(result);
                outputEdgeResults.add(edgeResult);
            }
        }
        mostRecentNodeResult = new NodeResult(result, currentInputEdgeResults, outputEdgeResults);
        currentInputEdgeResults = null;
        return mostRecentNodeResult;
    }

    public void backPropagate()
    {
        if(isInputNode())return;
        Double predictionValue = getMostRecentNodeResult().getValue();
        Double errorDelta;
        if(isOutputNode())
        {
            errorDelta = this.nodeValue - predictionValue;
        }else{
            errorDelta = calculateSumOfWeightedErrorForOutputEdges();
        }
        Double error = predictionValue * (1 - predictionValue) * errorDelta;
        mostRecentNodeResult.setError(error);
        for (EdgeResult edgeResult : mostRecentNodeResult.getInputEdgeResults()) {
            edgeResult.setError(error);
        }
    }

    public void updateWeights(Double learningRate)
    {
        if(isOutputNode())return;
        for (Edge outputEdge : outputEdges) {
            outputEdge.updateWeights(learningRate);
        }
    }

    public void setNodeValue(Double inputValue)
    {
        if (isHiddenNode()) throw new IllegalArgumentException("Only input and output nodes can set value");
        this.nodeValue = inputValue;
    }

    private Double calculateSumOfInputEdgeResults()
    {
        if (isInputNode()) return this.nodeValue;

        PreCheck.ifEmpty(() -> new IllegalStateException("Current inputs is null or empty"), currentInputEdgeResults);
        if (currentInputEdgeResults.size() != inputEdges.size() && !isInputNode())
            throw new IllegalArgumentException("Unable to feed forward, input size does not match the number of input nodes");
        Double sum = 0d;
        for (EdgeResult edgeResult : currentInputEdgeResults) sum += edgeResult.getOutput();
        return sum;
    }

    private Double calculateSumOfWeightedErrorForOutputEdges()
    {
        PreCheck.ifEmpty(() -> new IllegalStateException("Current output edge results is null or empty"), mostRecentNodeResult.getOutputEdgeResults());
        if (mostRecentNodeResult.getOutputEdgeResults().size() != outputEdges.size() && isOutputNode())
            throw new IllegalArgumentException("Unable to back propagate, output edge result size does not match the number of output nodes");
        Double errorSum = 0d;
        for (EdgeResult edgeResult : mostRecentNodeResult.getOutputEdgeResults())
            errorSum += edgeResult.getWeightedError();
        return errorSum;
    }

    public boolean isInputNode()
    {
        return (isInputNode != null) && isInputNode;
    }

    public boolean isOutputNode()
    {
        return (isInputNode != null) && !isInputNode();
    }

    public boolean isHiddenNode()
    {
        return isInputNode == null;
    }

    public String getTopologyString()
    {
        StringBuilder topologyStringBuilder = new StringBuilder("-Node: " + getName());
        if (mostRecentNodeResult != null) topologyStringBuilder.append(" V(" + mostRecentNodeResult.getValue() + ")");
        if (mostRecentNodeResult != null && mostRecentNodeResult.getError() != null)
            topologyStringBuilder.append(" E(" + mostRecentNodeResult.getError() + ")");
        if (PreCheck.notEmpty(outputEdges)) {
            for (Edge edge : outputEdges) {
                topologyStringBuilder.append("\n\t\t\t");
                topologyStringBuilder.append(edge.toString());
            }
        }
        return topologyStringBuilder.toString();
    }
}
