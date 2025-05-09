package dk.sdu.cbse.player;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.IGamePluginService;

import java.util.ArrayList;
import java.util.List;

/**
 * Plugin that creates and manages the player entity.
 */
public class PlayerPlugin implements IGamePluginService {

    private Player player;
    
    // Game window dimensions - these should match the Core module
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;

    @Override
    public List<Entity> start() {
        // Create player entity
        player = createPlayerShip();
        
        // Add to game world
        List<Entity> entities = new ArrayList<>();
        entities.add(player);
        
        return entities;
    }

    @Override
    public void stop() {
        // Set player inactive when plugin stops
        if (player != null) {
            player.setActive(false);
        }
    }
    
    /**
     * Creates and initializes a player ship entity.
     */
    private Player createPlayerShip() {
        Player player = new Player();
        
        // Position at center of screen
        player.setX(GAME_WIDTH / 2);
        player.setY(GAME_HEIGHT / 2);
        
        // Initial rotation and velocity
        player.setRadians(3.1415f / 2); // Facing up
        player.setDx(0);
        player.setDy(0);
        
        return player;
    }
}

