package com.ttoggweiler.cse5693.test;

import com.ttoggweiler.cse5693.TTTGameRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ttoggweiler on 1/13/17.
 */
public class TTTGameTestRunner{
    private static Logger log = LoggerFactory.getLogger(TTTGameRunner.class);

    // TODO: ttoggweiler 1/13/17 use logger
    public static void main(String... args)
    {
        Result result = JUnitCore.runClasses(TTTGameTests.class);

        System.out.println("===== Test Results =====");
        System.out.println("Count:" + result.getRunCount());
        System.out.println("Duration: " + result.getRunTime() + "(ms)");
        System.out.println("Failures: " + result.getFailureCount() + ", " + (result.getFailureCount() / (float)result.getRunCount())*100 + "%");

        System.out.println("\nFailure Detail");
        result.getFailures().forEach(fail->
        {
            printDetail("Header",fail.getTestHeader());
            printDetail("Description",fail.getDescription());
            printDetail("Message",fail.getMessage());
            printDetail("Exception",fail.getException());
            printDetail("Tract",fail.getTrace());
        });


    }

    public static void printDetail(String label, Object toPrint)
    {
        if(toPrint != null)
            System.out.println(label+": " + toPrint);
        else
            System.out.println(label+": null");
    }
}
