package com.ttoggweiler.cse5693.tree;

import com.ttoggweiler.cse5693.data.DataSet;
import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.MapUtil;
import com.ttoggweiler.cse5693.util.RandomUtil;
import com.ttoggweiler.cse5693.util.ValueParser;
import com.ttoggweiler.cse5693.predict.Classifier;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builds an ID3 Tree
 */
public class ID3Tree extends Classifier
{
    public static final int MIN_LEAF_DATA_SIZE = 2;
    private static boolean useStreams = true;
    private static boolean useParallelStreams = true;
    private static int parallelThreshold = 10000;

    public static final int DEFAULT_DEPTH_LIMIT = Integer.MAX_VALUE;
    private static int buildTreeDepthLimit = DEFAULT_DEPTH_LIMIT;

    private static Logger log = LoggerFactory.getLogger(ID3Tree.class);
    private FeatureNode rootNode;

    private Feature target;

    // in the middle of refactoring....plan it out what is parent resposible for and what is the child respibiel for in terms of edges, creating attribures...
//    public ID3Tree(Feature target, DataSet trainData)
//    {
//        this.target = target;
//        this.setFeatures(trainData.getFeatures());
//        this.setTargetFeatures(trainData.getTargets());
//
//        FeatureNode initNode = new FeatureNode(null,target,trainData.getFeatures());
//        initNode.setTargetDistributions(trainData.countValuesForFeature(target,true));
//        rootNode = buildSubTree(initNode, target, trainData);
//    }

    public ID3Tree(Feature target,Integer depth)
    {
        this.target = target;
        buildTreeDepthLimit = PreCheck.defaultTo(depth,DEFAULT_DEPTH_LIMIT);
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
        if (dataSet.countValuesForFeature(target,true).size() <= 1
                || parent.distanceFromRoot() >= buildTreeDepthLimit
                || dataSet.size()< MIN_LEAF_DATA_SIZE)
            return createLeafNodeForTarget(parent, target, dataSet);

        // Find feature with best info gain and create a new node for it
        Feature feature = dataSet.getFeatureWithBestInfoGainFor(target); // fixme? not removing already used features...
        assert PreCheck.notNull(feature)
                : "Feature with best gain should always be found. " + dataSet.getFeatures().toString();
        if (PreCheck.contains(feature.getFeatureType(), ValueParser.Type.BOOLEAN, ValueParser.Type.TEXT)) {
            return createValueNodeForFeature(parent, target, feature, dataSet);
        } else {
            return createContinuousNodeForFeature(parent, target, feature, dataSet);
        }
    }

    private static FeatureNode createLeafNodeForTarget(FeatureNode parent, Feature target, DataSet dataSet)
    {
        //checkNodeInputs(parent, target, dataSet);
        FeatureNode leaf = new FeatureNode(parent,target, parent.getFeatures());

        leaf.setTargetDistributions(dataSet.countValuesForFeature(target,true));
        log.info("Created Leaf Node: {} with Dist: {}", leaf.name(), leaf.getTargetDistributions());
        return leaf;
    }

    private static FeatureNode createValueNodeForFeature(FeatureNode parent, Feature target, Feature feature, DataSet dataSet)
    {
        checkNodeInputs(parent, target, dataSet);
        // Create new node with parents features - newFeature
        Collection<Feature> remainingFeatures = parent.getFeatures();
        remainingFeatures.remove(feature);
        FeatureNode newNode = new FeatureNode(parent,feature, remainingFeatures);
        newNode.setTargetDistributions(dataSet.countValuesForFeature(target,true));
        log.info("Created Value Node {} with Dist: {}", newNode.name(), newNode.getTargetDistributions());

        // Create child nodes for each value of feature, and split data set on that value
        Map<Comparable, DataSet> valSubSets = dataSet.subsetOnFeatureValues(feature,true);
        // TODO: ttoggweiler 4/19/17 getStream and metrics
        if (useStreams) {
            getStream(valSubSets.entrySet())
                    .filter(entry-> PreCheck.notEmpty(entry.getValue().getData()))
                    .forEach(valSubsetEntry -> {
                        FeatureNode child = buildSubTree(newNode, target, valSubsetEntry.getValue());
                        newNode.addEdge("=" + valSubsetEntry.getKey().toString(), valSubsetEntry.getKey(), child, false);
                        log.debug("{} -- hasChild -> {} for value: {}{}", newNode.name(), child.name(), feature.name(), child.getParentEdgeName());
                    });

        } else {
            for (Map.Entry<Comparable, DataSet> valSubsetEntry : valSubSets.entrySet()) {
                FeatureNode child = buildSubTree(newNode, target, valSubsetEntry.getValue());
                newNode.addEdge("=" + valSubsetEntry.getKey().toString(), valSubsetEntry.getKey(), child, false);
                log.debug("{} -- hasChild -> {} for value: {}{}", newNode.name(), child.name(), feature.name(), child.getParentEdgeName());
            }
        }
        return newNode;
    }

