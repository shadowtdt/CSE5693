package com.ttoggweiler.cse5693.genetic.fitness.selection;

import com.ttoggweiler.cse5693.genetic.fitness.metric.Fitness;
import com.ttoggweiler.cse5693.rule.Classifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Selects hypotheses based on proportion of fitness to others in the population
 * Probability_Of_Selection = fitness(xi) / Sum( fitness(x0 -> xn))
 */
public class FitnessProportional extends FitnessSelector
{
    private Random rand = new Random();

    @Override
    public Collection<Classifier> selectClassifiers(int count, Collection<Fitness> classifierFitness)
    {
        if(classifierFitness.size() <= count)
            return classifierFitness.stream().map(Fitness :: getClassifier).collect(Collectors.toSet());

        //Find most fit classifier and compute probabilities
        double fitnessSum = classifierFitness.stream().mapToDouble(Fitness::getValue).sum();
        HashMap<Fitness,Double> selectionProbabilities = new HashMap<>();
        Fitness mostFit = null;
        for (Fitness fitness : classifierFitness) {
            if(mostFit == null || fitness.getValue() > mostFit.getValue())
                mostFit = fitness;
            selectionProbabilities.put(fitness, fitness.getValue() / fitnessSum);
        }

        // Always select best fit classifier
        Set<Classifier> selectedClassifiers = new HashSet<>(count);
        selectedClassifiers.add(mostFit.getClassifier());
        selectionProbabilities.remove(mostFit);

        // Select classifiers using probabilities computed above
        while(true)
        {
            for (Map.Entry<Fitness, Double> fitnessEntry : selectionProbabilities.entrySet()) {
                if(fitnessEntry.getValue() > rand.nextDouble())
                {
                    selectedClassifiers.add(fitnessEntry.getKey().getClassifier());
                    selectionProbabilities.remove(fitnessEntry.getKey());
                    if(selectedClassifiers.size() != count)
                        return selectedClassifiers;
                }
            }


        }
    }
}
