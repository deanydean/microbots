/*
 * Copyright 2019 Matt Dean
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

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

import org.oddcyb.microbots.Robots;
import org.oddcyb.microbots.robots.InternetIPDetectorRobot;

/**
 * Example of creating a robot that reports your IP address.
 */
public class WhatsMyIP
{

    public static void main(String[] args)
    {
        // Create a basic barrier that can be passed when the output has 
        // been displayed
        var displayBarrier = new Phaser(2);

        // Create a robot that prints the IP to the screen.
        var detector = new InternetIPDetectorRobot( (ip) -> {
            if ( ip != null )
            {
                System.out.println("Your IP address is:");
                System.out.println(
                    ip.getHostAddress()+" ["+ip.getHostName()+"]");
            }
            else
            {
                System.err.println("Could not get IP address");
            }

            // Tell the barrier that we're done
            displayBarrier.arriveAndAwaitAdvance();
        });

        // Active the detector robot
        Robots.activate(detector);

        // Now wait for the display from the robot
        displayBarrier.arriveAndAwaitAdvance();
    }
    
}