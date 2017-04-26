package com.ttoggweiler.cse5693.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    /* Collection */
    public static <T,C extends Collection<T>> T randomElement(C collection)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to get random element from null or empty collection"),collection);
        int randomIndex = rand.nextInt(collection.size());
        for (T t : collection) if(randomIndex-- <= 0) return t;
        throw new AssertionError();
    }

    public static <T> Collection<T> randomElements(Collection<T> collection, long numberToSelect)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to get random elements from null or empty collection"),collection);
        if(numberToSelect <= 0)return Collections.emptySet();
        else if(numberToSelect >= collection.size())return collection;

        Collection<T> selected = new HashSet<>((int)numberToSelect);
        Collection<T> collectionCopy = new ArrayList<T>(collection);

        while(selected.size() < numberToSelect) {
            T val = randomElement(collectionCopy);
            selected.add(val);
            collectionCopy.remove(val);
        }

        return selected;
    }

    public static <T>Collection<T> randomElements(Collection<T> collection, double percent)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to get random elements from null or empty collection"),collection);
        if(percent <= 0)return Collections.emptySet();
        else if(percent >= 1)return collection;

        int numberToSelect = (int)Math.round(collection.size() * percent);
        return randomElements(collection,numberToSelect);
    }

    /* List */
    public static <T,C extends List<T>> T randomElement(C collection)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to get random element from null or empty collection"),collection);
        int randomIndex = rand.nextInt(collection.size());
        for (T t : collection) if(randomIndex-- <= 0) return t;
        throw new AssertionError();
    }

    public static <T> List<T> randomElements(List<T> collection, long numberToSelect)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to get random elements from null or empty collection"),collection);
        if(numberToSelect <= 0)return Collections.emptyList();
        else if(numberToSelect >= collection.size())return collection;

        List<T> selected = new ArrayList<>((int)numberToSelect);
        List<T> listCopy = new ArrayList<>(collection);

        while(selected.size() < numberToSelect) {
            T val = randomElement(listCopy);
            selected.add(val);
            listCopy.remove(val);
        }

        return selected;
    }

    public static <T>List<T> randomElements(List<T> collection, double percent)
    {
        PreCheck.ifEmpty(() -> new IllegalArgumentException("Unable to get random elements from null or empty collection"),collection);
        if(percent <= 0)return Collections.emptyList();
        else if(percent >= 1)return collection;

        int numberToSelect = (int)Math.round(collection.size() * percent);
        if(numberToSelect < 1)numberToSelect = 1;
        return randomElements(collection,numberToSelect);
    }

    public static <T> List<T> shuffle(Collection<T> collection)
    {
        List<T> unordered = collection.stream().collect(Collectors.toList());
        Collections.shuffle(unordered,rand);
        return unordered;
    }


}
