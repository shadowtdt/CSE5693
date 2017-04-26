package com.ttoggweiler.cse5693.util;

import java.util.Optional;

/**
 * Parses primitives from strings
 * uses mainly built in parse methods with optional wrappers
 * Also supports getting type from string that includes class, common, magic names
 * ie: java.lang.Integer, t, T, yes, ...
 */
public interface ValueParser<T extends Comparable>
{
    //todo Date, binary, hex, ip, mac, ...make types?
    public enum Type{
        BOOLEAN,
        TEXT,
        INTEGER,
        REAL;

        public boolean isContinous()
        {
            return ordinal()>INTEGER.ordinal();
        }
    }

    default Optional<? extends T> toValue(String stringToParse)
    {
        return Optional.empty();
    }

    static public Optional<Type> getType(String string)
    {
        if(PreCheck.isEmpty(string))return Optional.empty();

        if(toBoolean(string).isPresent())return Optional.of(Type.BOOLEAN);
        if(toInteger(string).isPresent()) return Optional.of(Type.INTEGER);
        if(toRealNumber(string).isPresent())return Optional.of(Type.REAL);
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
            case "long":
            case "java.lang.long":
                return Optional.of(Type.INTEGER);
            case "float":
            case "continuous":
            case "real":
            case "java.lang.float":
            case "double":
            case "java.lang.double":
                return Optional.of(Type.REAL);
            case "string":
            case "java.lang.string":
            default: return Optional.of(Type.TEXT);
        }
    }

    /**
     * Attempts to parse boolean value from string based on Common string values of boolean
     * String is trimmed of whitespace and made lowercase before comparison
     * true, t, yes, one, 1...
     * We do not use the build in parser {@link Boolean#parseBoolean(String)}
     * because it uses a limiting str.equals("true")? True:False ;logic
     * @param stringToParse
     * @return
     */
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

    static Optional<? extends Number> toNumber(String stringToParse)
    {
        return toInteger(stringToParse).isPresent()
                ? toInteger(stringToParse)
                : toRealNumber(stringToParse);
    }

    static Optional<? extends Number> toRealNumber(String stringToParse)
    {
        return toDouble(stringToParse);
    }

    static public Optional<Float> toFloat(String stringToParse)
    {
        try {
            return PreCheck.isEmpty(stringToParse) ? Optional.empty() : Optional.of(Float.parseFloat(stringToParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    static public Optional<Double> toDouble(String stringToParse)
    {
        try {
            return PreCheck.isEmpty(stringToParse) ? Optional.empty() : Optional.of(Double.parseDouble(stringToParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    static public Optional<Integer> toInteger(String stringToParse)
    {
        try {
            return PreCheck.isEmpty(stringToParse) ? Optional.empty() : Optional.of(Integer.parseInt(stringToParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    static public Optional<Long> toLong(String stringToParse)
    {
        try {
            return PreCheck.isEmpty(stringToParse) ? Optional.empty() : Optional.of(Long.parseLong(stringToParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
