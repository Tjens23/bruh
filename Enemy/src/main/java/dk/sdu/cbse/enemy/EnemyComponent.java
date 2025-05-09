package dk.sdu.cbse.enemy;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.collision.ICollisionService;
import dk.sdu.cbse.core.component.GameComponent;

import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Component that manages enemy lifecycle and behavior.
 * This component coordinates enemy plugin, processor, and AI behavior.
 */
public class EnemyComponent extends GameComponent {
    
    private static final Logger logger = Logger.getLogger(EnemyComponent.class.getName());
    
    // Enemy services
    private EnemyPlugin enemyPlugin;
    private EnemyProcessor enemyProcessor;
    
    // External services
    private ICollisionService collisionService;
    
    // Enemy state
    private List<Entity> enemyEntities;
    private int maxEnemies = 5;
    private float spawnTimer = 0;
    private float spawnInterval = 5.0f; // seconds
    private boolean spawningEnabled = true;
    
    /**
     * Creates a new enemy component.
     */
    public EnemyComponent() {
        super("EnemyComponent");
        
        // Create internal services
        enemyPlugin = new EnemyPlugin();
        enemyProcessor = new EnemyProcessor();
    }
    
    @Override
    protected void doInit() {
        logger.info("Initializing enemy component");
        
        // Look up external services
        lookupServices();
        
        // Register with collision service if available
        if (collisionService != null) {
            registerCollisionHandlers();
        }
    }
    
    @Override
    protected void doStart() {
        logger.info("Starting enemy component");
        
        // Start enemy plugin to create initial entities
        enemyEntities = enemyPlugin.start();
        
        logger.info("Enemy component started with " + 
                   (enemyEntities != null ? enemyEntities.size() : 0) + " entities");
    }
    
    @Override
    protected void doStop() {
        logger.info("Stopping enemy component");
        
        // Stop enemy plugin
        enemyPlugin.stop();
        
        // Clear entity references
        enemyEntities = null;
    }
    
    @Override
    protected void doDispose() {
        logger.info("Disposing enemy component");
        
        // Clean up references
        enemyPlugin = null;
        enemyProcessor = null;
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
        // collisionService.addCollisionHandler("enemy", "projectile", this::handleEnemyProjectileCollision);
    }
    
    /**
     * Updates the enemy spawning logic.
     * This should be called regularly to handle timed spawning.
     * 
     * @param deltaTime Time since last update in seconds
     * @param currentEnemyCount Current number of active enemies
     * @return List of newly spawned enemies or null if none spawned
     */
    public List<Entity> updateSpawning(float deltaTime, int currentEnemyCount) {
        if (!spawningEnabled) {
            return null;
        }
        
        // Update spawn timer
        spawnTimer += deltaTime;
        
        // Check if it's time to spawn and we haven't reached max enemies
        if (spawnTimer >= spawnInterval && currentEnemyCount < maxEnemies) {
            spawnTimer = 0;
            
            // Create a new enemy
            Enemy enemy = createEnemy();
            
            // Return the new enemy in a list
            return List.of(enemy);
        }
        
        return null;
    }
    
    /**
     * Creates a new enemy with random properties.
     * 
     * @return The created enemy
     */
    private Enemy createEnemy() {
        // Use the plugin to create a new enemy
        Enemy enemy = (Enemy) enemyPlugin.createEnemy();
        
        // Additional enemy setup could be done here
        
        return enemy;
    }
    
    /**
     * Gets the enemy plugin.
     */
    public EnemyPlugin getEnemyPlugin() {
        return enemyPlugin;
    }
    
    /**
     * Gets the enemy processor.
     */
    public EnemyProcessor getEnemyProcessor() {
        return enemyProcessor;
    }
    
    /**
     * Sets the maximum number of enemies allowed.
     * 
     * @param maxEnemies Maximum number of enemies
     */
    public void setMaxEnemies(int maxEnemies) {
        this.maxEnemies = maxEnemies;
    }
    
    /**
     * Gets the maximum number of enemies allowed.
     * 
     * @return Maximum number of enemies
     */
    public int getMaxEnemies() {
        return maxEnemies;
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
     * Enables or disables enemy spawning.
     * 
     * @param enabled True to enable spawning, false to disable
     */
    public void setSpawningEnabled(boolean enabled) {
        this.spawningEnabled = enabled;
        logger.info("Enemy spawning " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Checks if enemy spawning is enabled.
     * 
     * @return True if spawning is enabled, false otherwise
     */
    public boolean isSpawningEnabled() {
        return spawningEnabled;
    }
}

