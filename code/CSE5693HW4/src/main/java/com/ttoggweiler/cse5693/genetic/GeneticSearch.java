package com.ttoggweiler.cse5693.genetic;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.genetic.fitness.metric.ExponentialAccuracy;
import com.ttoggweiler.cse5693.genetic.fitness.metric.FitnessMetric;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessProportional;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessSelector;
import com.ttoggweiler.cse5693.rule.Classifier;
import com.ttoggweiler.cse5693.rule.Hypothesis;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Genetic Algorithm that returns the most successful hypotheses after multiple generations
 *
 * Pseudo:
 * Init CurrentGen with Random classifiers {@link com.ttoggweiler.cse5693.rule.Classifier}
 * While StoppingCriteria !met
 *  Select (1 - replacement%) * PopulationSize and add to next Generation {@link com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessSelector}
 *  Select (replacement% * PopulationSize) / 2 and add CrossOver results to next Generation {@link com.ttoggweiler.cse5693.genetic.population.OffspringFactory}
 *  Select (mutation% * PopulationSize) from next Generation and Mutate {@link com.ttoggweiler.cse5693.genetic.population.Mutator}
 *  Set CurrentGen = nextGen
 *  Compute Fitness for CurrentGen {@link com.ttoggweiler.cse5693.genetic.fitness.metric.FitnessMetric}
 */
public class GeneticSearch
{
    private FitnessMetric fitnessMetric = new ExponentialAccuracy(2);
    private FitnessSelector fitnessSelector = new FitnessProportional();

    private int generationLimit = 1000;
    private double fitnessThreshold = 0.9;

    private int populationSize = 100;
    private double crossoverReplacementRate = 0.6;
    private double mutationRate = .001;

    public GeneticSearch(int populationSize, double crossoverReplacementRate, double mutationRate)
    {
        this.populationSize = populationSize;
        this.crossoverReplacementRate = crossoverReplacementRate;
        this.mutationRate = mutationRate;
    }

    public Hypothesis generateClassifier(List<Feature> features, Feature targetFeature, Collection<Map<String, Comparable>> data)
    {

    }

    private static Collection<Hypothesis> generateRandomPopulation(int populationSize, List<Feature> features, Feature targetFeature)
    {
        Set<Hypothesis> generationZero = new HashSet<>();

        while(generationZero.size() != populationSize)
        {

        }

        return generationZero;
    }

    /* Getter n Setters */
    public FitnessMetric getFitnessMetric()
    {
        return fitnessMetric;
    }

    public void setFitnessMetric(FitnessMetric fitnessMetric)
    {
        this.fitnessMetric = fitnessMetric;
    }

    public FitnessSelector getFitnessSelector()
    {
        return fitnessSelector;
    }

    public void setFitnessSelector(FitnessSelector fitnessSelector)
    {
        this.fitnessSelector = fitnessSelector;
    }

    public int getGenerationLimit()
    {
        return generationLimit;
    }

    public void setGenerationLimit(int generationLimit)
    {
        this.generationLimit = generationLimit;
    }

    public double getFitnessThreshold()
    {
        return fitnessThreshold;
    }

    public void setFitnessThreshold(double fitnessThreshold)
    {
        this.fitnessThreshold = fitnessThreshold;
    }

    public int getPopulationSize()
    {
        return populationSize;
    }

    public void setPopulationSize(int populationSize)
    {
        this.populationSize = populationSize;
    }

    public double getCrossoverReplacementRate()
    {
        return crossoverReplacementRate;
    }

    public void setCrossoverReplacementRate(double crossoverReplacementRate)
    {
        this.crossoverReplacementRate = crossoverReplacementRate;
    }

    public double getMutationRate()
    {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate)
    {
        this.mutationRate = mutationRate;
    }
}
