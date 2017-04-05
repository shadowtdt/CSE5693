package com.ttoggweiler.cse5693.genetic.population;

import com.ttoggweiler.cse5693.rule.Hypothesis;
import com.ttoggweiler.cse5693.rule.Rule;
import com.ttoggweiler.cse5693.util.RandomUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ttoggweiler on 4/3/17.
 */
public class OffspringFactory
{
    public static Collection<Hypothesis> singlePointCross(Collection<Hypothesis> parents)
    {
        if(parents.size() % 2 !=  0) throw new IllegalArgumentException("Unable to generate offspring with odd number of parents!");

        Collection<Hypothesis> newParents = new HashSet<>(parents);
        Iterator<Hypothesis> itr = newParents.iterator();
        int ruleLength = newParents.iterator().next().getNumberOfBits();
        while(itr.hasNext())
        {
            Hypothesis mom = itr.next();
            Hypothesis dad = itr.next();

            int momSplitIndex = RandomUtil.rand.nextInt(mom.getRuleList().size())+1;
            int dadSplitIndex = RandomUtil.rand.nextInt(dad.getRuleList().size())+1;

            List<Rule> newMom = new ArrayList<>();
            List<Rule> newDad = new ArrayList<>();
            newMom.addAll(mom.getRuleList().subList(0,momSplitIndex));
            newDad.addAll(dad.getRuleList().subList(0,dadSplitIndex));

            if(dad.getRuleList().size() > dadSplitIndex+1)
                newMom.addAll(dad.getRuleList().subList(dadSplitIndex+1,dad.getRuleList().size()));
            if(mom.getRuleList().size() > momSplitIndex+1)
                newDad.addAll(mom.getRuleList().subList(momSplitIndex+1,mom.getRuleList().size()));

            mom.setRules(newMom);
            dad.setRules(newDad);


            //random condition start, end
            //random rule start,end (mom +dad)
            //build new conditions,rules
            //set on hypo
            //return;


        }
        return newParents;

    }
}
