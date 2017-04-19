package com.ttoggweiler.cse5693.genetic.fitness.selection;

import com.ttoggweiler.cse5693.genetic.fitness.metric.Fitness;
import com.ttoggweiler.cse5693.rule.Hypothesis;

import java.util.Collection;

/**
 * Base class that all Fitness selectors will extend, Rank, Tournament, Proportional
 * Used by {@link com.ttoggweiler.cse5693.genetic.GeneticSearch} to select hypotheses for the next generation
 */
public interface FitnessSelector
{
    public abstract Collection<Hypothesis> selectClassifiers(long count, Collection<Fitness> classifierFitness);
}
