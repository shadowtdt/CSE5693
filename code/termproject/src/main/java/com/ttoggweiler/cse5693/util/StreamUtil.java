package com.ttoggweiler.cse5693.util;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Util for java8 streams
 * threshold for using parallel streams
 * build in metrics support?
 */
public class StreamUtil
{
    private static boolean useParallelStreams = true;
    private static int parallelThreshold = 100;

    public static <T> Stream<T> getStream(Collection<T> collection)
    {
        return (useParallelStreams && collection.size() > parallelThreshold)
                ? collection.parallelStream()
                : collection.stream();
    }
}
