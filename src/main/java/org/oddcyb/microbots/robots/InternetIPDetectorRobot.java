/*
 * Copyright 2018, 2019 Matt Dean
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.oddcyb.microbots.Robot;
import org.oddcyb.microbots.RobotException;

/**
 * Robot that can detect the current IP from the point of view of the internet.
 */
public class InternetIPDetectorRobot implements Robot
{
    private static final Logger LOG = 
        Logger.getLogger(InternetIPDetectorRobot.class.getName());
    
    /**
     * System property that provides the ip detection url.
     */
    public static final String IP_DETECT_URL_PROP = "microbots.ip-detect-url";

    /**
     * System property that provides the ip extraction regex.
     */
    public static final String IP_EXTRACT_RE_PROP = "microbots.ip-extract-re";

    /**
     * URL on internet that can be used to the IP.
     */
    public static final String IP_DETECT_URL = 
        System.getProperty(IP_DETECT_URL_PROP, "http://checkip.dyndns.org");

    /**
     * RE that will extract the IP from the output of the URL.
     */
    public static final String IP_EXTRACT_RE = 
        System.getProperty(IP_EXTRACT_RE_PROP, 
            ".*<body>Current IP Address: (?<ip>[0-9\\.]*)</body>.*");
    
    /**
     * Pattern that can extract an IP from the URL response.
     */
    public static final Pattern IP_EXTRACT_PATTERN = Pattern.compile(IP_EXTRACT_RE);
    
    private final Consumer<InetAddress> onAddress;
    
    public InternetIPDetectorRobot(Consumer<InetAddress> onAddress)
    {
        this.onAddress = onAddress;
    }
    
    @Override
    public void activate() throws RobotException
    {
        try
        {           
            var ip = getIP();

            if ( ip != null )
            {
                this.onAddress.accept(ip);
            }
        }
        catch ( IOException ie )
        {
            throw new RobotException("Detecting IP failed", ie);
        }
    }

    /**
     * Get the IP address.
     * 
     * @return the IP address
     * @throws IOException if something goes wrong
     */
    public InetAddress getIP() throws IOException 
    {
        // Use the URL connection
        // TODO switch to newer HTTPClient class
        var connection = new URL(IP_DETECT_URL).openConnection();
        
        // Read the content
        var reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        var content = new StringBuilder();
        String line;
        while ( (line = reader.readLine()) != null )
        {
            content.append(line);
        }
        LOG.log(Level.FINE, "Got DNS response {0}", content);
    
        // Split the IP out of the content
        var matcher = IP_EXTRACT_PATTERN.matcher(content.toString());
        if ( !matcher.matches() )
        {
            return null;
        }

        var ip = matcher.group("ip");
        LOG.log(Level.FINE, "Found IP: {0}", ip);
        return InetAddress.getByName(ip);
    }
  
}
