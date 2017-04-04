//package com.ttoggweiler.cse5693.rule;
//
//import com.ttoggweiler.cse5693.feature.Feature;
//import com.ttoggweiler.cse5693.util.PreCheck;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.function.Predicate;
//
///**
// * Represents a precondition of a rule
// * For every feature of the learning task, there will be an associated precondition
// * Rules are a set of preconditions that are conjunctivley joined
// */
//public class PreCondition extends Condition
//{
//    private Random rand = new Random();
//
//    public PreCondition(Feature feature)
//    {
//        this.setConditionFeature(feature);
//        // If feature has no known values, assume continuous feature
//        if(PreCheck.isEmpty(feature.getValues()))
//        {
//            // Generate a random number for the single predicate that will represent this precondition
//            Double randomDouble = rand.nextGaussian() * 100;
//            getConditionPredicates().add((map)-> {
//                Comparable value = map.get(feature.getName());
//                return (value.compareTo(randomDouble) > 0);
//            });
//        }else{
//            for (Object value : feature.getValues()) {
//                Predicate valuePredicate = feature.getPredicateForValue((Comparable) value);
//                if(rand.nextDouble() > 0.5)
//                    getConditionPredicates().add(valuePredicate);
//                else
//                    getConditionPredicates().add(valuePredicate.negate());
//            }
//        }
//    }
//
//}
