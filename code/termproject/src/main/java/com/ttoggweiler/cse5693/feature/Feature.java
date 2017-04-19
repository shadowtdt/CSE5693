package com.ttoggweiler.cse5693.feature;

import com.ttoggweiler.cse5693.util.MoreMath;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a feature and its values
 * provides util methods for operating on data and get meta/statistical information
 */
public class Feature
{
    private UUID id = UUID.randomUUID();
    private String name;
    private Parser.Type type;

    //todo better value handling
    @Deprecated // move away from predicates
    private Map<Comparable, Predicate<Map<String, Comparable>>> values = new HashMap<>();
    private List<Comparable> valueArray;

    public Feature(String name, Parser.Type type, Set<Comparable> values)
    {
        this.setName(name);
        this.setFeatureType(type);
        this.setValues(values);
    }

    public Feature(String name, Parser.Type type, Comparable... values)
    {
        this.setName(name);
        this.setFeatureType(type);
        if (PreCheck.notEmpty(values)) this.setValues(Arrays.stream(values).collect(Collectors.toSet()));
        this.valueArray = new ArrayList<>(Arrays.asList(values));
    }

    public UUID getId()
    {
        return id;
    }

    public String getName()
    {
        return PreCheck.defaultTo(name, id.toString());
    }

    public void setName(String name)
    {
        if (!PreCheck.isEmpty(name)) this.name = name.trim();
    }

    public Parser.Type getFeatureType()
    {
        return type;
    }

    public void setFeatureType(Parser.Type type)
    {
        if (!PreCheck.isNull(type)) this.type = type;
    }

    @Deprecated // Dangerous if subsetting dataset, consider useing DataSet#getFeatureValuesInSet
    public Collection<Comparable> getValues()
    {
        return values.keySet();
    }

    //todo put on type?
    public boolean isContinuous()
    {
        return PreCheck.contains(this.type, Parser.Type.INTEGER, Parser.Type.LONG, Parser.Type.FLOAT, Parser.Type.DOUBLE);
    }

    public Double featureValueToDouble(Comparable value)
    {
        if(value.equals("?")) return 1d; // fixme

        if (PreCheck.contains(getFeatureType(), Parser.Type.BOOLEAN, Parser.Type.STRING)) {
            if (!getValues().contains(value))
                throw new IllegalArgumentException("Feature " + getName() + " does not have a value for " + value);
            for (int i = 0; i < valueArray.size(); i++) {
                if (valueArray.get(i).equals(value)) return ((double) i * (1d / valueArray.size()));
            }
            throw new IllegalArgumentException("Value not found for feature:" + getName() + " value: " + value);
        } else {
            return Double.parseDouble(value.toString());
        }
    }

    public Comparable doubleToFeatureValue(double doubleValue)
    {
        Double partitionSize = (1d / valueArray.size());
        int index = ((int) Math.round(doubleValue / partitionSize));
        if (index > valueArray.size() - 1) index = valueArray.size() - 1;
        return valueArray.get(index);
    }

    @Deprecated // move away from predicates
    public Predicate<Map<String, Comparable>> getPredicateForValue(Comparable value)
    {
        if (value.equals("?"))
            return map -> map.get(getName()).equals(value);

        if (PreCheck.contains(getFeatureType(), Parser.Type.BOOLEAN, Parser.Type.STRING)) {
            return map -> map.containsKey(this.getName()) && map.get(this.getName()).equals(value);
        } else {
            return map -> map.get(getName()).equals("?")
                    ? false
                    : map.get(getName()).compareTo(value) >= 0;
        }

    }

    @Deprecated // move away from predicates
    public Collection<Predicate<Map<String, Comparable>>> getValuePredicates()
    {
        return values.values();
    }

    public void setValues(Set<Comparable> values)
    {
        if (!PreCheck.isEmpty(values)) values.forEach(this::addValue);
    }

    @Deprecated // move away from predicates
    public Predicate<Map<String, Comparable>> addValue(Comparable value)
    {
        if (PreCheck.isEmpty(this.values)) this.values = new HashMap<>();
        PreCheck.ifNull("Unable to add null value to feature " + this.getName(), value);

        Predicate<Map<String, Comparable>> valuePredicate = getPredicateForValue(value);
        this.values.put(value, valuePredicate);

        if (this.valueArray == null) this.valueArray = new ArrayList<>();
        this.valueArray.add(value);
        return valuePredicate;
    }

