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
package org.oddcyb.microbots.robots;

import java.io.IOException;

import org.oddcyb.microbots.Robot;
import org.oddcyb.microbots.RobotException;

/**
 * Robot that can issue commands.
 */
public class CommandRobot implements Robot
{

    private final ProcessBuilder processBuilder;
    
    /**
     * Create a new commander robot.
     * 
     * @param command the command to issue.
     */
    public CommandRobot(String... command)
    {
        this.processBuilder = new ProcessBuilder(command);
    }
    
    /**
     * Set an environment variable for the command.
     * 
     * @param name the name of the environment variable
     * @param value the value of the environment variable
     * @return this CommandRobot
     */
    public CommandRobot setEnv(String name, String value)
    {
        this.processBuilder.environment().put(name, value);
        return this;
    }
    
    /**
     * Inherit the current environment.
     * 
     * @return this CommandRobot
     */
    public CommandRobot inheritEnv()
    {
        System.getenv().forEach( 
            ( k,v ) -> { this.processBuilder.environment().put(k, v); } );
        return this;
    }

    /**
     * Issue the command.
     * 
     * @throws Exception if the command execution failed. 
     */
    @Override
    public void activate() throws RobotException
    {
        try
        {
            this.runCommand();   
        }
        catch ( IOException | InterruptedException ie )
        {
            throw new RobotException("Failed to activate", ie);
        }
    }

    /**
     * Run this robot's command.
     * 
     * @return the return code of the finished
     * @throws IOException if something goes wrong
     * @throws InterruptedException if waiting on the completion of the command
     * is interrupted
     */
    public int runCommand() throws IOException, InterruptedException
    {
        return this.processBuilder.start().waitFor();
    }
}