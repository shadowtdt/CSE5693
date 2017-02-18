package com.ttoggweiler.cse5693.learner;

import com.ttoggweiler.cse5693.DecisionTreeRunner;
import com.ttoggweiler.cse5693.tree.Feature;
import com.ttoggweiler.cse5693.tree.FeatureNode;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 2/15/17.
 */
public class TreeBuilder
{
    private final List<Feature> features;
    private final List<Map<String, String>> trainingData;
    private static Logger log = LoggerFactory.getLogger(TreeBuilder.class);

    // TODO: ttoggweiler 2/16/17 tree factory?
    public TreeBuilder(List<Feature> features, List<Map<String, String>> trainingData)
    {
        this.features = features;
        this.trainingData = trainingData;
    }

    public static FeatureNode buildTree(Feature target, List<Feature> features, List<Map<String, Comparable>> trainingData)
    {
        if(PreCheck.isEmpty(features,trainingData))return null;
        Feature feature = features.remove(0);
        FeatureNode node = new FeatureNode(feature);
        Map<Comparable, Integer> targetFeatureDist = getValueDistributionForFeature(target,trainingData);
        node.setTargetDistributions(targetFeatureDist);
        log.debug("Created node {} with Dist: {}",node.getName(),targetFeatureDist);

        if(targetFeatureDist.size() <= 1)return node;

        if(PreCheck.isEmpty(features,trainingData))
        {
            return node;
        }

        if(PreCheck.notEmpty(feature.getValues())) {
            for (Object o : feature.getValues()) {
                FeatureNode child = buildTree(target,features.stream().collect(Collectors.toList()), trainingData.stream()
                        .filter(m -> m.containsKey(feature.getName())) // filter data that has feature
                        .filter(m -> m.get(feature.getName()).equals(o)) // filter data that has o value for feature
                        .collect(Collectors.toList()));
                if(child != null) {
                    node.setDecisionEdge(o.toString(),s -> s.equals(o), child);
                    node.setChildNode(child); // add the subtree to the current node
                    log.debug("Added child {} -> {}", node.getName(), child.getName());
                }
            }
        }else
        {
            Predicate<Comparable> numberPredicate = getPredicateForContinuousValuedFeature(target,feature,trainingData);
            FeatureNode positiveChild = buildTree(target,features.stream().collect(Collectors.toList()), trainingData.stream()
                    .filter(m -> m.containsKey(feature.getName())) // filter data that has feature
                    .filter(m -> numberPredicate.test(m.get(feature.getName()))) // filter data that has o value for feature
                    .collect(Collectors.toList()));

            if (positiveChild != null) {
                node.setDecisionEdge(numberPredicate.toString(),numberPredicate,positiveChild);
                log.debug("Added child {} -> {}",node.getName(),positiveChild.getName());
            }

            FeatureNode negativeChild = buildTree(target,features.stream().collect(Collectors.toList()), trainingData.stream()
                    .filter(m -> m.containsKey(feature.getName())) // filter data that has feature
                    .filter(m -> !numberPredicate.test(m.get(feature.getName()))) // filter data that has o value for feature
                    .collect(Collectors.toList()));
            if (negativeChild != null) {
                node.setDecisionEdge(numberPredicate.toString(),numberPredicate.negate(),negativeChild);
                log.debug("Added child {} -> {}",node.getName(),negativeChild.getName());
            }
        }
        return node;
    }

    public static Predicate<Comparable> getPredicateForContinuousValuedFeature(Feature target, Feature feature, List<Map<String, Comparable>> trainingData)
    {
        PreCheck.ifNull("Unable to get predicate for continuous feature " + feature.getName() + ". Null parameters found"
                ,target,feature,trainingData);

        //Double entropyForAll = getEntropyForFeature(target,trainingData);
        Double bestEntropy = 0d;
        Predicate<Comparable> bestPredicate = null;
        for (Map<String, Comparable> example : trainingData) {
            Comparable valueToTest = example.get(feature.getName());
            List<Map<String,Comparable>> exampleSubSet = trainingData.stream()
                    .filter(m -> m.get(feature.getName()).compareTo(valueToTest) >= 0)
                    .collect(Collectors.toList());
            Double entropyOfSubset = getEntropyForFeature(target,exampleSubSet);
            if(entropyOfSubset > bestEntropy) {
                bestEntropy = entropyOfSubset;
                bestPredicate = (c) -> c.compareTo(valueToTest) >= 0;
            }
        }
        return bestPredicate;
    }

    public static Double getEntropyForFeature(Feature feature, List<Map<String,Comparable>> examples)
    {
        Double entropy = 0d;
        Map<Comparable,Integer> featureValueDist = getValueDistributionForFeature(feature,examples);
        Integer exampleCount = examples.size();

        for (Object possibleValue : feature.getValues()) {
            Integer matchingExampleCount = featureValueDist.get(possibleValue);
            if(matchingExampleCount == null || matchingExampleCount == 0)continue;
            Double matchingExampleRatio = (double)matchingExampleCount/(double) exampleCount;
            entropy -= matchingExampleRatio * Math.log(matchingExampleRatio);
        }
        return entropy;
    }

    public static Map<Comparable,Integer> getValueDistributionForFeature(Feature feature, List<Map<String,Comparable>> examples)
    {
        Map<Comparable, Integer> targetFeatureDistribution;

        List<Map<String,Comparable>> filteredExamples = examples.stream()
                .filter(m -> m.containsKey(feature.getName())) // Target feature is present
                .collect(Collectors.toList());
        targetFeatureDistribution = new HashMap<>();
        for (Map<String, Comparable> example : filteredExamples) {
            Comparable targetValue = example.get(feature.getName());
            targetFeatureDistribution.putIfAbsent(targetValue,0);
            targetFeatureDistribution.compute(targetValue,(k,v) -> v = v+1);
        }
        return targetFeatureDistribution;
    }

}
