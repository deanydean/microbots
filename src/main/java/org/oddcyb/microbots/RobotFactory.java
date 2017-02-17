/*
 * Copyright 2016, 2017 Matt Dean
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
package org.oddcyb.microbots;

import org.oddcyb.microbots.core.AsyncActiveRobot;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.oddcyb.microbots.core.event.EventRegistry;

/**
 * Factory that can activate robots.
 */
public class RobotFactory
{
    private static final ExecutorService EXECUTOR = 
            Executors.newFixedThreadPool(2);
    
    /**
     * Activate a robot
     * @param robot the robot to activate
     * @return the activated robot
     */
    public static ActiveRobot activate(Robot robot)
    {
        return new AsyncActiveRobot(robot, EXECUTOR);
    }
    
    /**
     * Create a new active watcher robot.
     * This method will block waiting for information supplied by the 
     * provided service.
     * 
     * @param <T> the type of info provided by service
     * @param service the service to watch
     * @return ActiveRobot watching the supplied service
     */
    public static <T> ActiveRobot newWatcher(Supplier<T> service)
    {
        return activate( () -> new Event<>(service.get()).send() );
    }
    
    /**
     * Create a new active watcher robot.
     * This method will not block. The provided callback will be given a
     * consumer function that can be called when information is available.
     * 
     * @param <T> the type of the info provided by the service
     * @param onService the service to watch
     * @return ActiveRobot watching the supplied service
     */
    public static <T> ActiveRobot newWatcher(Consumer<Consumer<T>> onService)
    {
        return activate( () -> onService.accept((t) -> new Event<>(t).send()) );
    }
    
    /**
     * Create a new active reactor robot.
     * This method will not block. The provided action will be performed when
     * an event is triggered with the provided "on" object.
     * 
     * @param <T> the type of the info to on on
     * @param on the object to on on
     * @param action the action to take
     * @return ActiveRobot waiting to on
     */
    public static <T> ActiveRobot newReactor(T on, Action<T> action)
    {
        return activate( () -> { EventRegistry.register(on, action); } );
    }
    
    /**
     * Shutdown this RobotFactory.
     * This method should be called when a RobotFactory should cease operation.
     */
    public static void shutdown()
    {
        EXECUTOR.shutdown();
        
        try
        {
            EXECUTOR.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch ( InterruptedException ie )
        {
            // Interrupted while waiting
        }
    }
    
}