    @Deprecated // use DataSet#getValueCountsForFeature
    public Map<Comparable, Integer> getValueCounts(List<Map<String, Comparable>> examples)
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
        for (Object possibleValue : this.getValues()) {
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
    public Comparable getValueWithLowestEntropy(Feature target, DataSet data)
    {
        PreCheck.ifNull("Unable to get predicate for continuous feature " + getName() + ". Null parameters found"
                , target, data);

        Double bestEntropy = 1d;
        Comparable value = null;
        int bestEntropySize = 0;
        for (Map<String, Comparable> example : data) {
            Comparable valueToTest = example.get(getName());
            if (getValues().contains(valueToTest)) continue;

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
     * @deprecated Use {@link DataSet#subsetOnFeatureValues(Feature)}
     */
    @Deprecated // use DataSet#subsetOnFeatureValues
    public Map<Comparable, List<Map<String, Comparable>>> splitDataOnValues(List<Map<String, Comparable>> examples)
    {
        //PreCheck.ifEmpty(()->new IllegalStateException("Unable to partition example when no values are set on attribute."),examples);
        if (PreCheck.isEmpty(examples)) return Collections.emptyMap();

        Map<Comparable, List<Map<String, Comparable>>> valueMap = new HashMap<>();
        List<Map<String, Comparable>> filteredExamples = examples.stream()
                .filter(m -> m.containsKey(this.getName())) // Target feature is present
                .collect(Collectors.toList());

        for (Map<String, Comparable> example : filteredExamples) {
            Comparable targetValue = example.get(this.getName());
            valueMap.putIfAbsent(targetValue, new ArrayList<>());
            valueMap.get(targetValue).add(example);
        }
        return valueMap;
    }

    public static Feature parseFeature(String featureString)
    {
        String[] splitFeature = featureString.split("\\s+");
        if (splitFeature.length < 2)
            throw new IllegalArgumentException("Feature string must include a label and type/value parameters " + featureString);
        String name = splitFeature[0].trim();
        Parser.Type type = Parser.getType(splitFeature[1]).orElseThrow(() -> new IllegalArgumentException("Unknown type found " + splitFeature[1]));

        switch (type) {
            case BOOLEAN:
                Feature boolFeature = new Feature(name, type);
                // if there are ? values
                if (splitFeature.length > 3) {
                    for (int i = 1; i < splitFeature.length; i++) {
                        if (Parser.toBoolean(splitFeature[i]).isPresent())
                            boolFeature.addValue((Parser.toBoolean(splitFeature[i]).get()));
                        else
                            boolFeature.addValue(splitFeature[i]);
                    }
                    return boolFeature;
                } else
                    return new Feature(name, type, Boolean.TRUE, Boolean.FALSE);
            case INTEGER:
                Feature intFeature = new Feature(name, type);
                if (splitFeature.length > 2)
                    for (int i = 1; i < splitFeature.length; i++) {
                        intFeature.addValue(Parser.toInteger(splitFeature[i])
                                .orElseThrow(() -> new IllegalArgumentException("Failed to parse int from string; " + featureString)));
                    }
                return intFeature;
            case LONG:
                Feature longFeature = new Feature(name, type);
                if (splitFeature.length > 2)
                    for (int i = 1; i < splitFeature.length; i++) {
                        longFeature.addValue(Parser.toLong(splitFeature[i])
                                .orElseThrow(() -> new IllegalArgumentException("Failed to parse long from string; " + featureString)));
                    }
                return longFeature;
            case FLOAT:
                Feature floatFeature = new Feature(name, type);
                if (splitFeature.length > 2)
                    for (int i = 1; i < splitFeature.length; i++) {
                        floatFeature.addValue(Parser.toFloat(splitFeature[i])
                                .orElseThrow(() -> new IllegalArgumentException("Failed to parse float from string; " + featureString)));
                    }
                return floatFeature;
            case DOUBLE:
                Feature doubleFeature = new Feature(name, type);
                if (splitFeature.length > 2)
                    for (int i = 1; i < splitFeature.length; i++) {
                        doubleFeature.addValue(Parser.toDouble(splitFeature[i])
                                .orElseThrow(() -> new IllegalArgumentException("Failed to parse Double from string; " + featureString)));
                    }
                return doubleFeature;
            case STRING:
                Feature StringFeature = new Feature(name, type);
                if (splitFeature.length > 2)
                    for (int i = 1; i < splitFeature.length; i++) {
                        StringFeature.addValue(splitFeature[i].toLowerCase().trim());
                    }
                return StringFeature;
        }
        return null;

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
        Parser.Type type = Parser.getType(valueString)
                .orElseThrow(() -> new IllegalArgumentException("Failed to parse " + getName() + " feature from string: " + valueString));

        // If parsed type is not equal, but is an integer, assume index substituted values x -> 1, y -> 2 ...
        if (valueString.trim().equals("?")) return Optional.of((T) "?");
//        if(type != getFeatureType() && type.equals(Parser.Type.INTEGER) && !isContinuous())
//        {
//            Integer index = Parser.toInteger(valueString)
//                    .orElseThrow(() -> new IllegalArgumentException("Unexpected feature value type and index substituation has failed for feature: "+getName() + " on string: "+valueString));
//            return Optional.of((T)valueArray.get(index));
//        }

        switch (getFeatureType()) {
            case BOOLEAN:
                return (Parser.toBoolean(valueString).map(v -> (T) v));
            case INTEGER:
                return (Parser.toInteger(valueString).map(v -> (T) v));
            case LONG:
                return (Parser.toLong(valueString).map(v -> (T) v));
            case FLOAT:
                return (Parser.toFloat(valueString).map(v -> (T) v));
            case DOUBLE:
                return (Parser.toDouble(valueString).map(v -> (T) v));
            case STRING:
                return Optional.of((T) valueString.toLowerCase().trim());
            default:
                throw new IllegalStateException("Feature has no type!");
        }

    }

    public String toString()
    {
        return this.getName() + " : " + getValues().toString();
    }

}
