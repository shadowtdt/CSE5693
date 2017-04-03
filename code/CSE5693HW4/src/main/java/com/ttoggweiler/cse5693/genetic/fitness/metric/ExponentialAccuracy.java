package com.ttoggweiler.cse5693.genetic.fitness.metric;

import com.ttoggweiler.cse5693.rule.Classifier;
import com.ttoggweiler.cse5693.rule.Performance;
import com.ttoggweiler.cse5693.util.MoreMath;

import java.util.Collection;
import java.util.Map;

/**
 * Fitness = ( %correct/total )^power
 */
public class ExponentialAccuracy extends FitnessMetric
{
    private double power;

    public ExponentialAccuracy(double power)
    {
        this.power = power;
    }

    @Override
    double measureFitness(Performance performance)
    {
        return Math.pow(performance.getAccuracy(),this.power);
    }

    public double getPower()
    {
        return power;
    }

    public void setPower(double power)
    {
        this.power = power;
    }
}
