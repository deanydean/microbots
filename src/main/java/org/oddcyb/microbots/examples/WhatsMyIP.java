package org.oddcyb.microbots.examples;

import org.oddcyb.microbots.Robots;
import org.oddcyb.microbots.robots.InternetIPDetectorRobot;

/**
 * WhatsMyIP
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