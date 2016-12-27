/*
 * Copyright 2016 Matt Dean
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
package org.oddcyb.microbots.core.dispatch;

import org.oddcyb.microbots.core.event.EventRegistry;

/**
 * An enum of factories for EventRegistry Dispatchers.
 */
public enum Dispatchers implements DispatcherFactory
{
    
    /**
     * A factory for the main event dispatcher.
     */
    EVENT( () -> { return (event) -> EventRegistry.send(event); } );
    
    private final DispatcherFactory dispatcherFactory;
    
    private Dispatchers(DispatcherFactory dispatcherFactory)
    {
        this.dispatcherFactory = dispatcherFactory;
    }
    
    /**
     * Get a dispatcher.
     * 
     * @return the dispatcher 
     */
    @Override
    public Dispatcher dispatcher()
    {
        return this.dispatcherFactory.dispatcher();
    }
    
}
