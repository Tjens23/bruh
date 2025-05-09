package dk.sdu.cbse.enemy;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.IEntityProcessorService;

import java.util.List;

/**
 * Processor that handles enemy AI behavior and movement.
 */
public class EnemyProcessor implements IEntityProcessorService {

    // Game window dimensions - these should match the Core module
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    
    @Override
    public void process(List<Entity> entities, float deltaTime) {
        // Find the player entity (if one exists)
        Entity playerEntity = findPlayerEntity(entities);
        
        // Process all enemy entities
        for (Entity entity : entities) {
            if (entity instanceof Enemy && entity.isActive()) {
                Enemy enemy = (Enemy) entity;
                
                // Update behavior based on player position
                if (playerEntity != null) {
                    updateEnemyBehavior(enemy, playerEntity, deltaTime);
                } else {
                    // No player found, just wander
                    enemy.setCurrentState(Enemy.BehaviorState.WANDERING);
                    enemy.updateBehavior(deltaTime);
                }
                
                // Process movement based on current state
                processEnemyMovement(enemy, deltaTime);
                
                // Apply screen wrapping
                wrapPosition(enemy);
            }
        }
    }
    
    /**
     * Finds and returns the player entity, or null if not found.
     */
    private Entity findPlayerEntity(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity.isActive() && "player".equals(entity.getType())) {
                return entity;
            }
        }
        return null;
    }
    
    /**
     * Updates enemy behavior based on player position.
     */
    private void updateEnemyBehavior(Enemy enemy, Entity player, float deltaTime) {
        // Update target tracking
        enemy.updateTarget(player.getX(), player.getY());
        
        // Update behavior timer
        enemy.updateBehavior(deltaTime);
        
        // If in hunting state, rotate toward player
        if (enemy.getCurrentState() == Enemy.BehaviorState.HUNTING) {
            enemy.rotateTowardTarget(deltaTime);
        }
    }
    
    /**
     * Processes enemy movement based on its current state.
     */
    private void processEnemyMovement(Enemy enemy, float deltaTime) {
        // Different movement processing based on behavior state
        switch (enemy.getCurrentState()) {
            case HUNTING:
                // Move toward target
                processHuntingMovement(enemy, deltaTime);
                break;
                
            case WANDERING:
                // Move in current direction with random changes
                processWanderingMovement(enemy, deltaTime);
                break;
                
            case FLEEING:
                // Move away from target (not implemented in this basic version)
                processWanderingMovement(enemy, deltaTime); // Default to wandering
                break;
        }
        
        // Apply velocity to position
        enemy.setX(enemy.getX() + enemy.getDx() * deltaTime);
        enemy.setY(enemy.getY() + enemy.getDy() * deltaTime);
    }
    
    /**
     * Processes movement for hunting behavior state.
     */
    private void processHuntingMovement(Enemy enemy, float deltaTime) {
        // Accelerate in the direction the enemy is facing
        float accelerationX = (float) Math.cos(enemy.getRadians()) * Enemy.ACCELERATION * deltaTime;
        float accelerationY = (float) Math.sin(enemy.getRadians()) * Enemy.ACCELERATION * deltaTime;
        
        // Apply acceleration to velocity
        enemy.setDx(enemy.getDx() + accelerationX);
        enemy.setDy(enemy.getDy() + accelerationY);
        
        // Limit speed
        limitSpeed(enemy);
    }
    
    /**
     * Processes movement for wandering behavior state.
     */
    private void processWanderingMovement(Enemy enemy, float deltaTime) {
        // Move forward at a slower pace
        float accelerationX = (float) Math.cos(enemy.getRadians()) * (Enemy.ACCELERATION * 0.5f) * deltaTime;
        float accelerationY = (float) Math.sin(enemy.getRadians()) * (Enemy.ACCELERATION * 0.5f) * deltaTime;
        
        // Apply acceleration to velocity
        enemy.setDx(enemy.getDx() + accelerationX);
        enemy.setDy(enemy.getDy() + accelerationY);
        
        // Apply stronger deceleration when wandering for more controlled movement
        enemy.setDx(enemy.getDx() * Enemy.DECELERATION);
        enemy.setDy(enemy.getDy() * Enemy.DECELERATION);
        
        // Limit speed to a lower value when wandering
        limitSpeed(enemy, Enemy.MAX_SPEED * 0.7f);
    }
    
    /**
     * Limits the enemy's speed to MAX_SPEED.
     */
    private void limitSpeed(Enemy enemy) {
        limitSpeed(enemy, Enemy.MAX_SPEED);
    }
    
    /**
     * Limits the enemy's speed to the specified maximum.
     */
    private void limitSpeed(Enemy enemy, float maxSpeed) {
        float speedSquared = enemy.getDx() * enemy.getDx() + enemy.getDy() * enemy.getDy();
        
        if (speedSquared > maxSpeed * maxSpeed) {
            float speedFactor = maxSpeed / (float) Math.sqrt(speedSquared);
            enemy.setDx(enemy.getDx() * speedFactor);
            enemy.setDy(enemy.getDy() * speedFactor);
        }
    }
    
    /**
     * Wraps enemy position around screen edges.
     */
    private void wrapPosition(Enemy enemy) {
        // Wrap horizontally
        if (enemy.getX() < 0) {
            enemy.setX(GAME_WIDTH);
        } else if (enemy.getX() > GAME_WIDTH) {
            enemy.setX(0);
        }
        
        // Wrap vertically
        if (enemy.getY() < 0) {
            enemy.setY(GAME_HEIGHT);
        } else if (enemy.getY() > GAME_HEIGHT) {
            enemy.setY(0);
        }
    }
}

