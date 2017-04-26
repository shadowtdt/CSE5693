package com.ttoggweiler.cse5693.feature;

import com.ttoggweiler.cse5693.data.DataSet;
import com.ttoggweiler.cse5693.feature.value.Value;
import com.ttoggweiler.cse5693.util.ValueParser;
import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.MoreMath;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a feature and its values
 * provides util methods for operating on data and get meta/statistical information
 */
public class Feature extends Identity
{
    public static final String DEFAULT_DELIMINATOR = "\\s+";
    public static final int DEFAULT_INDEX_LABEL = 0;
    public static final int DEFAULT_INDEX_TYPE = 1;
    // Wild card
    public static final boolean DEFAULT_WILDCARD_ALLOW = true;
    public static final String DEFAULT_WILDCARD = "?";
    public static final Predicate<Comparable> isWildCard = (s) -> s.equals(DEFAULT_WILDCARD);
    public final Predicate<Map<String, Comparable>> wildPredicate = (m) ->
            m.get(name()) instanceof String
                    && isWildCard.test(m.get(name()));
    private boolean containsWildCards = true;

    private ValueParser.Type type;
    private List<Comparable> valueList;

    public Feature(String name, ValueParser.Type type, Set<Comparable> values)
    {
        setName(name);
        setFeatureType(type);
        setValues(new HashSet<>(values));
    }

    public Feature(String name, ValueParser.Type type, Comparable... values)
    {
        this(name, type, Arrays.stream(values).collect(Collectors.toSet()));
    }

    /* Mapping Discreet Values  0->1 */
    public Double mapFeatureValueToRealNumber(Comparable value)
    {
        if (isWildCard.test(value)) return 1d; // fixme
        if (PreCheck.contains(type, ValueParser.Type.REAL))
            return (Double) value;
        int index = valueList.indexOf(value);
        double sizeMod = 1d / (valueList.size() - 1);
        double realNumberForIndex = index * sizeMod;
        return realNumberForIndex;
    }

    public Comparable mapRealNumberToFeatureValue(double doubleValue)
    {
        if (PreCheck.contains(type, ValueParser.Type.REAL))
            return doubleValue;
        double sizeMod = (valueList.size() - 1);
        double rawIndex = sizeMod * doubleValue;
        int valueIndex = (int) Math.round(rawIndex);
        if (valueIndex >= valueList.size())
            valueIndex = valueList.size() - 1;
        Comparable value = valueList.get(valueIndex);
        return value;
    }

    public static Feature parseFeature(String featureString)
    {
        return parseFeature(featureString, null, null, null, null);
    }

