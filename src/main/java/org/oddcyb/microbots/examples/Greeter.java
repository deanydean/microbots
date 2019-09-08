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

import java.util.Random;
import org.oddcyb.microbots.Robot;
import org.oddcyb.microbots.Robots;

/**
 * Example of creating a robot that greets you.
 */
public class Greeter
{
    // An RNG that will give us a random greeting
    private static final Random RNG = new Random();
    
    /**
     * Display the usage message for this class.
     */
    public static void usage()
    {
        System.out.println("usage: Greeter <your-name>");
    }

    public static void main(String[] args)
    {
        if ( args.length != 1 )
        {
            usage();
            System.exit(1);
        }
        
        // Create some greetings for our robot
        String[] greets = 
            new String[] { "Hello", "Hi", "Hey", "Howdy", "Hej"};
   
        // Create a robot that will greet
        Robot greeter = () ->
            System.out.println(greets[RNG.nextInt(greets.length)]+" "+args[0]);
        
        // Activate the robot using the robot factory and wait for it to greet
        Robots.activate(greeter).activity().join();
    }
    
}
