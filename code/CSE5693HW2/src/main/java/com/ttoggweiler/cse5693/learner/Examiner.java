package com.ttoggweiler.cse5693.learner;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
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
    static final MetricRegistry metrics = new MetricRegistry();
    static private final Timer classificationTimes = metrics.timer(MetricRegistry.name(Examiner.class, "classification","time"));


    public static Double getAccuracy(Feature target , FeatureNode root, List<Map<String, Comparable>> datas)
    {
        Integer correct = 0;
        for (Map<String, Comparable> data : datas) {
            Timer.Context time = classificationTimes.time();
            FeatureNode classificationResult = root.getClassificationLeaf(data);
            time.stop();
            if(data.get(target.getName()).compareTo(classificationResult.getMostCommonValue()) == 0)correct++;
            else log.warn("Incorrect classification: {} for data: {} {}",classificationResult,data.toString(),classificationResult.toPathString());
        }
        log.info("Classification mean time: {}",classificationTimes.getSnapshot().getMean()/1000000);
        return correct>0? (double)correct/(double) datas.size() : 0d;
    }
}
