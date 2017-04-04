package com.ttoggweiler.cse5693.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 * Util class with static methods for interacting with Random
 */
public class RandomUtil
{
    public static Random rand = new Random();


    public static boolean probability(double prob)
    {
        return (prob > 1) || rand.nextDouble() <= prob;
    }

    public static <T> T selectRandomElement(Collection<T> collection)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to get random element from null or empty collection"),collection);
        return collection.stream().unordered().skip(rand.nextInt(collection.size())).findFirst().get();
    }

    public static <T> Collection<T> selectRandomElements(Collection<T> collection, int numberToSelect)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to get random elements from null or empty collection"),collection);
        if(numberToSelect <= 0)return Collections.emptySet();
        else if(numberToSelect >= collection.size())return collection;

        Collection<T> selected = new HashSet<>(numberToSelect);

        while(selected.size() != numberToSelect)
            selected.add(selectRandomElement(collection));

        return selected;
    }

    public static <T> Collection<T> selectRandomPercent(Collection<T> collection, double percent)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to get random elements from null or empty collection"),collection);
        if(percent <= 0)return Collections.emptySet();
        else if(percent >= 1)return collection;

        int numberToSelect = (int)Math.round(collection.size() * percent);
        return selectRandomElements(collection,numberToSelect);
    }

}
