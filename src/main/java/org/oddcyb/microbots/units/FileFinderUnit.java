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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.oddcyb.microbots.Robot;
import org.oddcyb.microbots.RobotException;

/**
 * Robot that can find files.
 */
public class FileFinderUnit extends SimpleFileVisitor<Path> implements Robot
{
    private static final Logger LOG =
        Logger.getLogger(FileFinderUnit.class.getName());

    private final Set<Path> roots;
    private final BiConsumer<Path,BasicFileAttributes> onFile;
    
    /**
     * Create a new file finder robot.
     * 
     * @param roots the filesystem roots to search
     * @param onFile called when a file is found
     */
    public FileFinderUnit(Set<Path> roots, 
            BiConsumer<Path,BasicFileAttributes> onFile)
    {
        this.roots = roots;
        this.onFile = onFile;
    }
    
    @Override
    public void activate() throws RobotException
    {
        LOG.log(Level.INFO, "Activating {0}", this);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try
        {
            roots.forEach((root) -> {
                executor.submit( () -> {
                    try
                    {

                        Files.walkFileTree(root, this);
                    }
                    catch ( IOException ioe )
                    {
                        LOG.log(Level.WARNING, "Failed to walk {0} : {1}",
                            new Object[]{ root, ioe });
                    }
                });
            });
        }
        catch ( Exception e )
        {
            LOG.log(Level.WARNING, "Failed to find file {0} : {1}", 
                new Object[]{ this.roots, e });
            throw new RobotException("Error in file finder", e);
        }
        finally
        {
            executor.shutdown();

            try
            {
                executor.awaitTermination(10, TimeUnit.MINUTES);
            }
            catch ( InterruptedException ie )
            {
                // Failed to wait for termination, terminal now
                executor.shutdownNow();
            }
        }
        
        LOG.log(Level.INFO, "Completed {0}", this);
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
    {
        if ( Files.isDirectory(file) )
        {
            try
            {
                // Check we can list files in the directory
                Files.list(file);
            }
            catch ( IOException ioe )
            {
                LOG.log(Level.WARNING, "Failed to list files in {0} : {1}",
                    new Object[]{ file, ioe });
                return FileVisitResult.SKIP_SUBTREE;
            }
        }
        this.onFile.accept(file, attrs);
        return FileVisitResult.CONTINUE;
    }
}
