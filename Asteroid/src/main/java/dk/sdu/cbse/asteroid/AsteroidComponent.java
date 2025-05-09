package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.collision.ICollisionService;
import dk.sdu.cbse.core.component.GameComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Component that manages asteroid lifecycle and behavior.
 * This component coordinates asteroid plugin, processor, and splitting behavior.
 */
public class AsteroidComponent extends GameComponent {
    
    private static final Logger logger = Logger.getLogger(AsteroidComponent.class.getName());
    
    // Asteroid services
    private AsteroidPlugin asteroidPlugin;
    private AsteroidProcessor asteroidProcessor;
    
    // External services
    private ICollisionService collisionService;
    
    // Asteroid state
    private List<Entity> asteroidEntities;
    private final List<Asteroid> newAsteroids = new ArrayList<>();
    private int maxAsteroids = 10;
    private float spawnTimer = 0;
    private float spawnInterval = 10.0f; // seconds
    private boolean spawningEnabled = true;
    
    /**
     * Creates a new asteroid component.
     */
    public AsteroidComponent() {
        super("AsteroidComponent");
        
        // Create internal services
        asteroidPlugin = new AsteroidPlugin();
        asteroidProcessor = new AsteroidProcessor();
    }
    
    @Override
    protected void doInit() {
        logger.info("Initializing asteroid component");
        
        // Look up external services
        lookupServices();
        
        // Register with collision service if available
        if (collisionService != null) {
            registerCollisionHandlers();
        }
    }
    
    @Override
    protected void doStart() {
        logger.info("Starting asteroid component");
        
        // Start asteroid plugin to create initial entities
        asteroidEntities = asteroidPlugin.start();
        
        logger.info("Asteroid component started with " + 
                   (asteroidEntities != null ? asteroidEntities.size() : 0) + " entities");
    }
    
    @Override
    protected void doStop() {
        logger.info("Stopping asteroid component");
        
        // Stop asteroid plugin
        asteroidPlugin.stop();
        
        // Clear entity references
        asteroidEntities = null;
        newAsteroids.clear();
    }
    
    @Override
    protected void doDispose() {
        logger.info("Disposing asteroid component");
        
        // Clean up references
        asteroidPlugin = null;
        asteroidProcessor = null;
        collisionService = null;
    }
    
    /**
     * Looks up external services using ServiceLoader.
     */
    private void lookupServices() {
        try {
            // Look up collision service
            ServiceLoader<ICollisionService> collisionServices = ServiceLoader.load(ICollisionService.class);
            for (ICollisionService service : collisionServices) {
                collisionService = service;
                logger.info("Found collision service: " + service.getClass().getName());
                break;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error looking up services", e);
        }
    }
    
    /**
     * Registers collision handlers with the collision service.
     */
    private void registerCollisionHandlers() {
        // In a real implementation, we would register specific collision handlers
        // For example:
        // collisionService.addCollisionHandler("asteroid", "projectile", this::handleAsteroidProjectileCollision);
    }
    
    /**
     * Updates the asteroid spawning logic.
     * This should be called regularly to handle timed spawning.
     * 
     * @param deltaTime Time since last update in seconds
     * @param currentAsteroidCount Current number of active asteroids
     * @return List of newly spawned asteroids or null if none spawned
     */
    public List<Entity> updateSpawning(float deltaTime, int currentAsteroidCount) {
        if (!spawningEnabled) {
            return null;
        }
        
        // Update spawn timer
        spawnTimer += deltaTime;
        
        // Check if it's time to spawn and we haven't reached max asteroids
        if (spawnTimer >= spawnInterval && currentAsteroidCount < maxAsteroids) {
            spawnTimer = 0;
            
            // Create a new large asteroid
            Asteroid asteroid = createLargeAsteroid();
            
            // Return the new asteroid in a list
            return List.of(asteroid);
        }
        
        return null;
    }
    
    /**
     * Creates a new large asteroid with random properties.
     * 
     * @return The created asteroid
     */
    private Asteroid createLargeAsteroid() {
        // Create a new large asteroid
        Asteroid asteroid = new Asteroid(Asteroid.Size.LARGE);
        
        // Position the asteroid away from the center (using plugin helper)
        asteroidPlugin.positionAsteroidAwayFromCenter(asteroid);
        
        // Initialize random movement
        asteroid.initializeRandomMovement();
        
        return asteroid;
    }
    
    /**
     * Splits an asteroid into smaller pieces.
     * 
     * @param asteroid The asteroid to split
     * @return List of newly created smaller asteroids or empty list if asteroid was too small
     */
    public List<Asteroid> splitAsteroid(Asteroid asteroid) {
        if (!asteroid.isActive()) {
            return new ArrayList<>();
        }
        
        // Use the built-in splitting functionality from the Asteroid class
        List<Asteroid> splitAsteroids = asteroid.createSplitAsteroids();
        
        // Deactivate the original asteroid
        asteroid.setActive(false);
        
        // Add new asteroids to the tracking list
        newAsteroids.addAll(splitAsteroids);
        
        return splitAsteroids;
    }
    
    /**
     * Gets any new asteroids created from splits that need to be added to the game.
     * 
     * @return List of new asteroids, may be empty
     */
    public List<Asteroid> getAndClearNewAsteroids() {
        List<Asteroid> result = new ArrayList<>(newAsteroids);
        newAsteroids.clear();
        return result;
    }
    
    /**
     * Gets the asteroid plugin.
     */
    public AsteroidPlugin getAsteroidPlugin() {
        return asteroidPlugin;
    }
    
    /**
     * Gets the asteroid processor.
     */
    public AsteroidProcessor getAsteroidProcessor() {
        return asteroidProcessor;
    }
    
    /**
     * Sets the maximum number of asteroids allowed.
     * 
     * @param maxAsteroids Maximum number of asteroids
     */
    public void setMaxAsteroids(int maxAsteroids) {
        this.maxAsteroids = maxAsteroids;
    }
    
    /**
     * Gets the maximum number of asteroids allowed.
     * 
     * @return Maximum number of asteroids
     */
    public int getMaxAsteroids() {
        return maxAsteroids;
    }
    
    /**
     * Sets the spawn interval in seconds.
     * 
     * @param spawnInterval Spawn interval in seconds
     */
    public void setSpawnInterval(float spawnInterval) {
        this.spawnInterval = spawnInterval;
    }
    
    /**
     * Gets the spawn interval in seconds.
     * 
     * @return Spawn interval in seconds
     */
    public float getSpawnInterval() {
        return spawnInterval;
    }
    
    /**
     * Enables or disables asteroid spawning.
     * 
     * @param enabled True to enable spawning, false to disable
     */
    public void setSpawningEnabled(boolean enabled) {
        this.spawningEnabled = enabled;
        logger.info("Asteroid spawning " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Checks if asteroid spawning is enabled.
     * 
     * @return True if spawning is enabled, false otherwise
     */
    public boolean isSpawningEnabled() {
        return spawningEnabled;
    }
}

