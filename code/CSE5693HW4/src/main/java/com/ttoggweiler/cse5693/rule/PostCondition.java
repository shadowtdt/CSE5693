//package com.ttoggweiler.cse5693.rule;
//
//import com.ttoggweiler.cse5693.feature.Feature;
//import com.ttoggweiler.cse5693.util.PreCheck;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.function.Predicate;
//
///**
// * Represents the post-condition for a classifier
// */
//public class PostCondition extends Condition
//{
//
//    public PostCondition(Feature feature)
//    {
//        this.setConditionFeature(feature);
//        // If feature has no known values, assume continuous feature
//        if(PreCheck.isEmpty(feature.getValues()))
//        {
//            throw new IllegalArgumentException("Continuous valued post-conditions for rule based classifiers are not supported at this time.");
//        }else{
//            for (Object value : feature.getValues()) {
//                Predicate valuePredicate = feature.getPredicateForValue((Comparable) value);
//                if(rand.nextDouble() > 0.5) {
//                    getConditionPredicates().add(valuePredicate);
//                    getValues().add((Comparable) value);
//                }
//                else
//                    getConditionPredicates().add(valuePredicate.negate());
//            }
//        }
//    }
//
//}