    private static FeatureNode createContinuousNodeForFeature(FeatureNode parent, Feature target, Feature feature, DataSet dataSet)
    {

        checkNodeInputs(parent, target, dataSet);
        // Create new node
        FeatureNode newNode = new FeatureNode(parent,feature, parent.getFeatures());
        newNode.setTargetDistributions(dataSet.countValuesForFeature(target,true));
        log.info("Created Continuous Node {} with Dist: {}", newNode.name(), newNode.getTargetDistributions());

        //Get entropy for all unused values
        Map<Comparable, Double> entropyMap = dataSet.mapFeatureValuesToTargetEntropy(feature, target, false).entrySet().stream()
                .filter(e -> !parent.hasFeatureValueBeenUsedBefore(feature, e.getKey())) // Filter out previously used values
                .collect(Collectors.toMap(Map.Entry :: getKey,Map.Entry :: getValue));

        // Get datasets sizes for values
        Map<Comparable,Integer> lowEntropyValueDataSetSizes = MapUtil.keysForMin(entropyMap).stream()
                .collect(Collectors.toMap(s ->s, s -> dataSet.subsetForFeatureValue(feature,s,false).size()));
        // Get values with largest data sets
        Set<Comparable> minEntropyMaxDataSetSizeFeatureValues = MapUtil.keysForMax(lowEntropyValueDataSetSizes);
        // Pick random feature value that has best entropy and has the largest dataset
        Comparable lowestEntropyValue = PreCheck.notEmpty(minEntropyMaxDataSetSizeFeatureValues)
                ?RandomUtil.randomElement(minEntropyMaxDataSetSizeFeatureValues)
                :RandomUtil.randomElement(entropyMap.keySet());


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
            log.debug("{} -- hasChild -> {} for value: {}{}", newNode.name(), positiveChild.name(), feature.name(), positiveChild.getParentEdgeName());

        } else
            log.warn("No positive child created because data set is empty");


        if (PreCheck.notEmpty(negativeSubset.getData()) || false) {
            FeatureNode negativeChild = buildSubTree(newNode, target, negativeSubset);
            newNode.addEdge("<" + lowestEntropyValue, lowestEntropyValue, negativeChild, true);
            log.debug("{} -- hasChild -> {} for value: {}{}", newNode.name(), negativeChild.name(), feature.name(), negativeChild.getParentEdgeName());
        } else
            log.warn("No negative child created because data set is empty");

        return newNode;
    }

    @Override
    public void train(DataSet trainData)
    {
        initWithDataSet(trainData);
        FeatureNode initNode = new FeatureNode(null,target,trainData.getFeatures());
        initNode.setTargetDistributions(trainData.countValuesForFeature(target,true));
        rootNode = buildSubTree(initNode, target, trainData);
    }

    @Override
    public Map<String, Comparable> classifyExample(Map<String, Comparable> example)
    {
        Comparable prediction = rootNode.getClassification(example);
        Map<String, Comparable> predictionMap = new HashMap<>(1);
        predictionMap.put(this.target.name(), prediction);
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

    public Feature getTarget()
    {
        return target;
    }
}
