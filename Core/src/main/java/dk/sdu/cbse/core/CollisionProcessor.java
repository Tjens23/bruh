package dk.sdu.cbse.core;

import dk.sdu.cbse.core.collision.ICollisionService;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Post-processor that handles collisions between entities.
 * Implements both IPostEntityProcessorService for legacy support and
 * ICollisionService for the new component system.
 */
@Component
public class CollisionProcessor implements IPostEntityProcessorService, ICollisionService {
    
    private static final Logger logger = Logger.getLogger(CollisionProcessor.class.getName());

    // Game state
    private int playerScore = 0;
    private int playerLives = 3;
    
    // Collision handling
    private final Map<String, Map<String, CollisionHandler>> handlers = new HashMap<>();
    private boolean autoProcess = true;
    
    // Entity lists for collision processing
    private final List<Entity> playerEntities = new ArrayList<>();
    private final List<Entity> enemyEntities = new ArrayList<>();
    private final List<Entity> asteroidEntities = new ArrayList<>();
    private final List<Entity> projectileEntities = new ArrayList<>();
    
    // References to plugin methods via reflection (would be implemented in full version)
    private Object asteroidSplitter = null;
    
    @Override
    public void postProcess(List<Entity> entities, float deltaTime) {
        // Clear entity lists
        playerEntities.clear();
        enemyEntities.clear();
        asteroidEntities.clear();
        projectileEntities.clear();
        
        // Categorize entities by type
        categorizeEntities(entities);
        
        // Process player collisions with asteroids
        processPlayerAsteroidCollisions(entities);
        
        // Process player collisions with enemies
        processPlayerEnemyCollisions(entities);
        
        // Process projectile collisions with targets
        processProjectileCollisions(entities);
        
        // Process enemy-asteroid collisions
        processEnemyAsteroidCollisions(entities);
    }
    
    /**
     * Categorizes entities into specific lists based on their type.
     */
    private void categorizeEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            if (!entity.isActive()) continue;
            
            String type = entity.getType();
            if (type == null) continue;
            
