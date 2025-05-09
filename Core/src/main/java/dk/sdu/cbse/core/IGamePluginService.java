package dk.sdu.cbse.core;

import java.util.List;

/**
 * Service interface for game plugins.
 * Plugins are responsible for initializing and cleaning up game entities.
 */
public interface IGamePluginService {
    
    /**
     * Starts the plugin and returns any entities created.
     * This method is called when the plugin is loaded.
     * 
     * @return A list of entities created by this plugin
     */
    List<Entity> start();
    
    /**
     * Stops the plugin and performs cleanup.
     * This method is called when the plugin is unloaded.
     */
    void stop();
}

