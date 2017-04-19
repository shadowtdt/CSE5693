package com.ttoggweiler.cse5693.genetic.fitness.selection;

import com.ttoggweiler.cse5693.genetic.fitness.metric.Fitness;
import com.ttoggweiler.cse5693.rule.Hypothesis;
import com.ttoggweiler.cse5693.util.RandomUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ttoggweiler on 4/3/17.
 */
public class RankProportional implements FitnessSelector
{
    @Override
    public Collection<Hypothesis> selectClassifiers(long count, Collection<Fitness> classifierFitness)
    {
        List<Fitness> ranked = classifierFitness.stream().sorted(Comparator.comparingDouble(Fitness::getValue)).collect(Collectors.toList());

        Set<Hypothesis> selectedClassifiers = new HashSet<>();

        while(true)
        {
            for (int i =classifierFitness.size()-1; i >= 0; i--) {
                Fitness h = ranked.get(i);
                if(selectedClassifiers.contains(h.getClassifier()))continue;
                if(RandomUtil.probability( ((i+1) / (double)classifierFitness.size())))
                {
                    selectedClassifiers.add(h.getClassifier());
                    if(selectedClassifiers.size() >= count)
                        return selectedClassifiers;
                }
            }

        }
    }
}