            switch (type) {
                case "player":
                    playerEntities.add(entity);
                    break;
                case "enemy":
                    enemyEntities.add(entity);
                    break;
                case "asteroid":
                    asteroidEntities.add(entity);
                    break;
                case "projectile":
                    projectileEntities.add(entity);
                    break;
            }
        }
    }
    
    /**
     * Processes collisions between player and asteroids.
     */
    private void processPlayerAsteroidCollisions(List<Entity> entities) {
        // For each player entity
        for (Entity player : playerEntities) {
            // Check collision with each asteroid
            for (Entity asteroid : asteroidEntities) {
                if (checkCollision(player, asteroid)) {
                    handlePlayerAsteroidCollision(player, asteroid, entities);
                }
            }
        }
    }
    
    /**
     * Processes collisions between player and enemies.
     */
    private void processPlayerEnemyCollisions(List<Entity> entities) {
        // For each player entity
        for (Entity player : playerEntities) {
            // Check collision with each enemy
            for (Entity enemy : enemyEntities) {
                if (checkCollision(player, enemy)) {
                    handlePlayerEnemyCollision(player, enemy);
                }
            }
        }
    }
    
    /**
     * Processes collisions between projectiles and their targets.
     */
    private void processProjectileCollisions(List<Entity> entities) {
        // For each projectile
        for (Entity projectile : projectileEntities) {
            // Check collision with asteroids
            for (Entity asteroid : asteroidEntities) {
                if (checkCollision(projectile, asteroid)) {
                    handleProjectileAsteroidCollision(projectile, asteroid, entities);
                    break; // Projectile hits only one target
                }
            }
            
            // Check collision with enemies
            for (Entity enemy : enemyEntities) {
                if (checkCollision(projectile, enemy)) {
                    handleProjectileEnemyCollision(projectile, enemy);
                    break; // Projectile hits only one target
                }
            }
        }
    }
    
    /**
     * Processes collisions between enemies and asteroids.
     */
    private void processEnemyAsteroidCollisions(List<Entity> entities) {
        // For each enemy
        for (Entity enemy : enemyEntities) {
            // Check collision with each asteroid
            for (Entity asteroid : asteroidEntities) {
                if (checkCollision(enemy, asteroid)) {
                    handleEnemyAsteroidCollision(enemy, asteroid);
                }
            }
        }
    }
    
    /**
     * Handles a collision between a player and an asteroid.
     */
    private void handlePlayerAsteroidCollision(Entity player, Entity asteroid, List<Entity> entities) {
        // Decrement player lives
        playerLives--;
        
        // Check if game over
        if (playerLives <= 0) {
            player.setActive(false);
            // Game over logic would be implemented here
        } else {
            // Reset player position (in a real implementation)
            // This would typically be done by setting a respawn flag or timer
        }
        
        // Try to split the asteroid
        tryToSplitAsteroid(asteroid, entities);
    }
    
    /**
     * Handles a collision between a player and an enemy.
     */
    private void handlePlayerEnemyCollision(Entity player, Entity enemy) {
        // Decrement player lives
        playerLives--;
        
        // Check if game over
        if (playerLives <= 0) {
            player.setActive(false);
            // Game over logic would be implemented here
        } else {
            // Reset player position (in a real implementation)
            // This would typically be done by setting a respawn flag or timer
        }
        
        // Destroy enemy
        enemy.setActive(false);
        
        // Add points for destroying enemy
        playerScore += 150;
    }
    
    /**
     * Handles a collision between a projectile and an asteroid.
     */
    private void handleProjectileAsteroidCollision(Entity projectile, Entity asteroid, List<Entity> entities) {
        // Destroy projectile
        projectile.setActive(false);
        
        // Try to split the asteroid and add points
        boolean split = tryToSplitAsteroid(asteroid, entities);
        
        // Add points based on asteroid size (would check actual size in real implementation)
        // Here we just use a default value
        playerScore += 50;
    }
    
    /**
     * Handles a collision between a projectile and an enemy.
     */
    private void handleProjectileEnemyCollision(Entity projectile, Entity enemy) {
        // Destroy projectile
        projectile.setActive(false);
        
        // Destroy enemy
        enemy.setActive(false);
        
        // Add points for destroying enemy
        playerScore += 150;
    }
    
    /**
     * Handles a collision between an enemy and an asteroid.
     */
    private void handleEnemyAsteroidCollision(Entity enemy, Entity asteroid) {
        // In a real implementation, could damage the enemy or have it bounce off
        // For simplicity, we'll just have them push each other
        
        // Apply simple collision response (in a real physics implementation)
        // This would calculate proper collision response vectors
    }
    
    /**
     * Tries to split an asteroid using the AsteroidProcessor.
     * In a full implementation, this would use reflection or direct references.
     */
    private boolean tryToSplitAsteroid(Entity asteroid, List<Entity> entities) {
        // Mark asteroid as inactive
        asteroid.setActive(false);
        
        // In a complete implementation, we would call the asteroid processor's
        // splitAsteroid method. Here we're just simulating that behavior.
        
        // Return true if asteroid was successfully split
        return true;
    }
    
    /**
     * Checks if there is a collision between two entities using circle collision detection.
     */
    @Override
    public boolean checkCollision(Entity a, Entity b) {
        if (!a.isActive() || !b.isActive()) return false;
        
        // Calculate distance between centers
        float dx = a.getX() - b.getX();
        float dy = a.getY() - b.getY();
        float distanceSquared = dx * dx + dy * dy;
        
        // Calculate sum of radii
        float radiiSum = a.getRadius() + b.getRadius();
        float radiiSumSquared = radiiSum * radiiSum;
        
        // Collision occurs if the distance squared is less than the sum of radii squared
        return distanceSquared < radiiSumSquared;
    }
    
    // Getter and setter methods for game state
    
    public int getPlayerScore() {
        return playerScore;
    }
    
    public void setScore(int score) {
        this.playerScore = score;
    }
    
    public int getPlayerLives() {
        return playerLives;
    }
    
    public void setLives(int lives) {
        this.playerLives = lives;
    }
    
    // ICollisionService implementation
    
    @Override
    public List<CollisionResult> detectCollisions(List<Entity> entities) {
        List<CollisionResult> results = new ArrayList<>();
        
        // Check all entity pairs for collisions
        for (int i = 0; i < entities.size(); i++) {
            Entity entity1 = entities.get(i);
            
            if (!entity1.isActive()) continue;
            
            for (int j = i + 1; j < entities.size(); j++) {
                Entity entity2 = entities.get(j);
                
                if (!entity2.isActive()) continue;
                
                // Check for collision between the two entities
                if (checkCollision(entity1, entity2)) {
                    // Process the collision if auto-processing is enabled
                    if (autoProcess) {
                        CollisionResult result = processCollision(entity1, entity2);
                        if (result != null) {
                            results.add(result);
                        }
                    } else {
                        // Just add a result without processing
                        results.add(new CollisionResult(entity1, entity2, false));
                    }
                }
            }
        }
        
        return results;
    }
    
    @Override
    public void addCollisionHandler(String type1, String type2, CollisionHandler handler) {
        // Ensure the type maps exist
        handlers.computeIfAbsent(type1, k -> new HashMap<>());
        handlers.computeIfAbsent(type2, k -> new HashMap<>());
        
        // Add handler in both directions for symmetric collision detection
        handlers.get(type1).put(type2, handler);
        
        // Only add the reverse mapping if types are different
        if (!type1.equals(type2)) {
            handlers.get(type2).put(type1, (entity2, entity1) -> 
                handler.handleCollision(entity1, entity2));
        }
        
        logger.info("Added collision handler for " + type1 + " and " + type2);
    }
    
    @Override
    public void removeCollisionHandler(String type1, String type2) {
        // Remove handlers in both directions
        if (handlers.containsKey(type1)) {
            handlers.get(type1).remove(type2);
        }
        
        if (handlers.containsKey(type2)) {
            handlers.get(type2).remove(type1);
        }
        
        logger.info("Removed collision handler for " + type1 + " and " + type2);
    }
    
    @Override
    public void setAutoProcess(boolean autoProcess) {
        this.autoProcess = autoProcess;
    }
    
    @Override
    public CollisionResult processCollision(Entity entity1, Entity entity2) {
        String type1 = entity1.getType();
        String type2 = entity2.getType();
        
        // If either entity has no type, we can't process
        if (type1 == null || type2 == null) {
            return null;
        }
        
        // Find the appropriate handler
        CollisionHandler handler = null;
        if (handlers.containsKey(type1) && handlers.get(type1).containsKey(type2)) {
            handler = handlers.get(type1).get(type2);
        }
        
        // Process the collision if a handler was found
        if (handler != null) {
            return handler.handleCollision(entity1, entity2);
        }
        
        // Legacy collision handling (use the built-in handlers)
        if (type1.equals("player") && type2.equals("asteroid") || 
            type2.equals("player") && type1.equals("asteroid")) {
            // Find player and asteroid
            Entity player = type1.equals("player") ? entity1 : entity2;
            Entity asteroid = type1.equals("asteroid") ? entity1 : entity2;
            
            // Handle collision
            handlePlayerAsteroidCollision(player, asteroid, new ArrayList<>());
            return new CollisionResult(player, asteroid, true);
        }
        
        if (type1.equals("player") && type2.equals("enemy") || 
            type2.equals("player") && type1.equals("enemy")) {
            // Find player and enemy
            Entity player = type1.equals("player") ? entity1 : entity2;
            Entity enemy = type1.equals("enemy") ? entity1 : entity2;
            
            // Handle collision
            handlePlayerEnemyCollision(player, enemy);
            return new CollisionResult(player, enemy, true);
        }
        
        if (type1.equals("projectile") && type2.equals("asteroid") || 
            type2.equals("projectile") && type1.equals("asteroid")) {
            // Find projectile and asteroid
            Entity projectile = type1.equals("projectile") ? entity1 : entity2;
            Entity asteroid = type1.equals("asteroid") ? entity1 : entity2;
            
            // Handle collision
            handleProjectileAsteroidCollision(projectile, asteroid, new ArrayList<>());
            return new CollisionResult(projectile, asteroid, true);
        }
        
        if (type1.equals("projectile") && type2.equals("enemy") || 
            type2.equals("projectile") && type1.equals("enemy")) {
            // Find projectile and enemy
            Entity projectile = type1.equals("projectile") ? entity1 : entity2;
            Entity enemy = type1.equals("enemy") ? entity1 : entity2;
            
            // Handle collision
            handleProjectileEnemyCollision(projectile, enemy);
            return new CollisionResult(projectile, enemy, true);
        }
        
        return null;
    }
}

