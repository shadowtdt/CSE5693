package com.ttoggweiler.cse5693.ann;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.MoreMath;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a node in the network
 */
public class Node extends Identity
{
    private Boolean isInputNode;
    private List<Edge> inputEdges;
    private List<Edge> outputEdges;

    private NodeResult lastNodeResult;
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

    public NodeResult getLastNodeResult()
    {
        return lastNodeResult;
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
        Double nodeValue = isInputNode() ? this.nodeValue // input Layer
                : sumAndSquishInputs(currentInputEdgeResults); // other layers

        Set<EdgeResult> outputEdgeResults = isOutputNode() ? null // outputLayer
                : outputEdges.stream().map(edge -> edge.feedForward(nodeValue)).collect(Collectors.toSet()); // other layers

        lastNodeResult = new NodeResult(nodeValue, currentInputEdgeResults, outputEdgeResults);
        currentInputEdgeResults = null;
        return lastNodeResult;
    }

    public void backPropagate()
    {
        if(isInputNode())return;
        Double predictedValue = getLastNodeResult().getValue();
        Double errorDelta = isOutputNode() ? (this.nodeValue - predictedValue)
                :lastNodeResult.getOutputEdgeResults().stream().mapToDouble(EdgeResult::getWeightedError).sum();

        Double error = predictedValue * (1 - predictedValue) * errorDelta;
        lastNodeResult.setError(error);
    }

    private static Double sumAndSquishInputs(Set<EdgeResult> inputEdges)
    {
        PreCheck.ifEmpty(()->new IllegalArgumentException("No inputs provided"),inputEdges);
        Double netInput = inputEdges.stream().mapToDouble(EdgeResult::getWeightedOutput).sum();
        return MoreMath.sigmoidFunction(netInput);
    }

    public void updateWeights(Double learningRate)
    {
        if(isInputNode())return;
        for (Edge inputEdges : inputEdges) {
            inputEdges.updateWeights(learningRate);
        }
    }

    public void setNodeValue(Double inputValue)
    {
        if (isHiddenNode()) throw new IllegalArgumentException("Only input and output nodes can set value");
        this.nodeValue = inputValue;
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
        if (lastNodeResult != null) topologyStringBuilder.append(" V(" + lastNodeResult.getValue() + ")");
        if (lastNodeResult != null && lastNodeResult.getError() != null)
            topologyStringBuilder.append(" E(" + lastNodeResult.getError() + ")");
        if (PreCheck.notEmpty(outputEdges)) {
            for (Edge edge : outputEdges) {
                topologyStringBuilder.append("\n\t\t\t");
                topologyStringBuilder.append(edge.toString());
            }
        }
        return topologyStringBuilder.toString();
    }
}
