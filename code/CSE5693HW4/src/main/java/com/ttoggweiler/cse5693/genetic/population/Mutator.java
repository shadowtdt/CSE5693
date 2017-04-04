package com.ttoggweiler.cse5693.genetic.population;

import com.ttoggweiler.cse5693.rule.Hypothesis;

/**
 * Base Mutator that will be extended by mutator implementations
 */
public interface Mutator
{
    Hypothesis mutate(Hypothesis hypothesis);
}
