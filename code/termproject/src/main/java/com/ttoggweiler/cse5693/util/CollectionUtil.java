package com.ttoggweiler.cse5693.util;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by ttoggweiler on 4/23/17.
 */
public class CollectionUtil
{
//    public static <T extends Comparable> T merge(T col1 ,T col2)
//    {
//
//        Collection<T> combined = col2
//    }

    /* Array */
    // TODO: ttoggweiler 4/26/17 pre checks , generic
    public static int[] fill(int size, int source)
    {
        int[] arr = new int[size];
        Arrays.fill(arr, source);
        return arr;
    }

    public static long[] fill(int size, long source)
    {
        long[] arr = new long[size];
        Arrays.fill(arr, source);
        return arr;
    }

    public static float[] fill(int size, float source)
    {
        float[] arr = new float[size];
        Arrays.fill(arr, source);
        return arr;
    }

    public static double[] fill(int size, double source)
    {
        double[] arr = new double[size];
        Arrays.fill(arr, source);
        return arr;
    }

}
