package com.ttoggweiler.cse5693.util;

import java.util.Collections;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 4/22/17.
 */
public class MapUtil
{

    public final Supplier<NullPointerException> nullPointSupply = () -> new NullPointerException("Null map detected");

    public static <T extends Object & Comparable<? super T>> T minVal(Map<?,T> map)
    {
        PreCheck.ifNull("Unable to get keys for max value with null map",map);
        if(PreCheck.isEmpty(map))return null;
        return  Collections.min(map.values());
    }

    public static <T extends Object & Comparable<? super T>> T maxVal(Map<?, T> map)
    {
        PreCheck.ifNull("Unable to get keys for max value with null map",map);
        if(PreCheck.isEmpty(map))return null;
        return  Collections.max(map.values());
    }

    public static <S, T extends Object & Comparable<? super T>> Set<S> keysForMin(Map<S, T> map)
    {
        PreCheck.ifNull("Unable to get keys for min value with null map",map);
        if(PreCheck.isEmpty(map))return Collections.emptySet();
        Comparable minVal = minVal(map);
        return map.entrySet().stream()
                .filter(e -> e.getValue().equals(minVal))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static <S,T extends Object & Comparable<? super T>> Set<S> keysForMax(Map<S, T> map)
    {
        PreCheck.ifNull("Unable to get keys for max value with null map",map);
        if(PreCheck.isEmpty(map))return Collections.emptySet();

        Comparable maxVal = maxVal(map);
        return map.entrySet().stream()
                .filter(e -> e.getValue().equals(maxVal))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

}
