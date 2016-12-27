/*
 * Copyright 2016 Matt Dean. All rights reserved.
 */
package org.oddcyb.microbots.core.event;

import org.oddcyb.microbots.core.Reactor;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import org.oddcyb.microbots.Action;
import org.oddcyb.microbots.Event;
import org.oddcyb.microbots.core.dispatch.Dispatcher;

/**
 * A registry for event subscribers.
 * TODO - Refactor away the singleton
 */
public class EventRegistry implements Dispatcher, Reactor
{   
    private static final EventRegistry REGISTRY = new EventRegistry();
    
    private final ConcurrentMap<Object,Queue<Action>> register;
    
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
    public void on(Object object, Action action)
    {
        Queue<Action> registered = 
            this.register.putIfAbsent(object, getEmptyQueue());
        
        this.register.get(object).add(action);
    }
    
    /**
     * Dispatch an event.
     * 
     * @param event the event to dispatch
     */
    @Override
    public void dispatch(Event event)
    {
        final Object object = event.getInfo();
        this.register.get(object).forEach((action) -> action.perform(object));
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
     * @param on the object to act on
     * @param action the action to take
     */
    public static final void register(Object on, Action action)
    {
        REGISTRY.on(on, action);
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