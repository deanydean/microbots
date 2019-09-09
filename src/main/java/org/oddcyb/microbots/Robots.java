/*
 * Copyright 2019 Matt Dean
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
package org.oddcyb.microbots;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Factory and utility methods for the Microbots framework.
 */
public class Robots
{

    private static final AtomicReference<RobotFactory> DEFAULT_FACTORY_REF =
        new AtomicReference<>(new RobotFactory());

    /**
     * 
     */
    public static RobotFactory factory()
    {
        return DEFAULT_FACTORY_REF.get();
    }

    public static ActiveRobot activate(Robot robot)
    {
        return factory().activate(robot);
    }

        /**
     * Create a new active watcher robot.
     * The robot will wait for information supplied by the provided service.
     * 
     * @param <T> the type of info provided by service
     * @param id to send watch info to
     * @param service the service to watch
     * @return ActiveRobot watching the supplied service
     */
    public static <T> ActiveRobot newWatcher(String id, Supplier<T> service)
    {
        return factory().newWatcher(id, service);
    }
    
    /**
     * Create a new active watcher robot.
     * The provided callback will be given a consumer function that can be 
     * called when information is available.
     * 
     * @param <T> the type of the info provided by the service
     * @param id to send watch info to
     * @param onService the service to watch
     * @return ActiveRobot watching the supplied service
     */
    public static <T> ActiveRobot newWatcher(String id, 
                                             Consumer<Consumer<T>> onService)
    {
        return factory().newWatcher(id, onService);
    }
    
    /**
     * Create a new active reactor robot.
     * This method will not block. The provided action will be performed when
     * an event is triggered with the provided "on" object.
     * 
     * @param <T> the type of the info to on on
     * @param id to act on
     * @param action the action to take
     * @return ActiveRobot waiting to on
     */
    public static <T> ActiveRobot newReactor(String id, Action<T> action)
    {
        return factory().newReactor(id, action);
    }
    
}