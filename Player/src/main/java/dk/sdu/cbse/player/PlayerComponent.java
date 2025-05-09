package dk.sdu.cbse.player;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.bullet.IBulletService;
import dk.sdu.cbse.core.collision.ICollisionService;
import dk.sdu.cbse.core.component.GameComponent;

import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Component that manages the player's lifecycle and functionality.
 * This component coordinates the player plugin, processor, and weapon services.
 */
public class PlayerComponent extends GameComponent {
    
    private static final Logger logger = Logger.getLogger(PlayerComponent.class.getName());
    
    // Player services
    private PlayerPlugin playerPlugin;
    private PlayerProcessor playerProcessor;
    private PlayerWeaponService weaponService;
    
    // External services
    private ICollisionService collisionService;
    private IBulletService bulletService;
    
    // Player state
    private List<Entity> playerEntities;
    private boolean weaponEnabled = true;
    
    /**
     * Creates a new player component.
     */
    public PlayerComponent() {
        super("PlayerComponent");
        
        // Create internal services
        playerPlugin = new PlayerPlugin();
        playerProcessor = new PlayerProcessor();
    }
    
    @Override
    protected void doInit() {
        logger.info("Initializing player component");
        
        // Look up external services
        lookupServices();
        
        // Initialize weapon service if available
        if (weaponService == null) {
            weaponService = new PlayerWeaponService();
        }
        
        // Register with collision service if available
        if (collisionService != null) {
            registerCollisionHandlers();
        }
    }
    
    @Override
    protected void doStart() {
        logger.info("Starting player component");
        
        // Start player plugin to create entities
        playerEntities = playerPlugin.start();
        
        // Set up input handling for player processor
        // Note: In a real implementation, we would pass the scene from the game engine
        // For now, we'll leave it to be set up externally
        
        logger.info("Player component started with " + 
                   (playerEntities != null ? playerEntities.size() : 0) + " entities");
    }
    
    @Override
    protected void doStop() {
        logger.info("Stopping player component");
        
        // Stop player plugin
        playerPlugin.stop();
        
        // Clear entity references
        playerEntities = null;
    }
    
    @Override
    protected void doDispose() {
        logger.info("Disposing player component");
        
        // Clean up references
        playerPlugin = null;
        playerProcessor = null;
        weaponService = null;
        collisionService = null;
        bulletService = null;
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
            
            // Look up bullet service
            ServiceLoader<IBulletService> bulletServices = ServiceLoader.load(IBulletService.class);
            for (IBulletService service : bulletServices) {
                bulletService = service;
                logger.info("Found bullet service: " + service.getClass().getName());
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
        // collisionService.addCollisionHandler("player", "asteroid", this::handlePlayerAsteroidCollision);
    }
    
    /**
     * Gets the player plugin.
     */
    public PlayerPlugin getPlayerPlugin() {
        return playerPlugin;
    }
    
    /**
     * Gets the player processor.
     */
    public PlayerProcessor getPlayerProcessor() {
        return playerProcessor;
    }
    
    /**
     * Gets the player weapon service.
     */
    public PlayerWeaponService getWeaponService() {
        return weaponService;
    }
    
    /**
     * Enables or disables the player's weapon.
     */
    public void setWeaponEnabled(boolean enabled) {
        this.weaponEnabled = enabled;
        logger.info("Player weapon " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Checks if the player's weapon is enabled.
     */
    public boolean isWeaponEnabled() {
        return weaponEnabled;
    }
    
    /**
     * Handles shooting for the player.
     * 
     * @param playerEntity The player entity
     * @return List of created bullet entities or null if no bullets were created
     */
    public List<Entity> shoot(Entity playerEntity) {
        // Check if weapon is enabled and services are available
        if (!weaponEnabled || weaponService == null || bulletService == null) {
            return null;
        }
        
        return weaponService.shoot(playerEntity);
    }
}

