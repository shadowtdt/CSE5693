package com.ttoggweiler.cse5693.learner;

import com.ttoggweiler.cse5693.tree.Feature;
import com.ttoggweiler.cse5693.tree.FeatureNode;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 2/15/17.
 */
public class TreeBuilder
{
    private static Logger log = LoggerFactory.getLogger(TreeBuilder.class);

    // TODO: ttoggweiler 2/16/17 tree factory?

    public static FeatureNode buildTree(Feature target, List<Feature> features, List<Map<String, Comparable>> trainingData)
    {
        if(PreCheck.isEmpty(features,trainingData))return null;
        Map<Comparable, Integer> targetFeatureDist = target.getValueCounts(trainingData);
        if(targetFeatureDist.size() == 1)
        {
            FeatureNode leaf = new FeatureNode(target);
            leaf.setTargetDistributions(targetFeatureDist);
            return leaf;
        }

        Feature feature = getBestInfoGainFeature(target,features,trainingData);
        features.remove(feature);
        FeatureNode node = new FeatureNode(feature);
        node.setTargetDistributions(targetFeatureDist);
        log.info("Created node {} with Dist: {}",node.getName(),targetFeatureDist);

        if(targetFeatureDist.size() <= 1 || features.isEmpty())return node;

        if(PreCheck.notEmpty(feature.getValues())) {
            for (Object o : feature.getValues()) {
                FeatureNode child = buildTree(target,features.stream().collect(Collectors.toList()), trainingData.stream()
                        .filter(m -> m.containsKey(feature.getName())) // filter data that has feature
                        .filter(m -> m.get(feature.getName()).equals(o)) // filter data that has o value for feature
                        .collect(Collectors.toList()));
                if(child != null) {
                    node.setDecisionEdge(o.toString(),s -> s.equals(o), child);
                    node.setChildNode(child); // add the subtree to the current node
                    log.debug("Added child {} -> {}",child.getName(), node.getName() );
                }
            }
        }else
        {
            Predicate<Comparable> numberPredicate = getPredicateForContinuousValuedFeature(target,feature,trainingData);

            List<Map<String, Comparable>> positiveSubSet = trainingData.stream()
                    .filter(m -> m.containsKey(feature.getName())) // filter data that has feature
                    .filter(m -> numberPredicate.test(m.get(feature.getName()))) // filter data that has o value for feature
                    .collect(Collectors.toList());
            FeatureNode positiveChild = buildTree(target,features.stream().collect(Collectors.toList()), positiveSubSet);
            if (positiveChild != null) {
                node.setDecisionEdge(numberPredicate.toString(),numberPredicate,positiveChild);
                log.debug("Added child {} -> {}",positiveChild.getName(), node.getName() );
            }

            List<Map<String, Comparable>> negativeSubset = trainingData.stream()
                    .filter(m -> m.containsKey(feature.getName())) // filter data that has feature
                    .filter(m -> !numberPredicate.test(m.get(feature.getName()))) // filter data that has o value for feature
                    .collect(Collectors.toList());
            FeatureNode negativeChild = buildTree(target,features.stream().collect(Collectors.toList()), negativeSubset);
            if (negativeChild != null) {
                node.setDecisionEdge(numberPredicate.toString(),numberPredicate.negate(),negativeChild);
                log.debug("Added child {} -> {}",negativeChild.getName(), node.getName() );
            }
        }
        return node;
    }

    public static Predicate<Comparable> getPredicateForContinuousValuedFeature(Feature target, Feature feature, List<Map<String, Comparable>> trainingData)
    {
        PreCheck.ifNull("Unable to get predicate for continuous feature " + feature.getName() + ". Null parameters found"
                ,target,feature,trainingData);

        //Double entropyForAll = getEntropy(target,trainingData);
        Double bestEntropy = 1d;
        Comparable value = null;
        Predicate<Comparable> bestPredicate = null;
        for (Map<String, Comparable> example : trainingData) {
            Comparable valueToTest = example.get(feature.getName());
            Predicate<Comparable> valuePredicate = x -> x.compareTo(valueToTest) >= 0;
            List<Map<String,Comparable>> exampleSubSet = trainingData.stream()
                    .filter(m -> valuePredicate.test(m.get(feature.getName())))
                    .collect(Collectors.toList());
            Double entropyOfSubset = target.getEntropy(exampleSubSet);
            log.debug("Feature: {} Value: {} Entropy: {}",feature.getName(),valueToTest,entropyOfSubset);
            if(entropyOfSubset < bestEntropy) {
                log.warn("{}= {} New best entropy: {} -> {}",feature.getName(),valueToTest,bestEntropy,entropyOfSubset);
                bestEntropy = entropyOfSubset;
                bestPredicate = valuePredicate;
                value = valueToTest;
            }
        }
        feature.addValue((Comparable<?>) value);
        return bestPredicate;
    }

    public static Feature getBestInfoGainFeature(Feature target, List<Feature> features, List<Map<String, Comparable>> datas)
    {
        Double datasEntropy = target.getEntropy(datas);
        Feature bestFeature = null;
        Double bestEntropy = 1d;
        Double bestGain = 0d;
        for (Feature feature : features) {
            Map<Comparable, List<Map<String, Comparable>>> valueMap = feature.getValueToDataMap(datas);
            Double featureEntropy = 0d;
            for (Map.Entry<Comparable, List<Map<String, Comparable>>> valueEntry : valueMap.entrySet()) {
                Double ratio = (double) valueEntry.getValue().size() / (double) datas.size();
                Double featureValueEntropy = target.getEntropy(valueEntry.getValue());
                featureEntropy += ratio *  featureValueEntropy;
            }
            Double gain = datasEntropy - featureEntropy;
            if(bestGain < gain) {
                bestFeature = feature;
                bestGain = gain;
            }

        }
        return bestFeature;
    }

}
