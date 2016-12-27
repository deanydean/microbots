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
package org.oddcyb.microbots.core;

import org.oddcyb.microbots.Action;

/**
 * Reacts to something that occurs.
 * 
 * @param <T> the type of thing that occurs
 */
public interface Reactor<T> 
{
    
    /**
     * When object occurs, perform action.
     * @param object the object to react to
     * @param action the action to take
     */
    public void on(T object, Action action);
    
}
