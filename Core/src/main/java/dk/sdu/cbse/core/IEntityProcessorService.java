package dk.sdu.cbse.core;

import java.util.List;

/**
 * Service interface for processing game entities.
 * Implementations handle game logic for specific entity types.
 */
public interface IEntityProcessorService {
    
    /**
     * Processes a list of entities.
     * This method should update entity states based on game logic.
     * 
     * @param entities The list of entities to process
     * @param deltaTime Time passed since last update in seconds
     */
    void process(List<Entity> entities, float deltaTime);
}

