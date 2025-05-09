package dk.sdu.cbse.core.collision;

import dk.sdu.cbse.core.Entity;

import java.util.List;

/**
 * Service interface for collision detection and handling.
 * This interface defines methods for detecting and handling collisions between entities.
 */
public interface ICollisionService {
    
    /**
     * Detects and handles collisions between entities.
     * 
     * @param entities List of entities to check for collisions
     * @return List of collision results
     */
    List<CollisionResult> detectCollisions(List<Entity> entities);
    
    /**
     * Checks if two entities are colliding.
     * 
     * @param entity1 First entity
     * @param entity2 Second entity
     * @return True if the entities are colliding, false otherwise
     */
    boolean checkCollision(Entity entity1, Entity entity2);
    
    /**
     * Adds a collision handler for specific entity types.
     * 
     * @param type1 First entity type
     * @param type2 Second entity type
     * @param handler Handler for this collision type
     */
    void addCollisionHandler(String type1, String type2, CollisionHandler handler);
    
    /**
     * Removes a collision handler for specific entity types.
     * 
     * @param type1 First entity type
     * @param type2 Second entity type
     */
    void removeCollisionHandler(String type1, String type2);
    
    /**
     * Sets whether collisions should be processed automatically.
     * 
     * @param autoProcess True to process collisions automatically, false to just detect them
     */
    void setAutoProcess(boolean autoProcess);
    
    /**
     * Processes a collision between two entities using the appropriate handler.
     * 
     * @param entity1 First entity
     * @param entity2 Second entity
     * @return A collision result object or null if no handler was found
     */
    CollisionResult processCollision(Entity entity1, Entity entity2);
    
    /**
     * Interface for collision handlers.
     */
    interface CollisionHandler {
        /**
         * Handles a collision between two entities.
         * 
         * @param entity1 First entity
         * @param entity2 Second entity
         * @return The result of the collision
         */
        CollisionResult handleCollision(Entity entity1, Entity entity2);
    }
    
    /**
     * Class representing the result of a collision.
     */
    class CollisionResult {
        private final Entity entity1;
        private final Entity entity2;
        private final boolean resolved;
        
        /**
         * Creates a new collision result.
         * 
         * @param entity1 First entity
         * @param entity2 Second entity
         * @param resolved Whether the collision was successfully resolved
         */
        public CollisionResult(Entity entity1, Entity entity2, boolean resolved) {
            this.entity1 = entity1;
            this.entity2 = entity2;
            this.resolved = resolved;
        }
        
        /**
         * Gets the first entity involved in the collision.
         * 
         * @return The first entity
         */
        public Entity getEntity1() {
            return entity1;
        }
        
        /**
         * Gets the second entity involved in the collision.
         * 
         * @return The second entity
         */
        public Entity getEntity2() {
            return entity2;
        }
        
        /**
         * Checks if the collision was successfully resolved.
         * 
         * @return True if the collision was resolved, false otherwise
         */
        public boolean isResolved() {
            return resolved;
        }
    }
}

