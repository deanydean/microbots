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
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final HttpRequest request;
    
    public InternetIPDetectorRobot(Consumer<InetAddress> onAddress)
    {
        this.onAddress = onAddress;
        this.request = HttpRequest.newBuilder(URI.create(IP_DETECT_URL))
                                  .version(Version.HTTP_1_1)
                                  .GET()
                                  .build();
    }
    
    @Override
    public void activate() throws RobotException
    {
        try
        {           
            getIP(this.onAddress);
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
    public void getIP(Consumer<InetAddress> onIP) throws IOException 
    {        
        HttpClient
            .newHttpClient()
            .sendAsync(this.request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenApply( (body) -> ipDetectBodyToAddr(body) )
            .thenAccept( (ip) -> onIP.accept(ip) );
    }

    /**
     * Convert the body of the response from the IP detect URL to an InetAdress.
     * 
     * @param body the body of the response from the IP detect URL
     * @return the InetAddress from the body, or null if none could be found
     */
    public static InetAddress ipDetectBodyToAddr(String body)
    {
        var matcher = IP_EXTRACT_PATTERN.matcher(body);
        if ( !matcher.matches() )
        {
            LOG.warning( () -> "No IP found in body: "+body);
            return null;
        }

        var ip = matcher.group("ip");

        try
        {
            return InetAddress.getByName(ip);
        }
        catch ( UnknownHostException uhe )
        {
            LOG.warning( () -> "Cannot resolve "+ip );
            return null;
        }
    }
}
