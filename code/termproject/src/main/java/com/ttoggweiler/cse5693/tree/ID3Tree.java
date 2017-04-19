package com.ttoggweiler.cse5693.tree;

import com.ttoggweiler.cse5693.feature.DataSet;
import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.feature.Parser;
import com.ttoggweiler.cse5693.predict.Classifier;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Builds an ID3 Tree
 */
public class ID3Tree extends Classifier
{
    private static Logger log = LoggerFactory.getLogger(ID3Tree.class);
    private static boolean useStreams = true;
    private static boolean useParallelStreams = false;
    private static int parallelThreshold = 100;
    private static int buildTreeDepthLimit = 3;

    private FeatureNode rootNode;
    private Feature target;

    // in the middle of refactoring....plan it out what is parent resposible for and what is the child respibiel for in terms of edges, creating attribures...
    public ID3Tree(Feature target, DataSet trainData)
    {
        this.target = target;
        this.setFeatures(trainData.getFeatures());
        this.setTargetFeatures(trainData.getTargets());

        FeatureNode initNode = new FeatureNode(target, trainData.getFeatures());
        initNode.setTargetDistributions(trainData.getValueCountsForFeature(target));
        rootNode = buildSubTree(initNode, target, trainData);
    }

    public void setPredictionDepth(int depth)
    {
        rootNode.setPredictionDepth(depth);
    }

    public FeatureNode getRootNode()
    {
        return rootNode;
    }
    private static FeatureNode buildSubTree(FeatureNode parent, Feature target, DataSet dataSet)
    {
        // If target feature has same value in all examples, create leaf
        if (dataSet.getValueCountsForFeature(target).size() <= 1
                || parent.distanceFromRoot() == buildTreeDepthLimit)
            return createLeafNodeForTarget(parent, target, dataSet);

        // Find feature with best info gain and create a new node for it
        Feature feature = dataSet.getFeatureWithBestInfoGainFor(target); // fixme this is not removing feature after being used
        assert PreCheck.notNull(feature)
                : "Feature with best gain should always be found. " + dataSet.getFeatures().toString();
        if (PreCheck.contains(feature.getFeatureType(), Parser.Type.BOOLEAN, Parser.Type.STRING)) {
            return createValueNodeForFeature(parent, target, feature, dataSet);
        } else {
            return createContinuousNodeForFeature(parent, target, feature, dataSet);
        }
    }

    private static FeatureNode createLeafNodeForTarget(FeatureNode parent, Feature target, DataSet dataSet)
    {
        //checkNodeInputs(parent, target, dataSet);
        FeatureNode leaf = new FeatureNode(target, parent.getFeatures());

        leaf.setTargetDistributions(dataSet.getValueCountsForFeature(target));
        log.info("Created Leaf Node: {} with Dist: {}", leaf.getName(), leaf.getTargetDistributions());
        return leaf;
    }

    private static FeatureNode createValueNodeForFeature(FeatureNode parent, Feature target, Feature feature, DataSet dataSet)
    {
        checkNodeInputs(parent, target, dataSet);
        // Create new node with parents features - newFeature
        Collection<Feature> remainingFeatures = parent.getFeatures();
        remainingFeatures.remove(feature);
        FeatureNode newNode = new FeatureNode(feature, remainingFeatures);
        newNode.setTargetDistributions(dataSet.getValueCountsForFeature(target));
        log.info("Created Value Node {} with Dist: {}", newNode.getName(), newNode.getTargetDistributions());

        // Create child nodes for each value of feature, and split data set on that value
        Map<Comparable, List<Map<String, Comparable>>> valueSubSets2 = feature.splitDataOnValues(dataSet.getData());
        Map<Comparable, DataSet> valSubSets = dataSet.subsetOnFeatureValues(feature);
        // TODO: ttoggweiler 4/19/17 getStream and metrics
        if (useStreams) {
            getStream(valSubSets.entrySet())
                    .filter(entry-> PreCheck.notEmpty(entry.getValue().getData()))//fixme do we want to filter? no child would exist for that val
                    .forEach(valSubsetEntry -> {
                        FeatureNode child = buildSubTree(newNode, target, valSubsetEntry.getValue());
                        newNode.addEdge("=" + valSubsetEntry.getKey().toString(), valSubsetEntry.getKey(), child, false);
                        log.debug("{} -- hasChild -> {} for value: {}{}", newNode.getName(), child.getName(), feature.getName(), child.getParentEdgeName());
                    });

        } else {
            for (Map.Entry<Comparable, DataSet> valSubsetEntry : valSubSets.entrySet()) {
                FeatureNode child = buildSubTree(newNode, target, valSubsetEntry.getValue());
                newNode.addEdge("=" + valSubsetEntry.getKey().toString(), valSubsetEntry.getKey(), child, false);
                log.debug("{} -- hasChild -> {} for value: {}{}", newNode.getName(), child.getName(), feature.getName(), child.getParentEdgeName());
            }
        }
        return newNode;
    }

