package com.ttoggweiler.cse5693.feature;

import com.ttoggweiler.cse5693.util.MoreMath;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 2/15/17.
 */
public class Feature<T extends Comparable>
{
    private UUID id = UUID.randomUUID();
    private String name;
    private Parser.Type type;

    //todo better value handling
    private Map<T, Predicate<Map<String, T>>> values = new HashMap<>();
    private List<Comparable> valueArray;

    public Feature(String name, Parser.Type type, Set<T> values)
    {
        this.setName(name);
        this.setFeatureType(type);
        this.setValues(values);
    }

    public Feature(String name, Parser.Type type, T... values)
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

    public Collection<T> getValues()
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
        if (PreCheck.contains(getFeatureType(), Parser.Type.BOOLEAN, Parser.Type.STRING)) {
            if(!getValues().contains(value)) throw new IllegalArgumentException("Feature " + getName() + " does not have a value for " + value);
            for (int i = 0; i < valueArray.size(); i++) {
                if(valueArray.get(i).equals(value))return ((double) i * (1d/valueArray.size()) );
            }
            throw new IllegalArgumentException("Value not found for feature:" +getName() + " value: "+value);
        }else
        {
            return Double.parseDouble(value.toString());
        }
    }

    public Comparable doubleToFeatureValue(double doubleValue)
    {
        Double partitionSize = (1d/valueArray.size());
        int index = ((int) Math.round(doubleValue / partitionSize));
        if(index>valueArray.size()-1) index = valueArray.size()-1;
        return valueArray.get(index);
    }

    @Deprecated
    public Predicate<Map<String, T>> getPredicateForValue(T value)
    {
        if (!values.containsKey(value))
            throw new IllegalArgumentException("No predicate found for feature value: " + this.getName() + " : " + value);
        else return values.get(value);
    }

    public Collection<Predicate<Map<String, T>>> getValuePredicates()
    {
        return values.values();
    }

    public void setValues(Set<T> values)
    {
        if (!PreCheck.isEmpty(values)) values.forEach(this::addValue);
    }

    public Predicate<Map<String, T>> addValue(T value)
    {
        if (PreCheck.isEmpty(this.values)) this.values = new HashMap<>();
        PreCheck.ifNull("Unable to add null value to feature "+ this.getName(), value);

        Predicate<Map<String, T>> valuePredicate;
        if (PreCheck.contains(getFeatureType(), Parser.Type.BOOLEAN, Parser.Type.STRING)) {
            valuePredicate = map -> map.containsKey(this.getName()) && map.get(this.getName()).equals(value);
            this.values.put(value, valuePredicate);
        } else {
            valuePredicate = map -> map.containsKey(this.getName()) && map.get(this.getName()).compareTo(value) >= 0;
            this.values.put(value, valuePredicate);
        }
        if(this.valueArray == null) this.valueArray = new ArrayList<>();
        this.valueArray.add(value);
        return valuePredicate;
    }

    public Map<T, Integer> getValueCounts(List<Map<String, Comparable>> examples)
    {
        Map<T, Integer> valueCounts = new HashMap<T, Integer>();
        partitionExamplesByFeatureValues(examples).forEach((k, v) -> valueCounts.put(k, v.size()));
        return valueCounts;
    }


    // todo move to util class
    public Double getEntropy(List<Map<String, Comparable>> examples)
    {
        Double entropy = 0d;
        Map<T, Integer> featureValueDist = this.getValueCounts(examples);
        Integer exampleCount = examples.size();

        String entropyCalcStr = "";
        for (Object possibleValue : this.getValues()) {
            Integer matchingExampleCount = featureValueDist.get(possibleValue);
            if (matchingExampleCount == null || matchingExampleCount == 0)
                continue;
            Double matchingExampleRatio = (double) matchingExampleCount / (double) exampleCount;
            String ratioString = matchingExampleCount + "/" + exampleCount;
            entropy += (-1 * matchingExampleRatio) * MoreMath.log2(matchingExampleRatio);
            entropyCalcStr += " + -" + ratioString + " log_2 " + ratioString;
        }
        //log.debug("Entropy = {} = {}",entropy,entropyCalcStr);
        return entropy;
    }

    /**
     * Partitions the input data based on this features values.
     * @param examples a set of inputs to map to feature value
     * @return Map for each value of this feature, to a list of examples with that value.
     */
    public Map<T, List<Map<String, Comparable>>> partitionExamplesByFeatureValues(List<Map<String, Comparable>> examples)
    {
        PreCheck.ifEmpty(()->new IllegalStateException("Unable to partition example when no values are set on attribute."),examples);
        Map<T, List<Map<String, Comparable>>> valueMap = new HashMap<>();

        List<Map<String, Comparable>> filteredExamples = examples.stream()
                .filter(m -> m.containsKey(this.getName())) // Target feature is present
                .collect(Collectors.toList());

        for (Map<String, Comparable> example : filteredExamples) {
            Comparable targetValue = example.get(this.getName());
            valueMap.putIfAbsent((T) targetValue, new ArrayList<>());
            valueMap.get((T) targetValue).add(example);
        }
        return valueMap;
    }

    public static Feature<? extends Comparable> parseFeature(String featureString)
    {
        String[] splitFeature = featureString.split(" ");
        if (splitFeature.length < 2)
            throw new IllegalArgumentException("Feature string must include a label and type/value parameters " + featureString);
        String name = splitFeature[0].trim();
        Parser.Type type = Parser.getType(splitFeature[1]).orElseThrow(() -> new IllegalArgumentException("Unknown type found " + splitFeature[1]));

        switch (type) {
            case BOOLEAN:
                return new Feature<Boolean>(name, type, Boolean.TRUE, Boolean.FALSE);
            case INTEGER:
                Feature<Integer> intFeature = new Feature<Integer>(name, type);
                if (splitFeature.length > 2)
                    for (int i = 1; i < splitFeature.length; i++) {
                        intFeature.addValue(Parser.toInteger(splitFeature[i])
                                .orElseThrow(() -> new IllegalArgumentException("Failed to parse int from string; " + featureString)));
                    }
                return intFeature;
            case LONG:
                Feature<Long> longFeature = new Feature<Long>(name, type);
                if (splitFeature.length > 2)
                    for (int i = 1; i < splitFeature.length; i++) {
                        longFeature.addValue(Parser.toLong(splitFeature[i])
                                .orElseThrow(() -> new IllegalArgumentException("Failed to parse long from string; " + featureString)));
                    }
                return longFeature;
            case FLOAT:
                Feature<Float> floatFeature = new Feature<Float>(name, type);
                if (splitFeature.length > 2)
                    for (int i = 1; i < splitFeature.length; i++) {
                        floatFeature.addValue(Parser.toFloat(splitFeature[i])
                                .orElseThrow(() -> new IllegalArgumentException("Failed to parse float from string; " + featureString)));
                    }
                return floatFeature;
            case DOUBLE:
                Feature<Double> doubleFeature = new Feature<Double>(name, type);
                if (splitFeature.length > 2)
                    for (int i = 1; i < splitFeature.length; i++) {
                        doubleFeature.addValue(Parser.toDouble(splitFeature[i])
                                .orElseThrow(() -> new IllegalArgumentException("Failed to parse Double from string; " + featureString)));
                    }
                return doubleFeature;
            case STRING:
                Feature<String> StringFeature = new Feature<String>(name, type);
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
    public Optional<T> parseValue(String valueString)
    {
        // TODO: ttoggweiler 2/16/17 validate on value set
        if (PreCheck.isEmpty(valueString)) return Optional.empty();

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
