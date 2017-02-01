package com.ttoggweiler.cse5693.appraiser;

/**
 * Class that all appraisers will extend
 * function is to take some input and assign a value
 * Value assignment is left to the implementors
 */
public abstract class BaseAppraiser<T>
{

    private Float weight;

    public abstract Float appraise(T input);

    public Float getWeight()
    {
        return weight;
    }

    public void setWeight(float weight)
    {
        this.weight = weight;
    }
}
