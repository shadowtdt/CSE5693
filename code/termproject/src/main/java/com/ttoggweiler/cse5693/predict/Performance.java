package com.ttoggweiler.cse5693.predict;

import com.ttoggweiler.cse5693.data.DataSet;
import com.ttoggweiler.cse5693.util.Identity;
import com.ttoggweiler.cse5693.util.PreCheck;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Performance data of a Rule/RuleSetClassifier accuracy for a given data set.
 * Such as: %correct, Classification Times, resource usage..
 */
public class Performance extends Identity
{
    private Classifier classifier;
    private double completionTime;
    private Set<Map<String, Comparable>> correctClassifications;
    private Set<Map<String, Comparable>> incorrectClassifications;


    public Performance(Classifier classifier, Collection<Map<String,Comparable>> exampleData)
    {
        this.classifier = classifier;
        measureClassificationPerformance(exampleData);
        completionTime = System.currentTimeMillis();
    }

    public Performance(Classifier classifier, DataSet exampleData)
    {
        this(classifier,exampleData.getData());
    }

    private double measureClassificationPerformance(Collection<Map<String,Comparable>> exampleData)
    {
        correctClassifications = new HashSet<>();
        incorrectClassifications = new HashSet<>();
        for (Map<String, Comparable> example : exampleData) {
//            Comparable classification = example.get(classifier.getTargetFeatures().name());
//            if(classification == null)
//                throw new NullPointerException("Unable to measure performance of classifier. Example data is missing target feature: "+classifier.name());
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
        return "(+" + correctClassifications.size() + "|-"+incorrectClassifications.size()
                +")/"+(correctClassifications.size()+incorrectClassifications.size());
    }

    public double getDuration()
    {
        return completionTime - creationMili();
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

    /* Field methods */
    public Set<Map<String,Comparable>> getCorrectClassifications()
    {
        return correctClassifications;
    }

    public void setCorrectClassifications(Set<Map<String,Comparable>> correctClassifications)
    {
        this.correctClassifications = correctClassifications;
    }

    public Set<Map<String, Comparable>> getIncorrectClassifications()
    {
        return incorrectClassifications;
    }

    public void setIncorrectClassifications(Set<Map<String,Comparable>> incorrectClassifications)
    {
        this.incorrectClassifications = incorrectClassifications;
    }

}
