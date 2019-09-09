/*
 * Copyright 2016, 2018, Matt Dean
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
package org.oddcyb.microbots.core.event;

import org.oddcyb.microbots.core.dispatch.Dispatchers;

/**
 * An event that can occur.
 * @param <T> the type of information provided with this event
 */
public class Event<T> 
{
    private final String id;
    private final T info;
    
    /**
     * Create an event for the provide info.
     * 
     * @param info the info for the event
     */
    public Event(String id, T info)
    {
        this.id = id;
        this.info = info;
    }
    
    /**
     * Get the info for this event.
     * 
     * @return the info 
     */
    public T getInfo()
    {
        return this.info;
    }
    
    /**
     * 
     * @return 
     */
    public String getId()
    {
        return this.id;
    }
  
    /**
     * Send the event.
     * Multiple calls to this method will send the event multiple times.
     */
    public final void send()
    {
        Dispatchers.EVENT.dispatcher().dispatch(this);
    }
    
}
