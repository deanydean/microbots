package org.oddcyb.microbots.core.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RobotThreadFactory
 */
public class RobotThreadFactory implements ThreadFactory
{
    private final AtomicInteger tCount = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable runnable)
    {
        var thread = new Thread(runnable, 
                                "Robot-thread-"+tCount.incrementAndGet());
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new RobotUncaughtExceptionHandler());
        return thread;
    }
   
}