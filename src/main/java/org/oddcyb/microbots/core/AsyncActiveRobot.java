/*
 * Copyright 2018 Matt Dean
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
package org.oddcyb.microbots.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.oddcyb.microbots.ActiveRobot;
import org.oddcyb.microbots.Robot;

/**
 * Activates a Robot asynchronously.
 */
public class AsyncActiveRobot implements ActiveRobot
{
    private static final Logger LOG = 
        Logger.getLogger(AsyncActiveRobot.class.getName());
    
    private final Robot robot;
    private final Executor executor;
    private final CompletableFuture activity;
    
    /**
     * Create an async ActiveRobot.
     * 
     * @param robot the robot to activate
     * @param executor the executor to do the activity
     */
    public AsyncActiveRobot(Robot robot, Executor executor)
    {
        this.robot = robot;
        this.executor = executor;
        
        // Create the activity for this robot
        this.activity = CompletableFuture.runAsync( 
            () -> { 
                try
                {
                    this.robot.activate();
                }
                catch(Exception e)
                {
                    LOG.log(Level.WARNING, "Robot failed : {0}", e);
                }
            },
            executor);
    }
    
    /**
     * Get the robots activity.
     * 
     * @return CompletableFuture representing the robots activity.
     */
    @Override
    public CompletableFuture activity()
    {
        return this.activity;
    }
}
