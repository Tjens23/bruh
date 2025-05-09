package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.IGamePluginService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Plugin that creates and manages asteroids in the game.
 */
public class AsteroidPlugin implements IGamePluginService {

    private final List<Asteroid> asteroids = new ArrayList<>();
    private final Random random = new Random();
    
    // Game window dimensions - these should match the Core module
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    
    // Configuration
    private static final int INITIAL_ASTEROID_COUNT = 4;
    private static final int SAFE_ZONE_RADIUS = 150; // Radius around center where asteroids won't spawn
    
    @Override
    public List<Entity> start() {
        // Create initial asteroids
        for (int i = 0; i < INITIAL_ASTEROID_COUNT; i++) {
            Asteroid asteroid = createAsteroid();
            asteroids.add(asteroid);
        }
        
        // Convert to list of entities and return
        List<Entity> entities = new ArrayList<>(asteroids);
        return entities;
    }

    @Override
    public void stop() {
        // Deactivate all asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.setActive(false);
        }
    }
    
    /**
     * Creates a new asteroid with random properties and position.
     * The asteroid is positioned away from the center of the screen.
     */
    private Asteroid createAsteroid() {
        // Create large asteroid
        Asteroid asteroid = new Asteroid(Asteroid.Size.LARGE);
        
        // Position the asteroid away from the center (player spawn area)
        positionAsteroidAwayFromCenter(asteroid);
        
        return asteroid;
    }
    
    /**
     * Positions an asteroid away from the center of the screen.
     */
    public void positionAsteroidAwayFromCenter(Asteroid asteroid) {
        float x, y;
        float centerX = GAME_WIDTH / 2.0f;
        float centerY = GAME_HEIGHT / 2.0f;
        
        // Keep generating positions until we find one outside the safe zone
        do {
            // Pick a random position on the screen
            x = random.nextFloat() * GAME_WIDTH;
            y = random.nextFloat() * GAME_HEIGHT;
            
            // Calculate distance from center
            float dx = x - centerX;
            float dy = y - centerY;
            float distanceSquared = dx * dx + dy * dy;
            
            // If it's far enough from center, we can use this position
            if (distanceSquared >= SAFE_ZONE_RADIUS * SAFE_ZONE_RADIUS) {
                asteroid.setX(x);
                asteroid.setY(y);
                return;
            }
        } while (true);
    }
    
    /**
     * Adds a new asteroid to the game.
     * This can be called when an existing asteroid is split.
     */
    public void addAsteroid(Asteroid asteroid) {
        if (asteroid != null) {
            asteroids.add(asteroid);
        }
    }
}

