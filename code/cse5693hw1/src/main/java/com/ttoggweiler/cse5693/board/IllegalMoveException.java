package com.ttoggweiler.cse5693.board;

/**
 * Created by ttoggweiler on 1/14/17.
 */
public class IllegalMoveException extends Exception
{
    /**
     * Creates a new empty exception.
     */
    public IllegalMoveException()
    {
        super();
    }

    /**
     * Constructor called when just an Exception is passed in.
     * @param e Exception passed in
     */
    public IllegalMoveException (Exception e)
    {
        super (e);
    }

    /**
     * Constructor called when just a String message is passed in.
     * @param msg exception message.
     */
    public IllegalMoveException(String msg)
    {
        super(msg);
    }

    /**
     *Constructor called when a String message and Throwable are passed in.
     * @param msg String message passed in
     * @param t Throwable object passed in
     */
    public IllegalMoveException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
