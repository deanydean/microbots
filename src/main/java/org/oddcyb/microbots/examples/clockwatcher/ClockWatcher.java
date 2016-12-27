/*
 * Copyright 2016 Matt Dean
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oddcyb.microbots.examples.clockwatcher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.oddcyb.microbots.RobotFactory;

/**
 * Example of creating a robot that watches the clock tick.
 */
public class ClockWatcher 
{
    
    /**
     * Display the usage message for this class.
     */
    public static void usage()
    {
        System.out.println("usage: ClockWatcher <tick-interval>");
    }
    
    public static void main(String[] args)
    {
        if ( args.length < 1 )
        {
            usage();
            return;
        }
        
        ScheduledExecutorService scheduler = 
            Executors.newSingleThreadScheduledExecutor();
        int tick = Integer.parseInt(args[0]);
        
        // Log the time on each tick
        RobotFactory.reactor("tick", (s) -> System.out.println(s) );
        
        // Create a watcher robot that can trigger an event on each tick
        RobotFactory.watcher( (cb) -> {
            scheduler.scheduleAtFixedRate(
                () -> cb.accept("tick"), tick, tick, TimeUnit.SECONDS);
        } );
        
        // Wait for ~5 ticks and then shutdown the scheduler and the factory
        CountDownLatch countDown = new CountDownLatch(5);
        RobotFactory.reactor("tick", (s) -> countDown.countDown());
        RobotFactory.activate( ()-> { countDown.await(); } )
            .activity().thenRun( () -> {
                scheduler.shutdown();
                RobotFactory.shutdown();
            });
    }
    
}
