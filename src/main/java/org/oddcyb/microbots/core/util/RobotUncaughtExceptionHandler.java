package org.oddcyb.microbots.core.util;

/**
 * Reports uncaught exceptions in robot threads.
 */
public class RobotUncaughtExceptionHandler 
        implements Thread.UncaughtExceptionHandler
{
    
    @Override
    public void uncaughtException(Thread thread, Throwable exception)
    {
        System.err.println(
            "RobotError - UncaughtException [thread="+thread.getName()+"]");
        System.err.println(
            "Exception: "+exception.getMessage());
        exception.printStackTrace();
    }

}