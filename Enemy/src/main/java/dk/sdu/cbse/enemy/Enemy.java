package dk.sdu.cbse.enemy;

import dk.sdu.cbse.core.Entity;
import java.util.Random;

/**
 * Enemy entity with AI behavior.
 */
public class Enemy extends Entity {
    
    // AI behavior states
    public enum BehaviorState {
        HUNTING,    // Actively pursuing player
        WANDERING,  // Moving randomly
        FLEEING     // Moving away from player
    }
    
    // Movement constants
    public static final float MAX_SPEED = 150.0f;
    public static final float ACCELERATION = 120.0f;
    public static final float ROTATION_SPEED = 2.0f;
    public static final float DECELERATION = 0.98f;
    
    // AI properties
    private BehaviorState currentState;
    private float targetX; // Target X position (usually player)
    private float targetY; // Target Y position (usually player)
    private boolean hasTarget;
    private float behaviorTimer; // Used for changing behaviors
    private int health;
    private Random random;
    
    // AI configuration
    private static final float BEHAVIOR_CHANGE_TIME = 3.0f; // Seconds between behavior changes
    private static final float TRACKING_RANGE = 250.0f; // Distance at which enemy can track player
    
    /**
     * Creates a new enemy entity.
     */
    public Enemy() {
        setType("enemy");
        setRadius(12);
        health = 3;
        random = new Random();
        
        // Start with wandering behavior
        currentState = BehaviorState.WANDERING;
        hasTarget = false;
        behaviorTimer = 0;
    }
    
    /**
     * Updates the behavior timer and possibly changes behavior.
     * @param deltaTime Time since last update
     */
    public void updateBehavior(float deltaTime) {
        behaviorTimer += deltaTime;
        
        // Randomly change behavior when timer expires (only if wandering)
        if (behaviorTimer >= BEHAVIOR_CHANGE_TIME && currentState == BehaviorState.WANDERING) {
            behaviorTimer = 0;
            
            // 30% chance to change direction when wandering
            if (random.nextFloat() < 0.3f) {
                setRandomTargetDirection();
            }
        }
    }
    
    /**
     * Sets a random direction for wandering behavior.
     */
    public void setRandomTargetDirection() {
        // Set random rotation
        setRadians(random.nextFloat() * (float) (Math.PI * 2));
    }
    
    /**
     * Updates the enemy's target (usually the player).
     * @param playerX Player's X position
     * @param playerY Player's Y position
     */
    public void updateTarget(float playerX, float playerY) {
        targetX = playerX;
        targetY = playerY;
        
        // Calculate distance to player
        float dx = playerX - getX();
        float dy = playerY - getY();
        float distanceSquared = dx * dx + dy * dy;
        
        // Determine if player is in tracking range
        if (distanceSquared <= TRACKING_RANGE * TRACKING_RANGE) {
            hasTarget = true;
            currentState = BehaviorState.HUNTING;
        } else {
            hasTarget = false;
            currentState = BehaviorState.WANDERING;
        }
    }
    
    /**
     * Rotates the enemy toward the target.
     * @param deltaTime Time since last update
     */
    public void rotateTowardTarget(float deltaTime) {
        if (!hasTarget) return;
        
        // Calculate angle to target
        float dx = targetX - getX();
        float dy = targetY - getY();
        float targetAngle = (float) Math.atan2(dy, dx);
        
        // Determine shortest rotation direction
        float angleDiff = targetAngle - getRadians();
        
        // Normalize angle difference to [-PI, PI]
        while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
        while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;
        
        // Rotate toward target
        if (angleDiff > 0) {
            setRadians(getRadians() + Math.min(ROTATION_SPEED * deltaTime, angleDiff));
        } else {
            setRadians(getRadians() + Math.max(-ROTATION_SPEED * deltaTime, angleDiff));
        }
    }
    
    public BehaviorState getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(BehaviorState state) {
        this.currentState = state;
    }
    
    public boolean hasTarget() {
        return hasTarget;
    }
    
    public float getTargetX() {
        return targetX;
    }
    
    public float getTargetY() {
        return targetY;
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
        if (this.health <= 0) {
            setActive(false);
        }
    }
    
    public void damage(int amount) {
        health -= amount;
        if (health <= 0) {
            setActive(false);
        }
    }
}

