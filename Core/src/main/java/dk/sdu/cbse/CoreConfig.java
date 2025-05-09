package dk.sdu.cbse;

import dk.sdu.cbse.core.CollisionProcessor;
import dk.sdu.cbse.core.IEntityProcessorService;
import dk.sdu.cbse.core.IGamePluginService;
import dk.sdu.cbse.core.IPostEntityProcessorService;
import dk.sdu.cbse.core.collision.ICollisionService;
import dk.sdu.cbse.core.component.IComponentService;
import dk.sdu.cbse.core.score.IScoreService;
import dk.sdu.cbse.core.score.RestScoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Spring configuration class for the Core module.
 * Replaces the ServiceLoader mechanism with Spring's dependency injection.
 */
@Configuration
@ComponentScan(basePackages = {
    "dk.sdu.cbse",              // Main package
    "dk.sdu.cbse.core",         // Core interfaces and classes
    "dk.sdu.cbse.core.bullet",  // Bullet interfaces
    "dk.sdu.cbse.core.weapon",  // Weapon interfaces
    "dk.sdu.cbse.core.collision", // Collision interfaces
    "dk.sdu.cbse.core.component"  // Component interfaces
})
public class CoreConfig {
    
    private static final Logger logger = Logger.getLogger(CoreConfig.class.getName());
    
    /**
     * Creates the CollisionProcessor bean.
     * This is required as a fallback for collision detection if no other
     * ICollisionService implementation is found.
     */
    @Bean
    public CollisionProcessor collisionProcessor() {
        logger.info("Creating CollisionProcessor bean");
        return new CollisionProcessor();
    }
    
    /**
     * Creates an empty list of entity processors if none are found.
     * This ensures that autowired lists are never null.
     */
    @Bean
    public List<IEntityProcessorService> entityProcessorServices() {
        return new ArrayList<>();
    }
    
    /**
     * Creates an empty list of game plugins if none are found.
     * This ensures that autowired lists are never null.
     */
    @Bean
    public List<IGamePluginService> gamePluginServices() {
        return new ArrayList<>();
    }
    
    /**
     * Creates an empty list of post entity processors if none are found.
     * This ensures that autowired lists are never null.
     */
    @Bean
    public List<IPostEntityProcessorService> postEntityProcessorServices() {
        return new ArrayList<>();
    }
    
    /**
     * Creates an empty list of component services if none are found.
     * This ensures that autowired lists are never null.
     */
    @Bean
    public List<IComponentService> componentServices() {
        return new ArrayList<>();
    }
    
    /**
     * Creates the RestScoreService bean.
     * This is required as a fallback for score service if no other
     * IScoreService implementation is found.
     */
    @Bean
    public IScoreService scoreService() {
        logger.info("Creating RestScoreService bean");
        return new RestScoreService();
    }
}

