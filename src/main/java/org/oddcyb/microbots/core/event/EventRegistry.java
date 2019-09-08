/*
 * Copyright 2016, 2017 Matt Dean
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

import org.oddcyb.microbots.core.Reactor;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import org.oddcyb.microbots.Action;
import org.oddcyb.microbots.core.dispatch.Dispatcher;

/**
 * A registry for event subscribers.
 * TODO - Refactor away the singleton
 */
public class EventRegistry implements Dispatcher, Reactor
{   
    private static final EventRegistry REGISTRY = new EventRegistry();
    
    private final ConcurrentMap<String,Queue<Action>> register;
    
    /**
     * Create an EventRegistry.
     */
    private EventRegistry()
    {
        this.register = new ConcurrentHashMap<>();
    }
    
    /**
     * When object occurs, perform action.
     * 
     * @param object the object to react to
     * @param action the action to take
     */
    @Override
    public void on(String id, Action action)
    {
        Queue<Action> registered = 
            this.register.putIfAbsent(id, getEmptyQueue());
        
        this.register.get(id).add(action);
    }
    
    /**
     * Dispatch an event.
     * 
     * @param event the event to dispatch
     */
    @Override
    public void dispatch(Event event)
    {
        String id = event.getId();
        final Object object = event.getInfo();
        this.register.get(id).forEach( (action) -> action.perform(object) );
    }
    
    /**
     * Get a default empty queue.
     * @return 
     */
    private Queue<Action> getEmptyQueue()
    {
        return new ConcurrentLinkedQueue<>();
    }
    
    /**
     * Register an action to be triggered on a given object.
     * @param id to act on
     * @param action the action to take
     */
    public static final void register(String id, Action action)
    {
        REGISTRY.on(id, action);
    }
    
    /**
     * Send an event.
     * 
     * @param event the event to send 
     */
    public static final void send(Event event)
    {
        REGISTRY.dispatch(event);
    }
    
}