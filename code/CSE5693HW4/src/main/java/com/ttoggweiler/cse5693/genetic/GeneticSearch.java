package com.ttoggweiler.cse5693.genetic;

import com.ttoggweiler.cse5693.feature.Feature;
import com.ttoggweiler.cse5693.genetic.fitness.metric.ExponentialAccuracy;
import com.ttoggweiler.cse5693.genetic.fitness.metric.Fitness;
import com.ttoggweiler.cse5693.genetic.fitness.metric.FitnessMetric;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessProportional;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessSelector;
import com.ttoggweiler.cse5693.genetic.fitness.selection.FitnessTournament;
import com.ttoggweiler.cse5693.genetic.fitness.selection.RankProportional;
import com.ttoggweiler.cse5693.genetic.population.BitFlipMutator;
import com.ttoggweiler.cse5693.genetic.population.Mutator;
import com.ttoggweiler.cse5693.genetic.population.OffspringFactory;
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
    private FitnessSelector fitnessSelector = new RankProportional();
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
        log.info("\n\nSelection: {}, MutationRate: {}, ReplacementRate: {} MaxGeneration {}, FitnessThreshold: {}",
                fitnessSelector.getClass().getSimpleName(),getMutationRate(),getCrossoverReplacementRate(),getGenerationLimit(),getFitnessThreshold());
        // Init Starting generation
        // Init Starting generation
        Collection<Hypothesis> currentGeneration = generateRandomPopulation(populationSize,features,targetFeatures);
        Collection<Fitness> currentGenerationFitness = Fitness.compute(fitnessMetric,currentGeneration,data,true);
        mostFitHypothesis = currentGenerationFitness.stream().max(Comparator.comparingDouble(Fitness::getValue)).get();

        // Evolve until stopping criteria
        int generationNumber = 0;
        long crossOverCount = Math.round(populationSize * crossoverReplacementRate);
        if(crossOverCount % 2 != 0)crossOverCount++;
        if(crossOverCount < 2)crossOverCount = 2;
        long keepCount = populationSize - crossOverCount;
        long mutationCount = Math.round(populationSize*mutationRate);
        if(mutationCount < 1) mutationCount = 1;



        while (mostFitHypothesis.getValue() < fitnessThreshold && generationNumber < generationLimit)
        {
            if(generationNumber++%100 == 0) {
                log.info("Gen:{} Fit:{} Best{}%",generationNumber,mostFitHypothesis.getValue(),mostFitHypothesis.getPerformance().getAccuracy()*100);
                log.info("Fitness    {}", currentGenerationFitness.stream().mapToDouble(Fitness::getValue).summaryStatistics());
                log.info("Accuracy   {}", currentGenerationFitness.stream().mapToDouble(f -> f.getPerformance().getAccuracy()).summaryStatistics());
                log.info("Rule Count {}", currentGeneration.stream().mapToDouble(g -> g.getRuleList().size()).summaryStatistics());
                //currentGenerationFitness.parallelStream().forEach(h -> log.debug("\n{}",h.getFitnessString(true,false)));}
            }

            Collection<Hypothesis> nextGeneration = new HashSet<>(populationSize);
            nextGeneration.add(mostFitHypothesis.getClassifier());

            //Remove the really bad Hypotheses
            //currentGenerationFitness.removeIf(f -> f.getValue() < 0.001);
            //int removed = populationSize-currentGenerationFitness.size();
            //log.warn("Removed {} ({})% poor hypotheses",removed, (removed/(double)populationSize)*100);

            // Select and populate the next generation
            nextGeneration.addAll(fitnessSelector.selectClassifiers(keepCount,currentGenerationFitness));
            nextGeneration.addAll(OffspringFactory.randomPointConditionCross(fitnessSelector.selectClassifiers(crossOverCount,currentGenerationFitness)));
            RandomUtil.selectRandomElements(nextGeneration,mutationCount).parallelStream().forEach(mutator::mutate);

//            // Fill population to capacity after purging poor
//            int populationDeficit = populationSize - nextGeneration.size();
//            if(populationDeficit > 0)
//                nextGeneration.addAll(generateRandomPopulation(populationDeficit,features,targetFeatures));

            // Measure the new generation
            currentGeneration = nextGeneration;
            currentGenerationFitness = Fitness.compute(fitnessMetric,currentGeneration,data,true);
            Fitness generationsBest = currentGenerationFitness.stream().max(Comparator.comparingDouble(Fitness::getValue)).get();
            if(generationsBest.getValue() > mostFitHypothesis.getValue())mostFitHypothesis = generationsBest;
        }

        return mostFitHypothesis.getClassifier();
    }

    private static Collection<Hypothesis> generateRandomPopulation(int populationSize, List<Feature<? extends Comparable>> features, List<Feature<? extends Comparable>>  targetFeatures)
    {
        Set<Hypothesis> generationZero = new HashSet<>();

        while(generationZero.size() != populationSize)
        {
            generationZero.add(new Hypothesis(2,features,targetFeatures));
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
