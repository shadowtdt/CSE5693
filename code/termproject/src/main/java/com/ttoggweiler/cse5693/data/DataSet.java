package com.ttoggweiler.cse5693.data;

import com.sun.corba.se.impl.naming.namingutil.CorbalocURL;
import com.sun.org.apache.regexp.internal.RE;
import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.MapUtil;
import com.ttoggweiler.cse5693.util.MoreMath;
import com.ttoggweiler.cse5693.util.PreCheck;
import com.ttoggweiler.cse5693.util.RandomUtil;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Helper class that holds a data set
 * has util methods for getting information about that data
 * TODO: ttoggweiler 4/23/17  metrics,performance, stream testing
 * // TODO: ttoggweiler 4/25/17 remove feature,
 * // TODO: ttoggweiler 4/25/17 cache common coputations, info gain, entropy, dist
 * **/
public class DataSet extends Identity implements Iterable<Map<String, Comparable>>
{
    private static boolean useParallelStreams = true;
    private static int parallelThreshold = 100;

    private List<Feature> features;
    private List<Feature> targetFeatures;
    private List<Map<String, Comparable>> data;
    private Map<Feature, Map<Comparable, Integer>> targetdist = null;

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

    public DataSet subsetWithSize(int count)
    {
        return subset(RandomUtil.randomElements(getData(),count));
    }

    public DataSet subsetWithPercent(float percent)
    {
        return subset(RandomUtil.randomElements(getData(),percent));
    }

    /**
     * Gets subset of data that has the provided feature value
     * @param feature attribute whos value to use
     * @param value value of attribute to match
     * @return DataSet containing subset of data with feature == value
     */
    public DataSet subsetForFeatureValue(Feature feature, Comparable value, boolean equalitySplit)
    {
        if (Feature.isWildCard.test(value))
            return subset(getData());
        Predicate<Map<String, Comparable>> featurePredicate = feature.getPredicateForValue(value);
        return equalitySplit
                ? subset(data.stream().filter(example -> example.get(feature.name()).equals(value)).collect(Collectors.toList()))
                : subset(data.stream().filter(featurePredicate).collect(Collectors.toList()));
    }

    /**
     * Partitions the input data based on this features values.
     * @param feature who's values will be used as keys for subset map
     * @return Map of features values to a sub-DataSet, with that feature value
     */
    public Map<Comparable, DataSet> subsetOnFeatureValues(Feature feature, boolean equalitySplit)
    {
        PreCheck.ifNull("Null feature detected while trying to spilt dataSet", feature);
        return getFeatureValuesInData(feature).stream().collect(
                Collectors.toMap(val -> val, val -> subsetForFeatureValue(feature, val, equalitySplit)));
    }

    public DataSet subsetOnPredicate(Predicate<Map<String, Comparable>> predicate)
    {
        return subset(data.stream().filter(predicate::test).collect(Collectors.toList()));
    }

    public DataSet noisySubset(float percent)
    {
        if (percent < 0) return this;
        List<Map<String, Comparable>> data = getData();

        List<Map<String, Comparable>> corruptSet = RandomUtil.randomElements(getData(), percent);
        data.removeAll(corruptSet);

        for (Map<String, Comparable> corruptExample : corruptSet) {
            for (Feature targetFeature : RandomUtil.randomElements(getTargets(), percent)) {
                Collection<Comparable> validVals = targetFeature.getAllValues();
                Comparable oldVal = corruptExample.get(targetFeature.key());
                validVals.remove(oldVal);
                Comparable newVal = RandomUtil.randomElement(validVals);
                corruptExample.put(targetFeature.key(), newVal);
            }
        }
        data.addAll(corruptSet);
        return subset(data);
    }


    /* Data Transformations */
    // TODO: ttoggweiler 4/23/17 remove wild cards, enumeration, write to disk
    public Map<String, Double> convertValuesToDouble(Map<String, Comparable> example)
    {
        Map<String, Double> example_double = new HashMap<>(getFeatures().size());
        for (Feature inputFeature : getAllFeatures()) {
            Double doubleValue = inputFeature.mapFeatureValueToRealNumber(example.get(inputFeature.name()));
            example_double.put(inputFeature.name(), doubleValue);
        }
        return example_double;
    }

    /* Feature Queries */

    public Set<Comparable> getFeatureValuesInData(Feature feature)
    {
        return getStream(data)
                .map(m -> m.get(feature.name()))
                .filter(Feature.isWildCard.negate())
                .collect(Collectors.toSet());
    }

