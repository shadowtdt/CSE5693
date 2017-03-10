package com.ttoggweiler.cse5693.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Static util methods that check arguments validity
 * // TODO: ttoggweiler 2/2/17 tests
 */
public class PreCheck
{
    /* Object */

    /**
     * Returns the default value if the item is null
     * @param item item to check if it is null
     * @param defaultItem the defult object to use if {@param item} is null
     * @param <T> the class of the object
     * @return {@param default} if {@param item} is null, {@param item} otherwise
     */
    public static <T> T defaultTo(T item, T defaultItem)
    {
        return isNull(item) ? defaultItem : item;
    }

    /**
     * Checks if any of the items in the array are null
     * @param items items to check for nulls
     * @return True if array contains null, false otherwise
     */
    public static boolean isNull(Object... items)
    {
        if (items == null) return true;
        for (Object item : items) if (item == null) return true;
        return false;
    }

    /**
     * Checks if any of the items in the array are null
     * @param items items to check for nulls
     * @return False if array contains null, true otherwise
     */
    public static boolean notNull(Object... items)
    {
        return !isNull(items);
    }

    /**
     * Checks if any of the items in the array are null
     * @param items items to check for nulls
     * @throws null pointer exception with provided message if any item is null
     */
    public static void ifNull(String error, Object... items) throws NullPointerException
    {
        ifNull(() -> new NullPointerException(error), items);
    }

    /**
     * Checks if all objects != null, throws provided exception otherwise otherwise.
     * @param exception exception to throw if any of the objects are null
     * @param objects object to check
     * @throws IllegalArgumentException when collection is null or empty, thrown with provided message
     */
    public static <T extends Throwable> void ifNull(Supplier<T> exception, Object... objects) throws T
    {
        if (isNull(objects)) throw exception.get();
    }

    /* String */

    /**
     * Checks if string has valid characters
     * @param strings object to check
     * @return true if == null || Empty
     */
    public static boolean isEmpty(String... strings)
    {
        if (isNull(strings)) return true;
        for (String str : strings) if (str.trim().isEmpty()) return true;
        return false;
    }

    /**
     * Checks if string has valid characters
     * @param strings object to check
     * @return true if  != null && !Empty
     */
    public static boolean notEmpty(String... strings)
    {
        return !isEmpty(strings);
    }

    /**
     * Checks if string is != nul && !Empty, IllegalArgument otherwise.
     * @param strings object to check
     * @throws IllegalArgumentException when string is null or empty, thrown with provided message
     */
    public static <T extends Throwable> void ifEmpty(Supplier<T> exception, String... strings) throws T
    {
        if (isEmpty(strings)) throw exception.get();
    }

    /**
     * checks not empty and trims any whitespace,
     * throws illegal argument otherwise.
     * @param errorMessage error message too throw if not valid.
     * @param string string to check
     * @return trimmed string
     * @throws IllegalArgumentException when string is null or empty
     */
    public static String reduceElse(String errorMessage, String string) throws IllegalArgumentException
    {
        string = reduceElse(() -> new IllegalArgumentException(errorMessage), string);
        return reduceElse(() -> new IllegalArgumentException(errorMessage), string);
    }

    /**
     * checks not empty and trims any whitespace,
     * throws illegal argument otherwise.
     * @param exception throwable to throw when string is null or empty
     * @param string string to check
     * @return trimmed string
     * @throws IllegalArgumentException when string is null or empty
     */
    public static <T extends Throwable> String reduceElse(Supplier<T> exception, String string) throws T
    {
        if (isEmpty(string)) throw exception.get();
        else return string.trim();
    }

    /* Array */

    /**
     * Checks if the provided array is null or empty
     * @param array object to check
     * @return true if == null || Empty
     */
    public static <T> boolean isEmpty(T[] array)
    {
        if (array == null || array.length == 0) return true;
        return false;
    }

    /**
     * Checks if the provided array is null or empty
     * @param array  object to check
     * @return true if  != null && !Empty
     */
    public static <T> boolean notEmpty(T[] array)
    {
        return !isEmpty(array);
    }

