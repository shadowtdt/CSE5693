package com.ttoggweiler.cse5693.appraiser;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ttoggweiler on 1/27/17.
 */
public abstract class AggregateAppraiser<T> extends BaseAppraiser<T>
{
    private Set<BaseAppraiser<T>> subAppraisers = new HashSet<>();

    public Set<BaseAppraiser<T>> getSubAppraisers()
    {
        return subAppraisers;
    }

    public void removeAppraiser(BaseAppraiser subAppraiser)
    {
        subAppraisers.remove(subAppraiser);
    }

    public void addSubAppraiser(BaseAppraiser<T> subAppraiser)
    {
        if (subAppraiser != null) subAppraisers.add(subAppraiser);
    }

    public void initilizeAllWeights(float initWeight)
    {
        this.setWeight(initWeight);
        subAppraisers.forEach(appr -> appr.setWeight(initWeight));
    }
}