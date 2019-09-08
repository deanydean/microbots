/*
 * Copyright 2016, 2019 Matt Dean
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
package org.oddcyb.microbots.examples;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.oddcyb.microbots.ActiveRobot;
import org.oddcyb.microbots.Robots;

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

        // Create a scheduler and set the tick interval
        ScheduledExecutorService scheduler = 
            Executors.newSingleThreadScheduledExecutor();
        int tick = Integer.parseInt(args[0]);

        // Create a robot that will log the time on each tick
        Robots.newReactor("tick", (s) -> System.out.println(s) );
        
        // Create a robot that will trigger an event on each tick
        Robots.newWatcher( "tick", (cb) -> {                  
            scheduler.scheduleAtFixedRate(
                () -> cb.accept("this is a tick"), tick, tick, TimeUnit.SECONDS);
        } );

        // Finally create a robot that will count 5 ticks and then clean
        // up all resources
        AtomicInteger numberOfTicks = new AtomicInteger(0);
        ActiveRobot cleanup = Robots.newReactor("tick", (s) -> {
            if ( numberOfTicks.incrementAndGet() > 5 )
            {
                scheduler.shutdownNow();
            }
        });

        cleanup.activity().join();
    }
    
}
