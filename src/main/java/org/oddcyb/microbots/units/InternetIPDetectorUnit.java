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
package org.oddcyb.microbots.units;

import java.io.BufferedReader;
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

/**
 * Robot unit that can detect the current IP from the point of view of the
 * internet.
 */
public class InternetIPDetectorUnit implements Robot
{
    private static final Logger LOG = 
        Logger.getLogger(InternetIPDetectorUnit.class.getName());
    
    /**
     * URL on internet that can be used to the IP.
     */
    public static final String DNS_DETECT_URL = "http://checkip.dyndns.org";
    
    /**
     * Pattern that can extract an IP from the URL response.
     */
    public static final Pattern IP_EXTRACTOR_RE =
        Pattern.compile(".*<body>Current IP Address: (?<ip>[0-9\\.]*)</body>.*");
    
    private final Consumer<InetAddress> onAddress;
    
    public InternetIPDetectorUnit(Consumer<InetAddress> onAddress)
    {
        this.onAddress = onAddress;
    }
    
    @Override
    public void activate() throws Exception
    {
        // Use the URL connection
        // TODO switch to newer HTTPClient class
        URLConnection connection = new URL(DNS_DETECT_URL).openConnection();
        
        // Read the content
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;
        while ( (line = reader.readLine()) != null )
        {
            content.append(line);
        }
        LOG.log(Level.FINE, "Got DNS response {0}", content);
        
        // Split the IP out of the content
        Matcher matcher = IP_EXTRACTOR_RE.matcher(content.toString());
        if ( matcher.matches() )
        {
            String ip = matcher.group("ip");
                LOG.log(Level.FINE, "Found IP: {0}", ip);
            this.onAddress.accept(InetAddress.getByName(ip));
        }
    }
  
}
