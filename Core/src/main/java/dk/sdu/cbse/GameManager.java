package dk.sdu.cbse;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.IEntityProcessorService;
import dk.sdu.cbse.core.IGamePluginService;
import dk.sdu.cbse.core.IPostEntityProcessorService;
import dk.sdu.cbse.core.collision.ICollisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spring-managed Game Manager component that centralizes Spring dependencies.
 * This class is responsible for managing game entities and processing game logic.
 */
@Component
public class GameManager {
    private static final Logger logger = Logger.getLogger(GameManager.class.getName());
    
    // Game entities
    private final List<Entity> entities = new ArrayList<>();
    
    // Spring-injected services
    @Autowired
    private List<IEntityProcessorService> entityProcessors;
    
    @Autowired
    private List<IGamePluginService> gamePlugins;
    
    @Autowired
    private List<IPostEntityProcessorService> postEntityProcessors;
    
    @Autowired
    private ICollisionService collisionService;
    
    /**
     * Creates a new GameManager instance.
     */
    public GameManager() {
        logger.info("GameManager created");
        
        // Initialize lists to avoid NullPointerException when not using Spring
        if (entityProcessors == null) {
            entityProcessors = new ArrayList<>();
        }
        if (gamePlugins == null) {
            gamePlugins = new ArrayList<>();
        }
        if (postEntityProcessors == null) {
            postEntityProcessors = new ArrayList<>();
        }
    }
    
    /**
     * Initializes the game by starting all game plugins.
     */
    public void initialize() {
        logger.info("Initializing GameManager");
        
        // Start all game plugins and collect entities
        for (IGamePluginService plugin : gamePlugins) {
            try {
                logger.info("Starting game plugin: " + plugin.getClass().getSimpleName());
                List<Entity> pluginEntities = plugin.start();
                if (pluginEntities != null) {
                    entities.addAll(pluginEntities);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error starting game plugin: " + plugin.getClass().getSimpleName(), e);
            }
        }
        
        logger.info("GameManager initialized with " + entities.size() + " entities");
    }
    
    /**
     * Updates the game state based on elapsed time.
     * 
     * @param deltaTime Time passed since last update in seconds
     */
    public void update(float deltaTime) {
        // Process all entities
        for (IEntityProcessorService processor : entityProcessors) {
            try {
                processor.process(entities, deltaTime);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in entity processor: " + processor.getClass().getSimpleName(), e);
            }
        }
        
        // Post-process entities (including collision detection)
        for (IPostEntityProcessorService postProcessor : postEntityProcessors) {
            try {
                postProcessor.postProcess(entities, deltaTime);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in post processor: " + postProcessor.getClass().getSimpleName(), e);
            }
        }
    }
    
    /**
     * Adds a new entity to the game.
     * 
     * @param entity The entity to add
     */
    public void addEntity(Entity entity) {
        if (entity != null) {
            entities.add(entity);
        }
    }
    
    /**
     * Gets the current list of game entities.
     * 
     * @return The current list of entities
     */
    public List<Entity> getEntities() {
        return entities;
    }
    
    /**
     * Stops the game and cleans up resources.
     */
    public void shutdown() {
        logger.info("Stopping GameManager");
        
        // Stop all game plugins
        for (IGamePluginService plugin : gamePlugins) {
            try {
                logger.info("Stopping game plugin: " + plugin.getClass().getSimpleName());
                plugin.stop();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error stopping game plugin: " + plugin.getClass().getSimpleName(), e);
            }
        }
        
        // Clear all entities
        entities.clear();
        
        logger.info("GameManager stopped");
    }
    
    /**
     * Gets the collision service.
     * 
     * @return The collision service
     */
    public ICollisionService getCollisionService() {
        return collisionService;
    }
    
    /**
     * Gets the entity processors.
     * 
     * @return The entity processors (never null)
     */
    public List<IEntityProcessorService> getEntityProcessors() {
        if (entityProcessors == null) {
            entityProcessors = new ArrayList<>();
        }
        return entityProcessors;
    }
    
    /**
     * Gets the game plugins.
     * 
     * @return The game plugins (never null)
     */
    public List<IGamePluginService> getGamePlugins() {
        if (gamePlugins == null) {
            gamePlugins = new ArrayList<>();
        }
        return gamePlugins;
    }
    
    /**
     * Gets the post entity processors.
     * 
     * @return The post entity processors (never null)
     */
    public List<IPostEntityProcessorService> getPostEntityProcessors() {
        if (postEntityProcessors == null) {
            postEntityProcessors = new ArrayList<>();
        }
        return postEntityProcessors;
    }
}

