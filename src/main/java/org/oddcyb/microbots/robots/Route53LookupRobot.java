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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResult;
import com.amazonaws.services.route53.model.ResourceRecordSet;

import org.oddcyb.microbots.Robot;
import org.oddcyb.microbots.RobotException;

/**
 * Robot that can lookup an AWS Route53 host entry.
 */
public class Route53LookupRobot implements Robot
{
    
    private final Regions region;
    private final String zone;
    private final List<String> entires;
    private final Consumer<ResourceRecordSet> onResult;
    
    public Route53LookupRobot(String region, String zone, List<String> entires,
                              Consumer<ResourceRecordSet> onResult)
    {
        this.region = Regions.valueOf(region);
        this.zone = zone;
        this.entires = new ArrayList<>(entires);
        this.onResult = onResult;
    }

    @Override
    public void activate() throws RobotException
    {
        getRecords().forEach( (record) -> {
            onResult.accept(record);
        });
    }

    /**
     * Get the records for this robot's entries.
     * 
     * @return a list of the records
     */
    public List<ResourceRecordSet> getRecords()
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

        // Return the records that this robot will look for
        return result.getResourceRecordSets()
                     .stream()
                     .filter( (r) -> this.entires.contains(r.getName()) )
                     .collect(Collectors.toList());
    }
    
}
