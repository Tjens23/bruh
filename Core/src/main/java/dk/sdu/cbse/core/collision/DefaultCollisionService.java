package dk.sdu.cbse.core.collision;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.bullet.IBullet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the collision service.
 * This class provides basic collision detection and handling for the game.
 */
public class DefaultCollisionService implements ICollisionService {
    
    // Map of collision handlers for specific entity type pairs
    private final Map<String, Map<String, CollisionHandler>> handlers = new HashMap<>();
    
    // Whether to automatically process collisions
    private boolean autoProcess = true;
    
    // Game state tracking
    private int score = 0;
    private int lives = 3;
    
    /**
     * Creates a new collision service with default handlers.
     */
    public DefaultCollisionService() {
        // Register default collision handlers
        registerDefaultHandlers();
    }
    
    /**
     * Registers the default collision handlers.
     */
    private void registerDefaultHandlers() {
        // Player-Asteroid collision
        addCollisionHandler("player", "asteroid", (player, asteroid) -> {
            // Decrease player lives if active
            if (player.isActive() && asteroid.isActive()) {
                lives--;
                
                if (lives <= 0) {
                    player.setActive(false);
                }
                
                // Deactivate the asteroid
                asteroid.setActive(false);
                
                return new CollisionResult(player, asteroid, true);
            }
            
            return new CollisionResult(player, asteroid, false);
        });
        
        // Player-Enemy collision
        addCollisionHandler("player", "enemy", (player, enemy) -> {
            // Decrease player lives if active
            if (player.isActive() && enemy.isActive()) {
                lives--;
                
                if (lives <= 0) {
                    player.setActive(false);
                }
                
                // Deactivate the enemy
                enemy.setActive(false);
                
                // Add score for destroying enemy
                score += 150;
                
                return new CollisionResult(player, enemy, true);
            }
            
            return new CollisionResult(player, enemy, false);
        });
        
        // Projectile-Asteroid collision
        addCollisionHandler("projectile", "asteroid", (projectile, asteroid) -> {
            if (projectile.isActive() && asteroid.isActive()) {
                // Check if projectile is a bullet
                if (projectile instanceof IBullet) {
                    IBullet bullet = (IBullet) projectile;
                    
                    // Add score based on asteroid type (would check size in a real implementation)
                    score += 50;
                    
                    // Deactivate the projectile
                    projectile.setActive(false);
                    
                    // Deactivate the asteroid
                    asteroid.setActive(false);
                    
                    return new CollisionResult(projectile, asteroid, true);
                }
            }
            
            return new CollisionResult(projectile, asteroid, false);
        });
        
        // Projectile-Enemy collision
        addCollisionHandler("projectile", "enemy", (projectile, enemy) -> {
            if (projectile.isActive() && enemy.isActive()) {
                // Check if projectile is a bullet
                if (projectile instanceof IBullet) {
                    IBullet bullet = (IBullet) projectile;
                    
                    // Add score for destroying enemy
                    score += 150;
                    
                    // Deactivate the projectile
                    projectile.setActive(false);
                    
                    // Deactivate the enemy
                    enemy.setActive(false);
                    
                    return new CollisionResult(projectile, enemy, true);
                }
            }
            
            return new CollisionResult(projectile, enemy, false);
        });
        
        // Enemy-Asteroid collision
        addCollisionHandler("enemy", "asteroid", (enemy, asteroid) -> {
            // Simple collision response (just bounce off)
            if (enemy.isActive() && asteroid.isActive()) {
                // In a real physics implementation, we would calculate proper collision response
                // For simplicity, we'll just reverse velocities
                enemy.setDx(-enemy.getDx() * 0.8f);
                enemy.setDy(-enemy.getDy() * 0.8f);
                
                return new CollisionResult(enemy, asteroid, true);
            }
            
            return new CollisionResult(enemy, asteroid, false);
        });
    }
    
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
    public boolean checkCollision(Entity entity1, Entity entity2) {
        if (!entity1.isActive() || !entity2.isActive()) return false;
        
        // Calculate distance between entity centers
        float dx = entity1.getX() - entity2.getX();
        float dy = entity1.getY() - entity2.getY();
        float distanceSquared = dx * dx + dy * dy;
        
        // Calculate sum of radii
        float radiiSum = entity1.getRadius() + entity2.getRadius();
        float radiiSumSquared = radiiSum * radiiSum;
        
        // Collision occurs if distance <= sum of radii
        return distanceSquared <= radiiSumSquared;
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
        
        return null;
    }
    
    /**
     * Gets the current score.
     * 
     * @return Current score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Gets the current number of lives.
     * 
     * @return Current lives
     */
    public int getLives() {
        return lives;
    }
    
    /**
     * Sets the current score.
     * 
     * @param score Current score
     */
    public void setScore(int score) {
        this.score = score;
    }
    
    /**
     * Sets the current number of lives.
     * 
     * @param lives Current lives
     */
    public void setLives(int lives) {
        this.lives = lives;
    }
}

