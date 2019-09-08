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

import java.util.List;
import java.util.function.Consumer;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResult;

import org.oddcyb.microbots.Robot;
import org.oddcyb.microbots.RobotException;

/**
 * Robot unit that can lookup an AWS Route53 host entry that need updating.
 */
public class Route53UpdateDetectorUnit implements Robot
{
    
    private final Regions region;
    private final String zone;
    private final List<String> hosts;
    private final String expectedIp;
    private final Consumer<String> onResult;
    
    public Route53UpdateDetectorUnit(String region, String zone, List<String> hosts,
            String expectedIp, Consumer<String> onResult)
    {
        this.region = Regions.valueOf(region);
        this.zone = zone;
        this.hosts = hosts;
        this.expectedIp = expectedIp;
        this.onResult = onResult;
    }

    @Override
    public void activate() throws RobotException
    {
        // Get a r53 client
        AmazonRoute53 r53 = AmazonRoute53ClientBuilder
            .standard()
            .withRegion(this.region)
            .build();
        
        // Create the request
        ListResourceRecordSetsRequest request = 
            new ListResourceRecordSetsRequest()
                .withHostedZoneId(zone);
        
        // Get the result from AWS
        ListResourceRecordSetsResult result = 
            r53.listResourceRecordSets(request);
        
        // Look for host entries that do not match the expected ip
        result.getResourceRecordSets().forEach( (record) -> {
            
        });
    }
    
}
