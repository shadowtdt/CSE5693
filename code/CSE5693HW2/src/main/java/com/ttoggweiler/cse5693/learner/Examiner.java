package com.ttoggweiler.cse5693.learner;

import com.ttoggweiler.cse5693.tree.Feature;
import com.ttoggweiler.cse5693.tree.FeatureNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by ttoggweiler on 2/15/17.
 */
public class Examiner
{
    private static Logger log = LoggerFactory.getLogger(Examiner.class);

    public static Double getAccuracy(Feature target , FeatureNode root, List<Map<String, Comparable>> datas)
    {
        Integer correct = 0;
        for (Map<String, Comparable> data : datas) {
            Comparable classificationResult = root.getClassification(data);
            if(data.get(target.getName()).compareTo(classificationResult) == 0)correct++;
            else log.warn("Incorrect classification: {} for data: {}",classificationResult,data.toString());
        }
        return correct>0? (double)correct/(double) datas.size() : 0d;
    }
}
