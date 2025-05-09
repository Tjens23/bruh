package dk.sdu.cbse.core;

import java.util.List;

/**
 * Service interface for post-processing game entities.
 * Post-processors run after all entity processors have completed.
 * Useful for collision detection, cleanup, and other operations
 * that depend on all entities being processed.
 */
public interface IPostEntityProcessorService {
    
    /**
     * Post-processes a list of entities.
     * This method is called after all entity processors have been executed.
     * 
     * @param entities The list of entities to post-process
     * @param deltaTime Time passed since last update in seconds
     */
    void postProcess(List<Entity> entities, float deltaTime);
}

