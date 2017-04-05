package com.ttoggweiler.cse5693.genetic;

import com.sun.org.apache.regexp.internal.RE;
import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.genetic.fitness.metric.ExponentialAccuracy;
import com.ttoggweiler.cse5693.genetic.fitness.metric.Fitness;
import com.ttoggweiler.cse5693.genetic.fitness.metric.FitnessMetric;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessProportional;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessSelector;
import com.ttoggweiler.cse5693.genetic.population.BitFlipMutator;
import com.ttoggweiler.cse5693.genetic.population.Mutator;
import com.ttoggweiler.cse5693.genetic.population.OffspringFactory;
import com.ttoggweiler.cse5693.rule.Classifier;
import com.ttoggweiler.cse5693.rule.Hypothesis;
import com.ttoggweiler.cse5693.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Genetic Algorithm that returns the most successful hypotheses after multiple generations
 *
 * Pseudo:
 * Init CurrentGen with Random classifiers {@link com.ttoggweiler.cse5693.rule.Classifier}
 * While StoppingCriteria !met
 *  Select (1 - replacement%) * PopulationSize and add to next Generation {@link com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessSelector}
 *  Select (replacement% * PopulationSize) / 2 pairs and add CrossOver results to next Generation {@link com.ttoggweiler.cse5693.genetic.population.OffspringFactory}
 *  Select (mutation% * PopulationSize) from next Generation and Mutate {@link com.ttoggweiler.cse5693.genetic.population.Mutator}
 *  Set CurrentGen = nextGen
 *  Compute Fitness for CurrentGen {@link com.ttoggweiler.cse5693.genetic.fitness.metric.FitnessMetric}
 */
public class GeneticSearch
{
    private FitnessMetric fitnessMetric = new ExponentialAccuracy(2);
    private FitnessSelector fitnessSelector = new FitnessProportional();
    private Mutator mutator = new BitFlipMutator();

    private int generationLimit = 1000;
    private double fitnessThreshold = 0.9;

    private int populationSize = 100;
    private double crossoverReplacementRate = 0.6;
    private double mutationRate = .001;

    private Fitness mostFitHypothesis;
    private Logger log = LoggerFactory.getLogger(GeneticSearch.class);

    public GeneticSearch(int populationSize, double crossoverReplacementRate, double mutationRate)
    {
        this.populationSize = populationSize;
        this.crossoverReplacementRate = crossoverReplacementRate;
        this.mutationRate = mutationRate;
    }

    public Hypothesis generateAndEvolveClassifier(List<Feature<? extends Comparable>>  features, List<Feature<? extends Comparable>>  targetFeatures, Collection<Map<String, ? extends Comparable>> data)
    {
        // Init Starting generation
        Collection<Hypothesis> currentGeneration = generateRandomPopulation(populationSize,features,targetFeatures);
        Collection<Fitness> currentGenerationFitness = Fitness.compute(fitnessMetric,currentGeneration,data,true);
        mostFitHypothesis = currentGenerationFitness.stream().max(Comparator.comparingDouble(Fitness::getValue)).get();

        // Evolve until stopping criteria
        int generationNumber = 0;
        long crossOverCount = Math.round(populationSize * crossoverReplacementRate);
        if(crossOverCount % 2 != 0)crossOverCount++;
        long keepCount = populationSize - crossOverCount;

        while (mostFitHypothesis.getValue() < fitnessThreshold && generationNumber++ < generationLimit)
        {
            log.info("Generation: {} Fitness: {}",generationNumber,mostFitHypothesis.getValue());
            Collection<Hypothesis> nextGeneration = fitnessSelector.selectClassifiers(keepCount,currentGenerationFitness);
            nextGeneration.addAll(OffspringFactory.singlePointCross(fitnessSelector.selectClassifiers(crossOverCount,currentGenerationFitness)));
            RandomUtil.selectRandomElements(nextGeneration,Math.round(nextGeneration.size()*mutationRate)).forEach(mutator::mutate);

            currentGeneration = nextGeneration;
            currentGenerationFitness = Fitness.compute(fitnessMetric,currentGeneration,data,true);
            mostFitHypothesis = currentGenerationFitness.stream().max(Comparator.comparingDouble(Fitness::getValue)).get();
        }

        return mostFitHypothesis.getClassifier();
    }

    private static Collection<Hypothesis> generateRandomPopulation(int populationSize, List<Feature<? extends Comparable>> features, List<Feature<? extends Comparable>>  targetFeatures)
    {
        Set<Hypothesis> generationZero = new HashSet<>();

        while(generationZero.size() != populationSize)
        {
            generationZero.add(new Hypothesis(RandomUtil.rand.nextInt(4)+1,features,targetFeatures));
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
