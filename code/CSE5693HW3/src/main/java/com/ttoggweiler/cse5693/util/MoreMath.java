package com.ttoggweiler.cse5693.util;

import java.util.Collection;

/**
 * Created by ttoggweiler on 2/18/17.
 */
public class MoreMath
{
    public static double log2(double x)
    {
        return logx(2d,x);
    }

    public static double logx(double x, double value)
    {
        return Math.log(value)/Math.log(x);
    }

    public static double sigmoidFunction(double sumOfInputs)
    {
        return 1 / (1 + Math.exp( (-1 * sumOfInputs)));
    }
}