    public Map<Comparable, Integer> countValuesForFeature(Feature feature, boolean equalitySplit)
    {
        if(PreCheck.isEmpty(targetdist))
            targetdist = new HashMap<>(feature.size());

        Map<Comparable, Integer> featDist = null;
        if (targetdist.containsKey(feature)) {
            featDist = targetdist.get(feature);
        }
        else{
            featDist = subsetOnFeatureValues(feature, equalitySplit).entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, m -> m.getValue().size()));
            targetdist.put(feature, featDist);
        }
        return featDist;
    }

    /* Calculations */

    // Entropy
    public Double getEntropyOfFeatureValues(Feature feature, boolean equalitySplit)
    {
        return getStream(countValuesForFeature(feature, equalitySplit).entrySet())
                .filter(e -> Feature.isWildCard.negate().test(e.getKey()))
                .map(Map.Entry::getValue)
                .filter(PreCheck::notNull)
                .filter(count -> count > 0)
                .mapToDouble(c -> c / (data.size() + 0d))// get ratio: #valueCount/datasize
                .map(ratio -> (-1d * ratio) * MoreMath.log2(ratio)) // Entropy of value //fixme check calc
                .sum(); // Entropy of all values ~ Feature Entropy
    }

    public Double getTargetEntropyForFeatureValue(Feature target, Feature feature, Comparable value, boolean equalitySplit)
    {
        if (Feature.isWildCard.test(value)) return 1d;
        // Partition on feature value
        DataSet examplesWithFeatureValue = subsetForFeatureValue(feature, value, equalitySplit);
        // Compute entropy of target feature on the subset
        Double subsetTargetEntropy = examplesWithFeatureValue.getEntropyOfFeatureValues(target, true);
        // Scale entropy based on proportion of examples with feature value
        Double proportionalEntropy = (examplesWithFeatureValue.size() / (size() + 0d)) * subsetTargetEntropy;
        return proportionalEntropy;
    }

    public Map<Comparable, Double> mapFeatureValuesToTargetEntropy(Feature feature, Feature target, boolean equalitySplit)
    {

        return getStream(getFeatureValuesInData(feature))
                .collect(Collectors.toMap(c -> c, val -> getTargetEntropyForFeatureValue(target, feature, val, equalitySplit)));
    }

    // Info Gain
    public Double getInfoGainForFeatureOnTarget(Feature feature, Feature target, boolean equalitySplit)
    {
        // Get current entropy of target feature
        Double targetEntropy = getEntropyOfFeatureValues(target, !target.getFeatureType().isContinous());
        // For each value of feature, split data on that value and get new entropy of target,
        Map<Comparable, Double> valueToEntropyMap = mapFeatureValuesToTargetEntropy(feature, target, equalitySplit);
        // If split using .equals, summation, if split using <= (for continuous), find lowest entropy
        Double entropyDeltaForFeature = equalitySplit
                ? getStream(valueToEntropyMap.values()).mapToDouble(s -> s).sum()
                : MapUtil.minVal(valueToEntropyMap);
        if (entropyDeltaForFeature == null)
            entropyDeltaForFeature = targetEntropy;
        // subtract the new entropy from the original to find how much this feature will lower the target entropy if selected
        Double gain = targetEntropy - entropyDeltaForFeature;
        return gain;
    }

    public Map<Feature, Double> mapFeaturesToInfoGainFor(Feature target)
    {
        return getStream(features).collect(
                Collectors.toMap(f -> f, f -> getInfoGainForFeatureOnTarget(f, target, !f.getFeatureType().isContinous())));
    }

    public Feature getFeatureWithBestInfoGainFor(Feature target)
    {
        PreCheck.ifNull("Unable to get best feature with null value for target/features/example", target, features, data);
        return getStream(mapFeaturesToInfoGainFor(target).entrySet())
                .max(Comparator.comparingDouble(Map.Entry::getValue)).map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("No best feature for info gain was able to be found"));
    }

    /* Loading */

    /**
     * Loads and parses features from provided Path
     * @param path path of the file to load
     * @return A set of features
     * @throws IOException when files does not exist or is not readable
     */
    public static List<Map<String, Comparable>> loadDataFromPath(Path path, List<Feature> featureSet) throws IOException
    {
        if (path == null) throw new NullPointerException("Unable to load features from a null path");
        List<String> lines = Files.lines(path)
                .filter(PreCheck::notEmpty)
                .collect(Collectors.toList());

        List<Map<String, Comparable>> data = new ArrayList<>();
        // For each example
        for (String line : lines) {
            String[] splitLine = line.trim().split("\\s+");
            if (splitLine.length != featureSet.size())
                throw new IllegalArgumentException("Data line does not have correct number of features: " + line);
            Map<String, Comparable> dataMap = new HashMap<>();
            // for each value in example

            for (int i = 0; i < featureSet.size(); i++) {
                Feature feature = featureSet.get(i);
                Comparable featureValue = feature.parseValue(splitLine[i])
                        .orElseThrow(() -> new IllegalArgumentException("Failed to parse feature " + feature.name() + "from line: " + line));
                dataMap.put(feature.name(), featureValue);
                feature.addValue(featureValue);
            }
            data.add(dataMap);
        }
        return data;
    }

    /**
     * Loads and parses features from provided file path
     * @param pathToData path to file to load
     * @return A set of features parsed from the provided file
     * @throws IOException when files does not exist or is not readable
     */
    public static List<Map<String, Comparable>> loadDataFromFile(String pathToData, List<Feature> featureSet) throws IOException
    {
        if (PreCheck.isEmpty(pathToData))
            throw new NullPointerException("Unable to load features from a null file path string");
        URL resource = DataLoader.class.getResource(pathToData);
        Path path = resource == null ? Paths.get(pathToData) : Paths.get(resource.getPath());
        return loadDataFromPath(path, featureSet);
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

    /* Identity Impl */

    @Override
    public String describe(boolean useNewlines)
    {
        Map<Comparable, Integer> dist = countValuesForFeature(getTargets().get(0), true); // fixme
        String distString = "Dist:" + size() + " " + dist.toString();
        return useNewlines
                ? fullName() + ":"
                + "\nFeatures: " + getFeatures().toString()
                + "\nTargets: " + getTargets().toString()
                + "\n" + distString
                : shortName() + " " + distString;
    }

    /* Field methods */
    public List<Map<String, Comparable>> getData()
    {
        return new ArrayList<>(this.data);
    }

    public int size()
    {
        return data.size();
    }

    public List<Feature> getFeatures()
    {
        return new ArrayList<>(features);
    }

    public List<Feature> getAllFeatures()
    {
        List<Feature> allFeatures = new ArrayList<>(getFeatures());
        allFeatures.addAll(getTargets());
        return allFeatures;
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
