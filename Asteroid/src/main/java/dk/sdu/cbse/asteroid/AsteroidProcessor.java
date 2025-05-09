package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.IEntityProcessorService;

import java.util.ArrayList;
import java.util.List;

/**
 * Processor that handles asteroid movement and behavior.
 */
public class AsteroidProcessor implements IEntityProcessorService {

    // Game window dimensions - these should match the Core module
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    
    // List to hold new asteroids created from splits
    private final List<Asteroid> newAsteroids = new ArrayList<>();
    
    @Override
    public void process(List<Entity> entities, float deltaTime) {
        // Clear new asteroids list
        newAsteroids.clear();
        
        // Process all asteroid entities
        for (Entity entity : entities) {
            if (entity instanceof Asteroid && entity.isActive()) {
                Asteroid asteroid = (Asteroid) entity;
                
                // Update rotation
                asteroid.updateRotation(deltaTime);
                
                // Update position
                asteroid.setX(asteroid.getX() + asteroid.getDx() * deltaTime);
                asteroid.setY(asteroid.getY() + asteroid.getDy() * deltaTime);
                
                // Apply screen wrapping
                wrapPosition(asteroid);
                
                // Check for collisions with other entities
                // Note: Actual collision handling would be done in a post-processor
                // or by publishing collision events
            }
        }
        
        // Add any new asteroids created from splits directly to the entities list
        if (!newAsteroids.isEmpty()) {
            entities.addAll(newAsteroids);
        }
    }
    
    /**
     * Wraps asteroid position around screen edges.
     */
    private void wrapPosition(Asteroid asteroid) {
        // Wrap horizontally
        if (asteroid.getX() < -asteroid.getRadius()) {
            asteroid.setX(GAME_WIDTH + asteroid.getRadius());
        } else if (asteroid.getX() > GAME_WIDTH + asteroid.getRadius()) {
            asteroid.setX(-asteroid.getRadius());
        }
        
        // Wrap vertically
        if (asteroid.getY() < -asteroid.getRadius()) {
            asteroid.setY(GAME_HEIGHT + asteroid.getRadius());
        } else if (asteroid.getY() > GAME_HEIGHT + asteroid.getRadius()) {
            asteroid.setY(-asteroid.getRadius());
        }
    }
    
    /**
     * Splits an asteroid into smaller pieces.
     * @param asteroid The asteroid to split
     * @return True if the asteroid was split, false if it was too small
     */
    public boolean splitAsteroid(Asteroid asteroid) {
        if (!asteroid.isActive()) return false;
        
        // Create smaller asteroids
        List<Asteroid> splitAsteroids = asteroid.createSplitAsteroids();
        
        // If no smaller asteroids were created, the asteroid was too small to split
        if (splitAsteroids.isEmpty()) {
            return false;
        }
        
        // Add the new asteroids to be processed
        newAsteroids.addAll(splitAsteroids);
        
        // Deactivate the original asteroid
        asteroid.setActive(false);
        
        return true;
    }
    
    /**
     * Checks if there is a collision between two entities using circle collision detection.
     */
    public static boolean checkCollision(Entity a, Entity b) {
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
}

