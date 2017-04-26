package com.ttoggweiler.cse5693.explore;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.ttoggweiler.cse5693.data.DataSet;
import com.ttoggweiler.cse5693.predict.Classifier;
import com.ttoggweiler.cse5693.predict.Performance;
import com.ttoggweiler.cse5693.util.PreCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ttoggweiler on 4/23/17.
 */
public class Evaluation
{

    private static Logger log = LoggerFactory.getLogger(Evaluation.class);

    public static final MetricRegistry metrics = new MetricRegistry();

    public Map<Classifier,Long> classifierTrainTimes;
    public Map<Classifier,Long> classifierPredictTimes;
    public Map<Classifier,Performance> classifierPerformanceMap;
    public Map<Classifier,Performance> classifierValidPerformanceMap;

    public Evaluation(DataSet data, DataSet valid, Classifier ... classifiers)
    {
        PreCheck.ifEmpty(NullPointerException::new,classifiers);

        log.info("== Training Times ==");
        // Training
        classifierTrainTimes = new HashMap<>(classifiers.length);
        for (Classifier classifier : classifiers) {
            classifierTrainTimes.put(classifier,trainingTime(classifier,data));
        }

        log.info("== Classify Times ==");
        classifierPredictTimes = new HashMap<>(classifiers.length);
        for (Classifier classifier : classifiers) {
            classifierPredictTimes.put(classifier,classifyTime(classifier,data));
        }

        log.info("== Performance ==");
        classifierPerformanceMap = new HashMap<>(classifiers.length);
        for (Classifier classifier : classifiers) {
            classifierPerformanceMap.put(classifier,performance(classifier,data));
        }

        log.info("== Validation Performance ==");
        classifierValidPerformanceMap = new HashMap<>(classifiers.length);
        for (Classifier classifier : classifiers) {
            classifierValidPerformanceMap.put(classifier,performance(classifier,valid));
        }


    }

    public static long trainingTime(Classifier classifier, DataSet data)
    {
        Timer.Context timer = metrics.timer(classifier.shortName()+"_TrainingTime").time();
        classifier.train(data);
        long milisec = timer.stop()/1000000;
        log.debug("{}\tTrain time: {}ms, with {}",classifier.shortName(), milisec,data.describe(false));
        return milisec/1000;
    }

    public static long classifyTime(Classifier classifier, DataSet data)
    {
        Timer.Context timer = metrics.timer(classifier.shortName()+"_ClassifyTime").time();
        data.forEach(classifier::classifyExample);
        long millisec = timer.stop()/1000000;
        log.debug("{}\tClassifyTime time: {}ms, with {}",classifier.shortName(), millisec,data.describe(false));
        return millisec/1000;
    }

    public static Performance performance(Classifier classifier, DataSet data)
    {
        Performance p = new Performance(classifier,data);
        log.debug("{}\tAccuracy: {}, with {}",classifier.shortName()
                ,p.getPerformanceString(true,false,false)
                ,data.describe(false));
        return p;
    }
}