    /**
     * Checks if object is != nul && !Empty, IllegalArgument otherwise.
     * @param array object to check
     * @throws IllegalArgumentException when array is null or empty, thrown with provided message
     */
    public static <X,T extends Throwable> void ifEmpty(Supplier<T> exception, X[] array) throws T
    {
        if (isEmpty(array)) throw exception.get();
    }

    public static <T> boolean anyMatch(T[] array, T... items)
    {
        if(isEmpty(items) || isEmpty(array))return false;

        for (T value : array)
            for (T item : items)
                if (item.equals(value)) return true;
        return false;
    }

    public static <T> boolean contains(T toMatch, T... items)
    {
        if(isEmpty(items) || isNull(toMatch))return false;
        for (T item : items)
            if (item.equals(toMatch)) return true;
        return false;
    }

    /* Collection */

    /**
     * Checks if the provided collection is null or empty
     * @param collections object to check
     * @return true if == null || Empty
     */
    public static boolean isEmpty(Collection... collections)
    {
        if (collections == null || collections.length == 0) return true;
        for (Collection c : collections) if (c == null || c.isEmpty()) return true;
        return false;
    }

    /**
     * Checks if the provided collection is null or empty
     * @param collections  object to check
     * @return true if  != null && !Empty
     */
    public static boolean notEmpty(Collection... collections)
    {
        return !isEmpty(collections);
    }

    /**
     * Checks if object is != nul && !Empty, IllegalArgument otherwise.
     * @param collections object to check
     * @throws IllegalArgumentException when collection is null or empty, thrown with provided message
     */
    public static <T extends Throwable> void ifEmpty(Supplier<T> exception, Collection... collections) throws T
    {
        if (isEmpty(collections)) throw exception.get();
    }

    /* Map */

    /**
     * Checks if the provided collection is null or empty
     * @param maps object to check
     * @return true if == null || Empty
     */
    public static boolean isEmpty(Map... maps)
    {
        if (maps == null || maps.length == 0) return true;
        for (Map m : maps) if (m == null || m.size() == 0 ||m.isEmpty()) return true;
        return false;
    }

    /**
     * Checks if the provided collection is null or empty
     * @param maps object to check
     * @return true if  != null && !Empty
     */
    public static boolean notEmpty(Map... maps)
    {
        return !isEmpty(maps);
    }

    /**
     * Checks if object is != nul && !Empty, IllegalArgument otherwise.
     * @param maps object to check
     * @throws IllegalArgumentException when map is null or empty, thrown with provided message
     */
    public static <T extends Throwable> void ifEmpty(Supplier<T> exception, Map... maps) throws T
    {
        if (isEmpty(maps)) throw exception.get();
    }

    /**
     * Transforms a throwable to list of strings
     * @param throwable trace to transform
     * @return list of strings representing the trace
     */
    public static List<String> traceToList(Throwable throwable)
    {
        ifNull(() -> new NullPointerException("Null Throwable found during list conversion"), throwable);

        // Build trace
        List<String> stackStrs = new ArrayList<>();
        stackStrs.add("Cause: " + throwable.toString());
        stackStrs.addAll(
                Arrays.stream(throwable.getStackTrace())
                        .map(Object::toString)
                        .collect(Collectors.toList()));

        // If caused by another throwable, recurse, remove duplicates and add to current list
        if (throwable.getCause() != null) {
            List<String> cause = traceToList(throwable.getCause());
            cause.removeAll(stackStrs);
            stackStrs.addAll(cause);
        }
        return stackStrs;
    }

    /* Optional */
    public static boolean isPresent(Optional... optionals)
    {
        for (Optional optional : optionals) if (!optional.isPresent()) return false;
        return true;
    }

    public static boolean notPresent(Optional... optionals)
    {
        return !isPresent(optionals);
    }

    public static <T extends Throwable> void ifNotPresent(Supplier<T> exception, Optional... optionals) throws T
    {
        if (!isPresent(optionals)) throw exception.get();
    }

    public static void main(String[] args)
    {
        Set<String> s = new HashSet<>();
        s.add(null);
        String a = " a  ";
        String b = "  b ";
        String c = null;//"  c ";

        PreCheck.ifNull(c, a, b);

        System.out.println(a);
        System.out.println(b);
        PreCheck.reduceElse("", a);
        System.out.println(a);
        System.out.println(b);


    }
}


