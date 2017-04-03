package util;

import com.ttoggweiler.cse5693.feature.Parser;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by ttoggweiler on 2/16/17.
 */
public class ParserTest
{
    @Test
    public void toBoolean() throws Exception
    {
        String trueStrs = "ONE , One,one,1,T,t,TRUE,True,true,YES,Yes,yes              ";
        String falseStrs = "ZERO , Zero,zero,0,F,f,FALSE,False,false,NO,No,no           ";
        String failureStrs = "Bool, Boolean , \"\" ,, 1.1  , 0.0       ";

        assertFalse("Boolean parse null should not be present", Parser.toBoolean(null).isPresent());

        for (String str : trueStrs.split(",")) {
            Optional<Boolean> parsedBoolean = Parser.toBoolean(str);
            assertTrue("Boolean parse \" " + str + " \" should be present",parsedBoolean.isPresent());
            assertTrue("Boolean parse \" " + str + " \" should return True",parsedBoolean.get());
        }

        for (String str : falseStrs.split(",")) {
            Optional<Boolean> parsedBoolean = Parser.toBoolean(str);
            assertTrue("Boolean parse \" " + str + " \" should be present",parsedBoolean.isPresent());
            assertFalse("Boolean parse \" " + str + " \" should return False",parsedBoolean.get());
        }

        for (String str : failureStrs.split(",")) {
            Optional<Boolean> parsedBoolean = Parser.toBoolean(str);
            assertFalse("Boolean parse \" " + str + " \" should not be present",parsedBoolean.isPresent());
        }

    }

    @Test
    public void toFloat() throws Exception
    {
        Map<String, Float> successStrings = new HashMap<>();
        successStrings.put("1",1f);
        successStrings.put("1.1",1.1f);
        successStrings.put("0.234",0.234f);
        successStrings.put("0000000.0000001",00000000.0000001f);
        successStrings.put("0000000.00000000",0f);
        successStrings.put("00000001.00000000",1f);
        successStrings.put("000132412341.1234120132134000134",000132412341.1234120132134000134f);

        Map<String, Float> failureStrings = new HashMap<>();
        failureStrings.put("1.1",1f);
        failureStrings.put("0.234",0.2341f);
        failureStrings.put("0000000.0000001",00000000.00000001f);
        failureStrings.put("0000000.000000000000000000000000000000000001",0f);
        failureStrings.put("1.0000001",1f);


        assertFalse("Float parse null should not be present", Parser.toFloat(null).isPresent());
        assertFalse("Float parse \"\" should not be present", Parser.toFloat("").isPresent());
        assertFalse("Float parse \" \" should not be present", Parser.toFloat(" ").isPresent());

        successStrings.forEach((k,v) ->
        {
            Optional<Float> parsedFloat = Parser.toFloat(k);
            assertTrue("Float parse \" " + k + " \" should be present",parsedFloat.isPresent());
            assertTrue("Float parse \" " + k + " \" should equal " + v,parsedFloat.get().equals(v));
        });

        failureStrings.forEach((k,v) ->
        {
            Optional<Float> parsedFloat = Parser.toFloat(k);
            assertTrue("Float parse \" " + k + " \" should be present",parsedFloat.isPresent());
            assertFalse("Float parse \" " + k + " \" should not equal " + v,parsedFloat.get().equals(v));
        });

//        for (String str : validStrs.split(",")) {
//
//        }
//
//        for (String str : failureStrs.split(",")) {
//            Optional<Float> parsedFloat = Parser.toFloat(str);
//            assertFalse("Float parse \" " + str + " \" should not be present",parsedFloat.isPresent());
//        }
    }

    @Test
    public void toDoubleT1() throws Exception
    {

    }

    @Test
    public void toIntegerT1() throws Exception
    {

    }

    @Test
    public void toLongT1() throws Exception
    {

    }

}