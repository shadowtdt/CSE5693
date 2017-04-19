package com.ttoggweiler.cse5693.genetic.fitness.selection;

import com.ttoggweiler.cse5693.genetic.fitness.metric.Fitness;
import com.ttoggweiler.cse5693.rule.Hypothesis;
import com.ttoggweiler.cse5693.util.RandomUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ttoggweiler on 4/3/17.
 */
public class FitnessTournament implements FitnessSelector
{
    @Override
    public Collection<Hypothesis> selectClassifiers(long count, Collection<Fitness> classifierFitness)
    {
        Set<Hypothesis> selectedClassifiers = new HashSet<>();

        while (true) {
            Fitness h1 = RandomUtil.selectRandomElement(classifierFitness);
            Fitness h2 = RandomUtil.selectRandomElement(classifierFitness);
            selectedClassifiers.add(h1.getValue() > h2.getValue() ? h1.getClassifier() : h2.getClassifier());
            if (selectedClassifiers.size() >= count)
                return selectedClassifiers;


        }
    }
}