    private static FeatureNode createContinuousNodeForFeature(FeatureNode parent, Feature target, Feature feature, DataSet dataSet)
    {

        checkNodeInputs(parent, target, dataSet);
        // Create new node
        FeatureNode newNode = new FeatureNode(feature, parent.getFeatures());
        newNode.setTargetDistributions(dataSet.getValueCountsForFeature(target));
        log.info("Created Continuous Node {} with Dist: {}", newNode.getName(), newNode.getTargetDistributions());

        // Find a single best value to split data on
        //Comparable lowestEntropyValue2 = feature.getValueWithLowestEntropy(target, dataSet);

        //todo use equivalnt entropy with larger set
        Comparable lowestEntropyValue = dataSet.mapFeatureValuesToTargetEntropy(target, feature).entrySet().stream()
                .filter(e -> !parent.hasFeatureValueBeenUsedBefore(feature, e.getKey()))
                .min(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (lowestEntropyValue == null)
            return newNode;
        else
            newNode.addEdgeValue(lowestEntropyValue);

        // Create positive and negative child nodes
        Predicate<Map<String, Comparable>> valPredicate = feature.getPredicateForValue(lowestEntropyValue);
        DataSet positiveSubSet = dataSet.subsetOnPredicate(valPredicate);
        DataSet negativeSubset = dataSet.subsetOnPredicate(valPredicate.negate());

        if (PreCheck.notEmpty(positiveSubSet.getData()) || false) {
            FeatureNode positiveChild = buildSubTree(newNode, target, positiveSubSet);
            newNode.addEdge(">=" + lowestEntropyValue, lowestEntropyValue, positiveChild, false);
            log.debug("{} -- hasChild -> {} for value: {}{}", newNode.getName(), positiveChild.getName(), feature.getName(), positiveChild.getParentEdgeName());

        } else
            log.warn("No positive child created because data set is empty");


        if (PreCheck.notEmpty(negativeSubset.getData()) || false) {
            FeatureNode negativeChild = buildSubTree(newNode, target, negativeSubset);
            newNode.addEdge("<" + lowestEntropyValue, lowestEntropyValue, negativeChild, true);
            log.debug("{} -- hasChild -> {} for value: {}{}", newNode.getName(), negativeChild.getName(), feature.getName(), negativeChild.getParentEdgeName());
        } else
            log.warn("No negative child created because data set is empty");

        return newNode;
    }

    @Override
    public Map<String, Comparable> classifyExample(Map<String, Comparable> example)
    {
        Comparable prediction = rootNode.getClassification(example);
        Map<String, Comparable> predictionMap = new HashMap<>(1);
        predictionMap.put(this.target.getName(), prediction);
        return predictionMap;
    }

    @Override
    public String getClassifierString(boolean includeName)
    {
        return rootNode.toTreeString();
    }

    private static void checkNodeInputs(FeatureNode parent, Feature target, DataSet dataSet) throws IllegalArgumentException, NullPointerException
    {
        PreCheck.ifNull("Null parent argument detected, unable to create node", parent);
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Empty feature set or example data detected, unable to create node"),
                parent.getFeatures(), dataSet.getData());
    }

    // TODO: ttoggweiler 4/19/17 util ?
    public static <T> Stream<T> getStream(Collection<T> collection)
    {
        return (useParallelStreams && collection.size() > parallelThreshold)
                ? collection.parallelStream()
                : collection.stream();
    }
}
