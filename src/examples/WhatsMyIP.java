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

import org.oddcyb.microbots.Robots;
import org.oddcyb.microbots.robots.InternetIPDetectorRobot;

/**
 * 
 */
public class WhatsMyIP
{

    public static void main(String[] args)
    {
        // Create a robot that prints the IP to the screen.
        var detector = new InternetIPDetectorRobot( (ip) -> {
            System.out.println("Your IP address is:");
            System.out.println(ip.getHostAddress()+" ["+ip.getHostName()+"]");
        });

        // Active the detector robot and wait for it to complete.
        Robots.activate(detector).activity().join();
    }
    
}