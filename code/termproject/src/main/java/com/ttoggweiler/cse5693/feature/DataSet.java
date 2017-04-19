package com.ttoggweiler.cse5693.feature;

import com.ttoggweiler.cse5693.util.MoreMath;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Helper class that holds a data set
 * has util methods for getting information about that data
 */
public class DataSet implements Iterable<Map<String, Comparable>>
{
    private static boolean useParallelStreams = false;
    private static int parallelThreshold = 100;

    private List<Feature> features;
    private List<Feature> targetFeatures;
    private List<Map<String, Comparable>> data;

    public DataSet(Collection<Feature> features, Collection<Feature> targetFeatures, List<Map<String, Comparable>> data)
    {
        this.features = new ArrayList<>(features);
        this.targetFeatures = new ArrayList<>(targetFeatures);
        this.data = data;
    }

    /* Subsets */

    public DataSet subset(List<Map<String, Comparable>> subsetOfData)
    {
        return new DataSet(new HashSet<>(features), new HashSet<>(targetFeatures), new ArrayList<>(subsetOfData));
    }

    /**
     * Gets subset of data that has the provided feature value
     * @param feature attribute whos value to use
     * @param value value of attribute to match
     * @return DataSet containing subset of data with feature == value
     */
    public DataSet subsetForFeatureValue(Feature feature, Comparable value)
    {
        return subset(data.parallelStream()
                .filter(example -> example.get(feature.getName()).equals(value))
                .collect(Collectors.toList()));
    }

    /**
     * Partitions the input data based on this features values.
     * @param feature who's values will be used as keys for subset map
     * @return Map of features values to a sub-DataSet, with that feature value
     */
    public Map<Comparable, DataSet> subsetOnFeatureValues(Feature feature)
    {
        PreCheck.ifNull("Null feature detected while trying to spilt dataSet",feature);
        return getFeatureValuesInData(feature).stream().collect(
                Collectors.toMap(val -> val, val -> subsetForFeatureValue(feature,val)));
    }

    public DataSet subsetOnPredicate(Predicate<Map<String,Comparable>> predicate)
    {
        return subset(data.stream().filter(predicate :: test).collect(Collectors.toList()));
    }

    /* Feature Queries */

    public Set<Comparable> getFeatureValuesInData(Feature feature)
    {
        return data.stream().map(m -> m.get(feature.getName())).collect(Collectors.toSet());
    }

    public Map<Comparable, Integer> getValueCountsForFeature(Feature feature)
    {
        return subsetOnFeatureValues(feature).entrySet().stream().collect(
                Collectors.toMap(Map.Entry :: getKey,m -> m.getValue().size()));
    }

    /* Calculations */

    // todo metrics and test performance
    // Entropy
    public Double getTargetEntropyForFeatureValue(Feature target, Feature feature, Comparable value)
    {
        // Partition on value, compute the target entropy
        return subsetForFeatureValue(feature,value).getEntropyOfFeatureValues(target);
    }

    public Map<Comparable, Double> mapFeatureValuesToTargetEntropy(Feature target, Feature feature)
    {
        return getFeatureValuesInData(feature).parallelStream().collect(
                Collectors.toMap(c -> c,val -> getTargetEntropyForFeatureValue(target,feature,val)));
    }

    public Double getEntropyOfFeatureValues(Feature feature)
    {
        return getStream(getValueCountsForFeature(feature).values())
                .filter(PreCheck::notNull)
                .filter(count -> count > 0)
                .mapToDouble(c -> c /(data.size()+0d) )// get ratio of value
                .map(ratio -> (-1 * ratio) * MoreMath.log2(ratio)) // Entropy of value //fixme check calc
                .sum(); // Entropy of all values ~ Feature Entropy
    }

    // Info Gain
    public Double getInfoGainForFeatureOnTarget(Feature feature, Feature target)
    {
        return getEntropyOfFeatureValues(target) - getEntropyOfFeatureValues(feature);
    }

    public Map<Feature, Double> mapFeaturesToInfoGainFor(Feature target)
    {
        return getStream(features).collect(
                Collectors.toMap(f -> f,f -> getInfoGainForFeatureOnTarget(f,target)));
    }

    public Feature getFeatureWithBestInfoGainFor(Feature target)
    {
        PreCheck.ifNull("Unable to get best feature with null value for target/features/example", target, features, data);
        Double targetEntropy = target.getEntropy(data);
        Feature bestFeature = null;
        Double bestGain = 0d;
        for (Feature feature : features) {
            Map<Comparable, List<Map<String, Comparable>>> valueMap = feature.splitDataOnValues(data);
            Double featureEntropy = 0d;
            for (Map.Entry<Comparable, List<Map<String, Comparable>>> examplesWithValue : valueMap.entrySet()) {
                Double ratio = (double) examplesWithValue.getValue().size() / (double) size();
                Double featureValueEntropy = target.getEntropy(examplesWithValue.getValue());
                featureEntropy += ratio * featureValueEntropy;
            }
            Double gain = targetEntropy - featureEntropy;
            if (bestGain < gain) {
                bestFeature = feature;
                bestGain = gain;
            }
        }

        Feature bestFeature2 = mapFeaturesToInfoGainFor(target).entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry :: getValue)).map(Map.Entry :: getKey)
                .orElseThrow(() -> new IllegalStateException("No best feature for info gain was able to be found"));
        return bestFeature;
    }

    /* Streaming of data set */

    /**
     * Returns a sequential {@code Stream} with the data set as its source.
     *
     * @implSpec
     * The default implementation creates a sequential {@code Stream} from the
     * collection's {@code Spliterator}.
     *
     * @return a sequential {@code Stream} over the elements in this collection
     * @since 1.8
     */
    public Stream<Map<String, Comparable>> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns a possibly parallel {@code Stream} with this data set as its
     * source.  It is allowable for this method to return a sequential stream.
     *
     * @implSpec
     * The default implementation creates a parallel {@code Stream} from the
     * collection's {@code Spliterator}.
     *
     * @return a possibly parallel {@code Stream} over the elements in this
     * collection
     * @since 1.8
     */
    public Stream<Map<String, Comparable>> parallelStream()
    {
        return StreamSupport.stream(spliterator(), true);
    }

    /* Iterator Impl */
    @Override
    public Iterator<Map<String, Comparable>> iterator()
    {
        return data.iterator();
    }

    @Override
    public void forEach(Consumer<? super Map<String, Comparable>> action)
    {
        data.forEach(action);
    }

    @Override
    public Spliterator<Map<String, Comparable>> spliterator()
    {
        return this.data.spliterator();
    }

    /* Field methods */
    public List<Map<String, Comparable>> getData()
    {
        return this.data;
    }

    public Integer size()
    {
        return data.size();
    }

    public List<Feature> getFeatures()
    {
        return new ArrayList<>(features);
    }

    public List<Feature> getTargets()
    {
        return targetFeatures;
    }

    // TODO: ttoggweiler 4/19/17 util ?
    public <T> Stream<T> getStream(Collection<T> collection)
    {
        return (useParallelStreams && collection.size() > parallelThreshold)
                ? collection.parallelStream()
                : collection.stream();
    }
}
