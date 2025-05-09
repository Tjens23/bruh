package dk.sdu.cbse.player;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.IEntityProcessorService;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processor responsible for handling player movement and controls.
 */
public class PlayerProcessor implements IEntityProcessorService {

    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    
    // Keep track of which keys are currently pressed
    private final Map<KeyCode, Boolean> keyState = new HashMap<>();
    
    private Scene gameScene;
    
    public PlayerProcessor() {
        // Default initializations
    }
    
    /**
     * Sets up input handling for the player.
     * This should be called once the JavaFX scene is available.
     */
    public void setupInput(Scene scene) {
        this.gameScene = scene;
        
        // Initialize key state map with default values
        keyState.put(KeyCode.W, false);
        keyState.put(KeyCode.A, false);
        keyState.put(KeyCode.D, false);
        keyState.put(KeyCode.UP, false);
        keyState.put(KeyCode.LEFT, false);
        keyState.put(KeyCode.RIGHT, false);
        
        // Set up key press events
        scene.setOnKeyPressed(event -> {
            keyState.put(event.getCode(), true);
            updatePlayerInput();
        });
        
        // Set up key release events
        scene.setOnKeyReleased(event -> {
            keyState.put(event.getCode(), false);
            updatePlayerInput();
        });
    }
    
    /**
     * Updates player input state based on currently pressed keys.
     */
    private void updatePlayerInput() {
        if (gameScene == null) return;
        
        // Get entities from scene properties
        Object entitiesObj = gameScene.getRoot().getProperties().get("entities");
        if (entitiesObj == null || !(entitiesObj instanceof List<?>)) {
            return; // No entities found or not a list
        }
        
        // Try to cast to list of entities
        @SuppressWarnings("unchecked")
        List<Entity> entities = (List<Entity>) entitiesObj;
        
        // Find player entities and update their input state
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                
                // Check acceleration keys (W or UP)
                boolean accelerating = isKeyPressed(KeyCode.W) || isKeyPressed(KeyCode.UP);
                player.setAccelerating(accelerating);
                
                // Check rotation keys
                boolean rotatingLeft = isKeyPressed(KeyCode.A) || isKeyPressed(KeyCode.LEFT);
                boolean rotatingRight = isKeyPressed(KeyCode.D) || isKeyPressed(KeyCode.RIGHT);
                player.setRotatingLeft(rotatingLeft);
                player.setRotatingRight(rotatingRight);
            }
        }
    }
    
    /**
     * Checks if a key is currently pressed.
     */
    private boolean isKeyPressed(KeyCode code) {
        Boolean state = keyState.get(code);
        return state != null && state;
    }

    @Override
    public void process(List<Entity> entities, float deltaTime) {
        // Process only player entities
        for (Entity entity : entities) {
            if (entity instanceof Player && entity.isActive()) {
                Player player = (Player) entity;
                
                // Process player physics
                processPlayerMovement(player, deltaTime);
            }
        }
    }
    
    /**
     * Updates player movement based on input and physics.
     */
    private void processPlayerMovement(Player player, float deltaTime) {
        // Handle rotation
        if (player.isRotatingLeft()) {
            player.setRadians(player.getRadians() + Player.ROTATION_SPEED * deltaTime);
        }
        if (player.isRotatingRight()) {
            player.setRadians(player.getRadians() - Player.ROTATION_SPEED * deltaTime);
        }
        
        // Handle acceleration
        if (player.isAccelerating()) {
            // Calculate acceleration vector based on player's rotation
            float accelerationX = (float) Math.cos(player.getRadians()) * Player.ACCELERATION * deltaTime;
            float accelerationY = (float) Math.sin(player.getRadians()) * Player.ACCELERATION * deltaTime;
            
            // Apply acceleration to velocity
            player.setDx(player.getDx() + accelerationX);
            player.setDy(player.getDy() + accelerationY);
            
            // Limit speed
            limitSpeed(player);
        }
        
        // Apply deceleration
        player.setDx(player.getDx() * Player.DECELERATION);
        player.setDy(player.getDy() * Player.DECELERATION);
        
        // Update position
        player.setX(player.getX() + player.getDx() * deltaTime);
        player.setY(player.getY() + player.getDy() * deltaTime);
        
        // Wrap around screen edges
        wrapPosition(player);
    }
    
    /**
     * Limits the player's speed to MAX_SPEED.
     */
    private void limitSpeed(Player player) {
        float speedSquared = player.getDx() * player.getDx() + player.getDy() * player.getDy();
        
        if (speedSquared > Player.MAX_SPEED * Player.MAX_SPEED) {
            float speedFactor = Player.MAX_SPEED / (float) Math.sqrt(speedSquared);
            player.setDx(player.getDx() * speedFactor);
            player.setDy(player.getDy() * speedFactor);
        }
    }
    
    /**
     * Wraps player position around screen edges.
     */
    private void wrapPosition(Player player) {
        // Wrap horizontally
        if (player.getX() < 0) {
            player.setX(GAME_WIDTH);
        } else if (player.getX() > GAME_WIDTH) {
            player.setX(0);
        }
        
        // Wrap vertically
        if (player.getY() < 0) {
            player.setY(GAME_HEIGHT);
        } else if (player.getY() > GAME_HEIGHT) {
            player.setY(0);
        }
    }
}

