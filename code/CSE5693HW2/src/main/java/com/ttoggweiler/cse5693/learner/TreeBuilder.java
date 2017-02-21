package com.ttoggweiler.cse5693.learner;

import com.ttoggweiler.cse5693.tree.Feature;
import com.ttoggweiler.cse5693.tree.FeatureNode;
import com.ttoggweiler.cse5693.util.Parser;
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

        if(PreCheck.contains(feature.getFeatureType(), Parser.Type.BOOLEAN, Parser.Type.STRING))
        {
            Map<Comparable, List<Map<String, Comparable>>> valueSubSets = feature.getValueToDataMap(trainingData);
            for (Map.Entry<Comparable, List<Map<String, Comparable>>> exampleSubsetMap : valueSubSets.entrySet()) {
                FeatureNode child = buildTree(target,features.stream().collect(Collectors.toList()),exampleSubsetMap.getValue());
                if(child != null) {
                    node.addEdge(exampleSubsetMap.getKey().toString(),exampleSubsetMap.getKey(),child);
                    log.debug("{} -- hasChild -> {} for value: {}: {}",node.getName(),child.getName(),feature.getName(),child.getParentEdgeName() );
                }
            }
        }else
        {
            Comparable lowestEntropyValue = getValueWithLowestEntropy(target,feature,trainingData);
            Predicate<Map<String, Comparable>> numberPredicate = feature.addValue(lowestEntropyValue);

            features.add(feature);

            List<Map<String, Comparable>> positiveSubSet = trainingData.stream()
                    .filter(m -> m.containsKey(feature.getName())) // filter data that has feature
                    .filter(m -> numberPredicate.test(m)) // filter data that has o value for feature
                    .collect(Collectors.toList());
            FeatureNode positiveChild = buildTree(target,features.stream().collect(Collectors.toList()), positiveSubSet);
            if (positiveChild != null) {
                node.addEdge(">=" +lowestEntropyValue,lowestEntropyValue,positiveChild);
                log.debug("Added child {} -> {}",positiveChild.getName(), node.getName() );
            }

            List<Map<String, Comparable>> negativeSubset = trainingData.stream()
                    .filter(m -> m.containsKey(feature.getName())) // filter data that has feature
                    .filter(m -> numberPredicate.negate().test(m)) // filter data that has o value for feature
                    .collect(Collectors.toList());
            FeatureNode negativeChild = buildTree(target,features.stream().collect(Collectors.toList()), negativeSubset);
            if (negativeChild != null) {
                node.addNegativeEdge("<"+lowestEntropyValue,lowestEntropyValue,negativeChild);
                log.debug("Added child {} -> {}",negativeChild.getName(), node.getName() );
            }

        }
        return node;
    }


    public static Comparable getValueWithLowestEntropy(Feature target, Feature feature, List<Map<String, Comparable>> trainingData)
    {
        PreCheck.ifNull("Unable to get predicate for continuous feature " + feature.getName() + ". Null parameters found"
                ,target,feature,trainingData);

        Double bestEntropy = 1d;
        Comparable value = null;
        int bestEntropySize = 0;
        for (Map<String, Comparable> example : trainingData) {
            Comparable valueToTest = example.get(feature.getName());
            if(feature.getValues().contains(valueToTest)) continue;

            Predicate<Map<String, Comparable>> valuePredicate = x -> x.get(feature.getName()).compareTo(valueToTest) >= 0;

            List<Map<String,Comparable>> exampleSubSet = trainingData.stream()
                    .filter(m -> valuePredicate.test(m))
                    .collect(Collectors.toList());
            Double entropyOfSubset = target.getEntropy(exampleSubSet);
            log.debug("Feature: {} Value: {} Entropy: {} Dist: {}",feature.getName(),valueToTest,entropyOfSubset, target.getValueCounts(exampleSubSet));

            if((entropyOfSubset < bestEntropy
                    || (entropyOfSubset.equals(bestEntropy) && exampleSubSet.size() > bestEntropySize ))){
                log.warn("{}= {} New best entropy: {} -> {}",feature.getName(),valueToTest,bestEntropy,entropyOfSubset);
                bestEntropy = entropyOfSubset;
                value = valueToTest;
                bestEntropySize = exampleSubSet.size();
            }
        }
        return value;
    }

    public static Feature getBestInfoGainFeature(Feature target, List<Feature> features, List<Map<String, Comparable>> datas)
    {
        PreCheck.ifNull("Unable to get best feature with null value for target/features/example",target,features,datas);
        Double targetEntropy = target.getEntropy(datas);
        Feature bestFeature = null;
        Double bestGain = 0d;
        for (Feature feature : features) {
            Map<Comparable, List<Map<String, Comparable>>> valueMap = feature.getValueToDataMap(datas);
            Double featureEntropy = 0d;
            for (Map.Entry<Comparable, List<Map<String, Comparable>>> examplesWithValue : valueMap.entrySet()) {
                Double ratio = (double) examplesWithValue.getValue().size() / (double) datas.size();
                Double featureValueEntropy = target.getEntropy(examplesWithValue.getValue());
                featureEntropy += ratio *  featureValueEntropy;
            }
            Double gain = targetEntropy - featureEntropy;
            if(bestGain < gain) {
                bestFeature = feature;
                bestGain = gain;
            }

        }
        return bestFeature;
    }

}
