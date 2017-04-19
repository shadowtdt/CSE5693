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
        commpletionTime = System.currentTimeMillis();
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
        return getAccuracy();
    }

    public double getAccuracy()
    {
        if(PreCheck.isEmpty(correctClassifications))return 0;
        else return correctClassifications.size()/ (double)(correctClassifications.size()+incorrectClassifications.size());
    }

    public String getAccuracyString()
    {
        return getAccuracy()*100 + "%";
    }

    public String getExampleCounts()
    {
        return "+" + correctClassifications.size() + "|-"+incorrectClassifications.size()
                +"/"+(correctClassifications.size()+incorrectClassifications.size());
    }

    public double getDuration()
    {
        return commpletionTime-getCreationTime();
    }

    public String getDurationString()
    {
        return getDuration()+"ms";
    }

    public String getPerformanceString(boolean includeCounts, boolean includeTime, boolean includeClassifier)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getAccuracyString());
        if (includeCounts) {
            sb.append(" ");
            sb.append(getExampleCounts());
        }
        if (includeTime) {
            sb.append(" ");
            sb.append(getDurationString());
        }
        if(includeClassifier) {
            sb.append(" ");
            sb.append(classifier.getClassifierString(false));
        }
        return sb.toString();
    }

}
