package dk.sdu.cbse.player;

import dk.sdu.cbse.core.Entity;

/**
 * Player entity class representing the player's ship.
 */
public class Player extends Entity {
    
    // Player properties
    private int lives;
    private int score;
    
    // Movement constants
    public static final float ACCELERATION = 200.0f;
    public static final float ROTATION_SPEED = 3.0f;
    public static final float MAX_SPEED = 300.0f;
    public static final float DECELERATION = 0.97f;
    
    // Player state
    private boolean isAccelerating;
    private boolean isRotatingLeft;
    private boolean isRotatingRight;
    
    /**
     * Creates a new player entity.
     */
    public Player() {
        setType("player");
        setRadius(8);
        lives = 3;
        score = 0;
    }
    
    public int getLives() {
        return lives;
    }
    
    public void setLives(int lives) {
        this.lives = lives;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public void addScore(int points) {
        this.score += points;
    }
    
    public boolean isAccelerating() {
        return isAccelerating;
    }
    
    public void setAccelerating(boolean accelerating) {
        isAccelerating = accelerating;
    }
    
    public boolean isRotatingLeft() {
        return isRotatingLeft;
    }
    
    public void setRotatingLeft(boolean rotatingLeft) {
        isRotatingLeft = rotatingLeft;
    }
    
    public boolean isRotatingRight() {
        return isRotatingRight;
    }
    
    public void setRotatingRight(boolean rotatingRight) {
        isRotatingRight = rotatingRight;
    }
}