    public static Feature parseFeature(String featureString, String deliminator, Integer labelIndex, Integer typeIndex, Boolean allowWildCards)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to parse feature from empty or null string."),
                featureString);

        List<String> splitFeature = Arrays.stream(featureString
                .split(PreCheck.defaultTo(deliminator, DEFAULT_DELIMINATOR)))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(PreCheck::notEmpty)
                .collect(Collectors.toList());

        if (splitFeature.size() < 2)
            throw new IllegalArgumentException("Detected not enough feature descriptors in " + featureString + "). Feature string must include at least a label and type or value parameters ex : \'IsValid T F\' OR \'IsValid Bool\' " + featureString);

        String name = splitFeature.get(PreCheck.defaultTo(labelIndex, DEFAULT_INDEX_LABEL));
        String typeStr = splitFeature.get(PreCheck.defaultTo(typeIndex, DEFAULT_INDEX_TYPE));
        splitFeature.remove(name);
        //splitFeature.remove(typeStr);

        ValueParser.Type type = ValueParser.getType(typeStr)
                .orElseThrow(() -> new IllegalArgumentException("Unknown type found " + typeStr));

        Feature feature = new Feature(name, type);
        if (PreCheck.defaultTo(allowWildCards, DEFAULT_WILDCARD_ALLOW) && splitFeature.contains(DEFAULT_WILDCARD))
            feature.containsWildCards = splitFeature.remove(DEFAULT_WILDCARD);
        Set<Comparable> values = new HashSet<>();
        switch (type) {
            case BOOLEAN:
                splitFeature.stream().map(ValueParser::toBoolean)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(values::add);

                break;
            case INTEGER:
                splitFeature.stream().map(ValueParser::toInteger)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(values::add);
                break;
            case REAL:
                splitFeature.stream().map(ValueParser::toDouble)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(values::add);
                break;
            case TEXT:
                splitFeature.forEach(values::add);
                break;
        }
        feature.setValues(values);
        return feature;
    }

    /**
     * Depending on the features type, returns an optional of that value parsed from the provided string
     * @param valueString string to parse value for
     * @return Optional of the parsed value
     */
    public <T extends Comparable> Optional<T> parseValue(String valueString)
    {
        // TODO: ttoggweiler 2/16/17 validate on value set
        if (PreCheck.isEmpty(valueString)) return Optional.empty();

        if (isWildCard.test(valueString)) {
            return Optional.of((T) DEFAULT_WILDCARD);
        }

        switch (getFeatureType()) {
            case BOOLEAN:
                return (ValueParser.toBoolean(valueString).map(v -> (T) v));
            case INTEGER:
                return (ValueParser.toInteger(valueString).map(v -> (T) v));
            case REAL:
                return (ValueParser.toDouble(valueString).map(v -> (T) v));
            case TEXT:
                return Optional.of(valueString.toLowerCase().trim()).map(v -> (T) v);
            default:
                throw new IllegalArgumentException("Unable to detect Type value for String: " + valueString);
        }

    }

    public Collection<Comparable> getAllValues()
    {
        return PreCheck.isEmpty(valueList)? Collections.emptyList()
                : new ArrayList<>(valueList);
    }

    private void setValues(Set<Comparable> values)
    {
        if (PreCheck.notEmpty(values)) {
            //PreCheck.defaultTo(valueList, new ArrayList<Comparable>()).addAll(values);
            values.forEach(this::addValue);
        }
    }

    public ValueParser.Type getFeatureType()
    {
        return type;
    }

    public void setFeatureType(ValueParser.Type type)
    {
        if (!PreCheck.isNull(type)) this.type = type;
    }

    /**
     *
     * @return Number of unique values
     */
    public int size()
    {
        return getAllValues().size();
    }

    @Override
    public String toString()
    {
        return this.name() + " : " + getAllValues().toString();
    }

    @Override
    public String describe(boolean useNewlines)
    {
        return useNewlines
                ? "ID: " + getId()
                + "\nName: " + name()
                + "\nValues: " + getAllValues()
                : name() + ": " + getAllValues();
    }

