package com.ttoggweiler.cse5693.predict;

/**
 * Created by ttoggweiler on 2/15/17.
 */
public class Examiner
{
//    private static Logger log = LoggerFactory.getLogger(Examiner.class);
//    static final MetricRegistry metrics = new MetricRegistry();
//
//
//    public static Double getAccuracy(Feature target, FeatureNode root, List<Map<String, Comparable>> datas)
//    {
//        Timer classificationTimes = metrics.timer(MetricRegistry.name(Examiner.class, "classification", "time","tree"));
//        Integer correct = 0;
//        for (Map<String, Comparable> data : datas) {
//            Timer.Context time = classificationTimes.time();
//            FeatureNode classificationResult = root.getClassificationLeaf(data);
//            time.stop();
//            if (data.get(target.getName()).compareTo(classificationResult.getMostCommonValue()) == 0) correct++;
//            else
//                log.debug("Incorrect classification (TREE):\n{} for example: {} \nFailed Path: {}", classificationResult.getMostCommonValue(), data.toString(), classificationResult.toPathTree());
//        }
//        log.debug("Avg classification time (TREE): {}", classificationTimes.getSnapshot().getMean() / 1000000);
//        return correct > 0 ? (double) correct / (double) datas.size() : 0d;
//    }
//
//    public static Double getAccuracy(Feature target, List<Rule> rules, List<Map<String, Comparable>> datas)
//    {
//        Timer classificationTimes = metrics.timer(MetricRegistry.name(Examiner.class, "classification", "time","rule"));
//        Integer correct = 0;
//        for (Map<String, Comparable> data : datas) {
//            Timer.Context time = classificationTimes.time();
//            Optional<Rule> oRule = rules.stream().filter(r -> r.test(data)).findAny();
//            time.stop();
//
//            if (oRule.isPresent()) {
//                FeatureNode classificationResult = oRule.get().getClassificationLeaf();
//                if (data.get(target.getName()).compareTo(classificationResult.getMostCommonValue()) == 0) correct++;
//                else
//                    log.debug("Incorrect classification (RULE):\n{} for example: {} \nFailed Rule: {}", classificationResult.getMostCommonValue(), data.toString(), classificationResult.toPathString(true));
//            } else continue;
//        }
//        log.debug("Avg classification time (Rule): {}", classificationTimes.getSnapshot().getMean() / 1000000);
//        return correct > 0 ? (double) correct / (double) datas.size() : 0d;
//    }
}
