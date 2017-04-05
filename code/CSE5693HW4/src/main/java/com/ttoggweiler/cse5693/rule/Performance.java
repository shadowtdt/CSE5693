package com.ttoggweiler.cse5693.rule;

import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Performance data of a Rule/Hypothesis accuracy for a given data set.
 * Such as: %correct, Classification Times, resource usuage..
 */
public class Performance extends Identity
{
    private Classifier classifier;
    private double commpletionTime;
    private Set<Map<String,? extends Comparable>> correctClassifications;
    private Set<Map<String,? extends Comparable>> incorrectClassifications;

    public Performance(Classifier classifier, Collection<Map<String, ? extends Comparable>> exampleData)
    {
        this.classifier = classifier;
        measureClassificationPerformance(exampleData);
    }

    private double measureClassificationPerformance(Collection<Map<String, ? extends Comparable>> exampleData)
    {
        correctClassifications = new HashSet<>();
        incorrectClassifications = new HashSet<>();
        for (Map<String, ? extends Comparable> example : exampleData) {
//            Comparable classification = example.get(classifier.getTargetFeatures().getName());
//            if(classification == null)
//                throw new NullPointerException("Unable to measure performance of classifier. Example data is missing target feature: "+classifier.getName());
//            Comparable prediction = this.classifier.classifyExample(example);
//            if(prediction.compareTo(classification) == 0)

            if(classifier.isCorrectClassification(example))
                correctClassifications.add(example);
            else
                incorrectClassifications.add(example);
        }
        commpletionTime = System.currentTimeMillis();
        return getAccuracy();
    }

    public double getAccuracy()
    {
        if(PreCheck.isEmpty(correctClassifications))return 0;
        else return correctClassifications.size()/ (double)(correctClassifications.size()+incorrectClassifications.size());
    }

}