/* Deprecated */
    // Most were operation on data, moved to DataSet Class
    // Some are predicates, which should be moved away from.
    @Deprecated // move away from predicates
    private Map<Comparable, Predicate<Map<String, Comparable>>> values = new HashMap<>();

    @Deprecated // move away from predicates
    public Predicate<Map<String, Comparable>> addValue(Comparable value)
    {
        PreCheck.ifNull("Unable to add null value to feature " + this.name(), value);
        // Don't add Wildcard to value sets
        if (isWildCard.test(value))
            return wildPredicate;

        if (PreCheck.isEmpty(this.values)) this.values = new HashMap<>();
        if (PreCheck.isEmpty(valueList)) valueList = new ArrayList<>();

        Predicate<Map<String, Comparable>> valuePredicate = getPredicateForValue(value);
        this.values.put(value, valuePredicate);

        if (!valueList.contains(value)) valueList.add(value);

        return valuePredicate;
    }


    @Deprecated //Moved to value parser type Enum
    public boolean isContinuous()
    {
        return getFeatureType().isContinous();
    }

    @Deprecated // move away from predicates
    public Predicate<Map<String, Comparable>> getPredicateForValue(Comparable value)
    {
        if (isWildCard.test(value))
            return wildPredicate;

        if (PreCheck.contains(getFeatureType(), ValueParser.Type.BOOLEAN, ValueParser.Type.TEXT)) {
            return map -> wildPredicate.test(map)
                    || map.get(this.name()).equals(value);
        } else {
            return map -> wildPredicate.test(map)
                    || map.get(name()).compareTo(value) >= 0;
        }

    }

    @Deprecated // move away from predicates
    private Collection<Predicate<Map<String, Comparable>>> getValuePredicates()
    {
        return values.values();
    }

    @Deprecated // use DataSet#countValuesForFeature
    private Map<Comparable, Integer> getValueCounts(List<Map<String, Comparable>> examples)
    {
        Map<Comparable, Integer> valueCounts = new HashMap<Comparable, Integer>();
        splitDataOnValues(examples).forEach((k, v) -> valueCounts.put(k, v.size()));
        return valueCounts;
    }

    // Info based on dataset
    @Deprecated  // DataSet#GetEntropyOfFeature
    public Double getEntropy(List<Map<String, Comparable>> examples)
    {
        Double entropy = 0d;
        Map<Comparable, Integer> featureValueDist = this.getValueCounts(examples);
        Integer exampleCount = examples.size();

        // String entropyCalcStr = "";
        for (Object possibleValue : this.getAllValues()) {
            Integer matchingExampleCount = featureValueDist.get(possibleValue);
            if (matchingExampleCount == null || matchingExampleCount == 0)
                continue;
            Double matchingExampleRatio = (double) matchingExampleCount / (double) exampleCount;
            //String ratioString = matchingExampleCount + "/" + exampleCount;
            entropy += (-1 * matchingExampleRatio) * MoreMath.log2(matchingExampleRatio);
            //entropyCalcStr += " + -" + ratioString + " log_2 " + ratioString;
        }
        //log.debug("Entropy = {} = {}",entropy,entropyCalcStr);
        return entropy;
    }

    @Deprecated // use dataset entropy methods
    private Comparable getValueWithLowestEntropy(Feature target, DataSet data)
    {
        PreCheck.ifNull("Unable to get predicate for continuous feature " + name() + ". Null parameters found"
                , target, data);

        Double bestEntropy = 1d;
        Comparable value = null;
        int bestEntropySize = 0;
        for (Map<String, Comparable> example : data) {
            Comparable valueToTest = example.get(name());
            if (getAllValues().contains(valueToTest)) continue;

            Predicate<Map<String, Comparable>> valuePredicate = getPredicateForValue(valueToTest);

            List<Map<String, Comparable>> exampleSubSet = data.getData().stream()
                    .filter(m -> valuePredicate.test(m))
                    .collect(Collectors.toList());
            Double entropyOfSubset = target.getEntropy(exampleSubSet);

            if (value == null
                    || entropyOfSubset < bestEntropy
                    || (entropyOfSubset.equals(bestEntropy) && exampleSubSet.size() > bestEntropySize)) {
                bestEntropy = entropyOfSubset;
                value = valueToTest;
                bestEntropySize = exampleSubSet.size();
            }
        }
        return value;
    }

    /**
     * Partitions the input data based on this features values.
     * @param examples a set of inputs to map to feature value
     * @return Map for each value of this feature, to a list of examples with that value.
     * @deprecated Use {@link DataSet#subsetOnFeatureValues(Feature, boolean)}
     */
    @Deprecated // use DataSet#subsetOnFeatureValues
    public Map<Comparable, List<Map<String, Comparable>>> splitDataOnValues(List<Map<String, Comparable>> examples)
    {
        //PreCheck.ifEmpty(()->new IllegalStateException("Unable to partition example when no values are set on attribute."),examples);
        if (PreCheck.isEmpty(examples)) return Collections.emptyMap();

        Map<Comparable, List<Map<String, Comparable>>> valueMap = new HashMap<>();
        List<Map<String, Comparable>> filteredExamples = examples.stream()
                .filter(m -> m.containsKey(this.name())) // Target feature is present
                .collect(Collectors.toList());

        for (Map<String, Comparable> example : filteredExamples) {
            Comparable targetValue = example.get(this.name());
            valueMap.putIfAbsent(targetValue, new ArrayList<>());
            valueMap.get(targetValue).add(example);
        }
        return valueMap;
    }
}
