package com.ttoggweiler.cse5693.genetic.fitness.metric;

import com.ttoggweiler.cse5693.rule.Performance;

/**
 * Base class that all Fitness metrics will extend
 * Used by {@link com.ttoggweiler.cse5693.genetic.GeneticSearch} to assign a fitness value to a classifier
 * The assigned fitness values should be absolute, meaning for a given data set,
 * the fitness value of the classifier will always be the same. *Not dependent on other classifiers fitness
 */
public abstract class FitnessMetric
{
    public abstract double measureFitness(Performance performance);
}
