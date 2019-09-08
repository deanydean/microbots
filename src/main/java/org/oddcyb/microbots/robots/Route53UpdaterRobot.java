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

import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import com.amazonaws.services.route53.model.RRType;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.oddcyb.microbots.Robot;
import org.oddcyb.microbots.RobotException;

/**
 * Robot that can update a AWS Route53 host entries.
 */
public class Route53UpdaterRobot implements Robot
{
    
    private final Regions region;
    private final String zone;
    private final ChangeBatch batch;
    
    /**
     * Create a robot for the provided list of named entries.
     * 
     * @param region the AWS region
     * @param zone the AWS zone
     * @param entries a list of entries to update
     * @param ip the ip to update the entries to
     */
    public Route53UpdaterRobot(String region, String zone, 
                               List<String> entries, String ip)
    {
        this.region = Regions.valueOf(region);
        this.zone = zone;

        // Create the change
        var resources = Arrays.asList(
            new ResourceRecord(ip)
        );
        
        // Add each host to the changes
        var changes = new ArrayList<Change>();
        entries.forEach( (host) -> {
            ResourceRecordSet recordSet = 
                new ResourceRecordSet(host, RRType.A);
            recordSet.setTTL(300l);
            recordSet.setResourceRecords(resources);
            changes.add(
                new Change(ChangeAction.UPSERT, recordSet)
            );
        } );

        // Create the catch to update the entries
        this.batch = new ChangeBatch(changes);
    }

    /**
     * Create a robot for the provided list of resource records.
     * 
     * @param region the AWS region
     * @param zone the AWS zone
     * @param records a list of AWS ResourceRecordSets to update
     * @param ip the ip to update the entries to
     * @param alwaysUpdate true if an entry should always be updated,
     * false if an entry should only be updated if it's changed
     */
    public Route53UpdaterRobot(String region, String zone, 
                               List<ResourceRecordSet> records,
                               String ip, boolean alwaysUpdate)
    {
        this.region = Regions.valueOf(region);
        this.zone = zone;

        // Create the change
        var resources = Arrays.asList(
            new ResourceRecord(ip)
        );

        var changes = new ArrayList<Change>();
        records.forEach( (record) -> {
            if ( alwaysUpdate || 
                 !record.getResourceRecords().contains(new ResourceRecord(ip)) )
            {
                record.setResourceRecords(resources);
                changes.add(new Change(ChangeAction.UPSERT, record));
            }
        });

        this.batch = new ChangeBatch(changes);
    }

    @Override
    public void activate() throws RobotException
    {
        update();
    }

    /**
     * Perform the update.
     * 
     * @return the result of the update
     */
    public ChangeResourceRecordSetsResult update()
    {
        // Get a r53 client
        var r53 = AmazonRoute53ClientBuilder
                    .standard()
                    .withRegion(this.region)
                    .build();

        // Change the record
        var changeRequest = new ChangeResourceRecordSetsRequest()
                                    .withHostedZoneId(this.zone)
                                    .withChangeBatch(this.batch);
        return r53.changeResourceRecordSets(changeRequest);
    }
    
}
