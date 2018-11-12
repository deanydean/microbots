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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.oddcyb.microbots.Robot;

/**
 * Robot unit that can update an AWS Route53 host entry.
 */
public class Route53UpdaterUnit implements Robot
{
    
    private final Regions region;
    private final String zone;
    private final String host;
    private final String ip;
    
    public Route53UpdaterUnit(String region, String zone, String host, 
            String ip)
    {
        this.region = Regions.valueOf(region);
        this.zone = zone;
        this.host = host;
        this.ip = ip;
    }

    @Override
    public void activate() throws Exception
    {
        // Create the change
        Collection<ResourceRecord> records = Arrays.asList(
            new ResourceRecord(ip)
        );
        ResourceRecordSet recordSet = new ResourceRecordSet(host, RRType.A);
        recordSet.setTTL(300l);
        recordSet.setResourceRecords(records);
        List<Change> changes = Arrays.asList(
            new Change(ChangeAction.UPSERT, recordSet)
        );
        ChangeBatch batch = new ChangeBatch(changes);

        // Get a r53 client
        AmazonRoute53 r53 = AmazonRoute53ClientBuilder
            .standard()
            .withRegion(this.region)
            .build();

        // Change the record
        ChangeResourceRecordSetsRequest changeRequest =
            new ChangeResourceRecordSetsRequest()
                .withHostedZoneId(zone)
                .withChangeBatch(batch);
        ChangeResourceRecordSetsResult changeResult =
            r53.changeResourceRecordSets(changeRequest);
    }
    
}
