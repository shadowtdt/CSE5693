package com.ttoggweiler.cse5693.genetic.population;

import com.ttoggweiler.cse5693.rule.Hypothesis;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by ttoggweiler on 4/3/17.
 */
public class OffspringFactory
{
    public Collection<Hypothesis> generateOffspring(Collection<Hypothesis> parents)
    {
        if(parents.size() % 2 !=  0) throw new IllegalArgumentException("Unable to generate offspring with odd number of parents!");

        Iterator<Hypothesis> itr = parents.iterator();
        int ruleLength = parents.iterator().next().getNumberOfBits();
        while(itr.hasNext())
        {
            Hypothesis mom = itr.next();
            Hypothesis data = itr.next();

            //random condition start, end
            //random rule start,end (mom +dad)
            //build new conditions,rules
            //set on hypo
            //return;


        }

    }
}
