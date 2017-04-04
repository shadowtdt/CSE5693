package com.ttoggweiler.cse5693.feature;

import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.Optional;

/**
 * Parses primitives from strings
 * uses mainly built in parse methods with optional wrappers
 * Also supports getting type from string that includes class, common, magic names
 * ie: java.lang.Integer
 */
public class Parser
{
    //todo Date, binary, hex, ....
    public enum Type{
        BOOLEAN,
        FLOAT, DOUBLE,
        INTEGER, LONG,
        STRING
    }

    static public Optional<Type> getType(String string)
    {
        if(PreCheck.isEmpty(string))return Optional.empty();
        if(toBoolean(string).isPresent())return Optional.of(Type.BOOLEAN);
        if(toInteger(string).isPresent())return Optional.of(Type.INTEGER);
        if(toLong(string).isPresent())return Optional.of(Type.LONG);
        if(toFloat(string).isPresent())return Optional.of(Type.FLOAT);
        if(toDouble(string).isPresent())return Optional.of(Type.DOUBLE);
        switch (string.trim().toLowerCase())
        {
            case "bool":
            case "boolean":
            case "binary":
            case "java.lang.boolean":
                return Optional.of(Type.BOOLEAN);
            case "int":
            case "natural":
            case "discrete":
            case "java.lang.integer":
                return Optional.of(Type.INTEGER);
            case "long":
            case "java.lang.long":
                return Optional.of(Type.LONG);
            case "float":
            case "continuous":
            case "real":
            case "java.lang.float":
                return Optional.of(Type.FLOAT);
            case "double":
            case "java.lang.double":
                return Optional.of(Type.DOUBLE);
            case "string":
            case "java.lang.string":
            default: return Optional.of(Type.STRING);
        }
    }

    static public Optional<Boolean> toBoolean(String stringToParse)
    {
        if(PreCheck.isEmpty(stringToParse))return Optional.empty();
        switch (stringToParse.trim().toLowerCase())
        {
            case "t":
            case "true":
            case "1":
            case "yes":
            case "one":
                return Optional.of(Boolean.TRUE);
            case "f":
            case "false":
            case "0":
            case "no":
            case "zero":
                return Optional.of(Boolean.FALSE);
            default: return Optional.empty();
        }
    }

    static public Optional<Float> toFloat(String stringToParse)
    {
        if(PreCheck.isEmpty(stringToParse))return Optional.empty();
        try {
            return PreCheck.isEmpty(stringToParse) ? Optional.empty() : Optional.of(Float.parseFloat(stringToParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    static public Optional<Double> toDouble(String stringToParse)
    {
        if(PreCheck.isEmpty(stringToParse))return Optional.empty();
        try {
            return PreCheck.isEmpty(stringToParse) ? Optional.empty() : Optional.of(Double.parseDouble(stringToParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    static public Optional<Integer> toInteger(String stringToParse)
    {
        if(PreCheck.isEmpty(stringToParse))return Optional.empty();
        try {
            return PreCheck.isEmpty(stringToParse) ? Optional.empty() : Optional.of(Integer.parseInt(stringToParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    static public Optional<Long> toLong(String stringToParse)
    {
        if(PreCheck.isEmpty(stringToParse))return Optional.empty();
        try {
            return PreCheck.isEmpty(stringToParse) ? Optional.empty() : Optional.of(Long.parseLong(stringToParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
