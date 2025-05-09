package dk.sdu.cbse.enemy;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.IGamePluginService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Plugin that creates and manages enemy entities.
 */
public class EnemyPlugin implements IGamePluginService {

    private final List<Enemy> enemies = new ArrayList<>();
    private final Random random = new Random();
    
    // Game window dimensions - these should match the Core module
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    
    // Configuration
    private static final int INITIAL_ENEMY_COUNT = 3;
    private static final int ENEMY_SPAWN_MARGIN = 50; // Margin from the edge where enemies spawn

    @Override
    public List<Entity> start() {
        // Create enemies
        for (int i = 0; i < INITIAL_ENEMY_COUNT; i++) {
            Enemy enemy = createEnemy();
            enemies.add(enemy);
        }
        
        // Convert to list of entities and return
        List<Entity> entities = new ArrayList<>(enemies);
        return entities;
    }

    @Override
    public void stop() {
        // Deactivate all enemies
        for (Enemy enemy : enemies) {
            enemy.setActive(false);
        }
    }
    
    /**
     * Creates a new enemy positioned randomly around the edge of the screen.
     */
    public Enemy createEnemy() {
        Enemy enemy = new Enemy();
        
        // Choose which edge to spawn on (0=top, 1=right, 2=bottom, 3=left)
        int edge = random.nextInt(4);
        
        switch (edge) {
            case 0: // Top edge
                enemy.setX(random.nextInt(GAME_WIDTH));
                enemy.setY(ENEMY_SPAWN_MARGIN);
                break;
            case 1: // Right edge
                enemy.setX(GAME_WIDTH - ENEMY_SPAWN_MARGIN);
                enemy.setY(random.nextInt(GAME_HEIGHT));
                break;
            case 2: // Bottom edge
                enemy.setX(random.nextInt(GAME_WIDTH));
                enemy.setY(GAME_HEIGHT - ENEMY_SPAWN_MARGIN);
                break;
            case 3: // Left edge
                enemy.setX(ENEMY_SPAWN_MARGIN);
                enemy.setY(random.nextInt(GAME_HEIGHT));
                break;
        }
        
        // Set initial velocity and rotation
        enemy.setDx(0);
        enemy.setDy(0);
        enemy.setRadians(random.nextFloat() * (float) (Math.PI * 2));
        
        // Start with random direction for wandering behavior
        enemy.setRandomTargetDirection();
        
        return enemy;
    }
}

