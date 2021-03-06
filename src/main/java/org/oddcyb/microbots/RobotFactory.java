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
package org.oddcyb.microbots;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.oddcyb.microbots.core.AsyncActiveRobot;
import org.oddcyb.microbots.core.event.Event;
import org.oddcyb.microbots.core.event.EventRegistry;
import org.oddcyb.microbots.core.util.RobotThreadFactory;

/**
 * Factory that can activate robots.
 */
public class RobotFactory
{
    private final ExecutorService executor;
    
    public RobotFactory()
    {
        this.executor = 
            Executors.newCachedThreadPool(new RobotThreadFactory());
    }

    public RobotFactory(ExecutorService executors)
    {
        this.executor = executors;
    }

    /**
     * Activate a robot.
     * 
     * @param robot the robot to activate
     * @return the activated robot
     */
    public ActiveRobot activate(Robot robot)
    {
        return new AsyncActiveRobot(robot, this.executor);
    }
    
    /**
     * Create a new active watcher robot.
     * The robot will wait for information supplied by the provided service.
     * 
     * @param <T> the type of info provided by service
     * @param id to send watch info to
     * @param service the service to watch
     * @return ActiveRobot watching the supplied service
     */
    public <T> ActiveRobot newWatcher(String id, Supplier<T> service)
    {
        return activate( () -> new Event<>(id, service.get()).send() );
    }
    
    /**
     * Create a new active watcher robot.
     * The provided callback will be given a consumer function that can be 
     * called when information is available.
     * 
     * @param <T> the type of the info provided by the service
     * @param id to send watch info to
     * @param onService the service to watch
     * @return ActiveRobot watching the supplied service
     */
    public <T> ActiveRobot newWatcher(String id, 
                                      Consumer<Consumer<T>> onService)
    {
        return activate( () -> {
            onService.accept( (t) -> {
                new Event<>(id, t).send() ;
            }); 
        });
    }
    
    /**
     * Create a new active reactor robot.
     * This method will not block. The provided action will be performed when
     * an event is triggered with the provided "on" object.
     * 
     * @param <T> the type of the info to on on
     * @param id to act on
     * @param action the action to take
     * @return ActiveRobot waiting to on
     */
    public <T> ActiveRobot newReactor(String id, Action<T> action)
    {
        return activate( () -> { EventRegistry.register(id, action); } );
    }
    
    /**
     * Shutdown the RobotFactory.
     * This method should be called when the RobotFactory should cease operation.
     */
    public void shutdown()
    {
        this.executor.shutdown();
        
        try
        {
            this.executor.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch ( InterruptedException ie )
        {
            // Interrupted while waiting
            this.executor.shutdownNow();
        }
    }
    
}
