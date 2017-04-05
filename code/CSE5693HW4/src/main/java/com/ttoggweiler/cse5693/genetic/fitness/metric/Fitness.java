package com.ttoggweiler.cse5693.genetic.fitness.metric;

import com.ttoggweiler.cse5693.rule.Classifier;
import com.ttoggweiler.cse5693.rule.Hypothesis;
import com.ttoggweiler.cse5693.rule.Performance;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the measured fitness of a given classifier for a given data set
 */
public class Fitness
{
    private Hypothesis classifier;
    private Performance classifierPerformance;
    private FitnessMetric metric;
    private double value;

    public Fitness(FitnessMetric metric, Hypothesis classifier, Collection<Map<String, ? extends Comparable>> exampleData)
    {
        PreCheck.ifNull("Fitness metric cannot be null when creating Fitness measurement",metric);
        PreCheck.ifNull("Classifier cannot be null when creating Fitness measurement",metric);
        PreCheck.ifNull("Example data  cannot be null when creating Fitness measurement",metric);

        this.classifier = classifier;
        this.metric = metric;
        this.classifierPerformance = new Performance(classifier,exampleData);
        this.value = metric.measureFitness(classifierPerformance);
    }

    public double getValue()
    {
        return this.value;
    }

    public Hypothesis getClassifier()
    {
        return this.classifier;
    }

    public FitnessMetric getMetric()
    {
        return this.metric;
    }

    public static Collection<Fitness> compute(FitnessMetric metric, Collection<Hypothesis> classifiers, Collection<Map<String,? extends Comparable>> fitnessTestData, boolean parallel)
    {
        return parallel
                ?classifiers.stream().map(aClassifier -> new Fitness(metric,aClassifier,fitnessTestData)).collect(Collectors.toSet())
                :classifiers.stream().map(aClassifier -> new Fitness(metric,aClassifier,fitnessTestData)).collect(Collectors.toSet());

    }
}
