package com.ttoggweiler.cse5693.tree;

import com.ttoggweiler.cse5693.util.MoreMath;
import com.ttoggweiler.cse5693.util.Parser;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 2/15/17.
 */
public class Feature<T extends Comparable<?>>
{
    private UUID id = UUID.randomUUID();
    private String name;
    private Parser.Type type;
    private Set<T> values = new HashSet<>();

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

    public Set<T> getValues()
    {
        return values;
    }

    public void setValues(Set<T> values)
    {
        if (!PreCheck.isEmpty(values)) this.values = values;
    }

    public void addValue(T value)
    {
        if (PreCheck.isEmpty(this.values)) this.values = new HashSet<T>();
        if (!PreCheck.isNull(value)) this.values.add(value);
    }

    public Map<T,Integer> getValueCounts(List<Map<String, Comparable>> examples)
    {
        Map<T,Integer> valueCounts = new HashMap<T, Integer>();
        mapValueToData(examples).forEach((k,v) -> valueCounts.put(k,v.size()));
        return valueCounts;
//        Map<T, Integer> targetFeatureDistribution;
//
//        List<Map<String,Comparable>> filteredExamples = examples.stream()
//                .filter(m -> m.containsKey(this.getName())) // Target feature is present
//                .collect(Collectors.toList());
//        targetFeatureDistribution = new HashMap<>();
//        for (Map<String, Comparable> example : filteredExamples) {
//            Comparable targetValue = example.get(this.getName());
//            targetFeatureDistribution.putIfAbsent((T)targetValue,0);
//            targetFeatureDistribution.compute((T)targetValue,(k,v) -> v = v+1);
//        }
//        return targetFeatureDistribution;
    }

    public Double getEntropy(List<Map<String,Comparable>> examples)
    {
        Double entropy = 0d;
        Map<T,Integer> featureValueDist = this.getValueCounts(examples);
        Integer exampleCount = examples.size();

        String entropyCalcStr = "";
        for (Object possibleValue : this.getValues()) {
            Integer matchingExampleCount = featureValueDist.get(possibleValue);
            if(matchingExampleCount == null || matchingExampleCount == 0)
                continue;
            Double matchingExampleRatio = (double)matchingExampleCount/(double) exampleCount;
            String ratioString = matchingExampleCount+"/"+exampleCount;
            entropy += (-1*matchingExampleRatio) * MoreMath.log2(matchingExampleRatio);
            entropyCalcStr += " + -"+ratioString+" log_2 " + ratioString;
        }
        //log.debug("Entropy = {} = {}",entropy,entropyCalcStr);
        return entropy;
    }

    public Map<T, List<Map<String, Comparable>>> mapValueToData(List<Map<String, Comparable>> datas)
    {
        Map<T, List<Map<String, Comparable>>> valueMap = new HashMap<>();

        List<Map<String,Comparable>> filteredExamples = datas.stream()
                .filter(m -> m.containsKey(this.getName())) // Target feature is present
                .collect(Collectors.toList());

        for (Map<String, Comparable> example : filteredExamples) {
            Comparable targetValue = example.get(this.getName());
            valueMap.putIfAbsent((T)targetValue,new ArrayList<>());
            valueMap.get((T)targetValue).add(example);
        }
        return valueMap;
    }

    public static Feature parseFeature(String featureString)
    {
        String[] splitFeature = featureString.split(" ");
        if (splitFeature.length < 2) throw new IllegalArgumentException("Feature string must include a label and type/value parameters " + featureString );
        String name = splitFeature[0].trim();
        Parser.Type type = Parser.getType(splitFeature[1]).orElseThrow(() -> new IllegalArgumentException("Unknown type found " + splitFeature[1]));

        switch (type) {
            case BOOLEAN:
                return new Feature<Boolean>(name, type, Boolean.TRUE, Boolean.FALSE);
            case INTIGER:
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
                        StringFeature.addValue(splitFeature[i].trim());
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
                return ( Parser.toBoolean(valueString).map(v -> (T)v));
            case INTIGER:
                return ( Parser.toInteger(valueString).map(v -> (T)v));
            case LONG:
                return ( Parser.toLong(valueString).map(v -> (T)v));
            case FLOAT:
                return ( Parser.toFloat(valueString).map(v -> (T)v));
            case DOUBLE:
                return ( Parser.toDouble(valueString).map(v -> (T)v));
            case STRING:
                return Optional.of((T)valueString.trim());
            default: throw new IllegalStateException("Feature has no type!");
        }

    }

    public String toString()
    {
        return this.getName() + " : " + getValues().toString();
    }

}
