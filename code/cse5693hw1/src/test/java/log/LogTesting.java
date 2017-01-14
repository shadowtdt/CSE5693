package log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class to test logging
 * This project uses SLFJ with log4j2 as a driver
 */
public class LogTesting
{
    public static void main(String ... args)
    {
        System.out.println("LogTesting in progress...");
        Logger log = LoggerFactory.getLogger(LogTesting.class);
        log.debug("Hello Logger!");
    }
}
