package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.core.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Asteroid entity class with different size variations.
 */
public class Asteroid extends Entity {
    
    // Size enumeration with properties
    public enum Size {
        LARGE(32, 20, 50.0f, 2),
        MEDIUM(16, 50, 75.0f, 2),
        SMALL(8, 100, 100.0f, 0);
        
        private final float radius;
        private final int points;
        private final float maxSpeed;
        private final int splitCount;
        
        Size(float radius, int points, float maxSpeed, int splitCount) {
            this.radius = radius;
            this.points = points;
            this.maxSpeed = maxSpeed;
            this.splitCount = splitCount;
        }
        
        public float getRadius() {
            return radius;
        }
        
        public int getPoints() {
            return points;
        }
        
        public float getMaxSpeed() {
            return maxSpeed;
        }
        
        public int getSplitCount() {
            return splitCount;
        }
        
        /**
         * Returns the next smaller size or null if already smallest.
         */
        public Size getNextSize() {
            switch (this) {
                case LARGE: return MEDIUM;
                case MEDIUM: return SMALL;
                default: return null; // SMALL has no smaller size
            }
        }
    }
    
    private static final Random random = new Random();
    private static final float MIN_ROTATION_SPEED = 0.2f;
    private static final float MAX_ROTATION_SPEED = 1.5f;
    
    private Size size;
    private float rotationSpeed; // Radians per second
    
    /**
     * Creates a new asteroid of the specified size.
     */
    public Asteroid(Size size) {
        this.size = size;
        setType("asteroid");
        setRadius(size.getRadius());
        
        // Initialize with random movement
        initializeRandomMovement();
    }
    
    /**
     * Sets random velocity and rotation for the asteroid.
     */
    public void initializeRandomMovement() {
        // Random rotation direction and speed
        rotationSpeed = (random.nextFloat() * (MAX_ROTATION_SPEED - MIN_ROTATION_SPEED) + MIN_ROTATION_SPEED)
                * (random.nextBoolean() ? 1 : -1);
        
        // Random initial rotation
        setRadians(random.nextFloat() * (float)(Math.PI * 2));
        
        // Random velocity based on size
        float speed = random.nextFloat() * size.getMaxSpeed();
        float angle = random.nextFloat() * (float)(Math.PI * 2);
        
        setDx((float) Math.cos(angle) * speed);
        setDy((float) Math.sin(angle) * speed);
    }
    
    /**
     * Creates smaller asteroids when this asteroid is split.
     * @return List of new asteroids or empty list if this asteroid is too small to split.
     */
    public List<Asteroid> createSplitAsteroids() {
        List<Asteroid> splitAsteroids = new ArrayList<>();
        Size nextSize = size.getNextSize();
        
        if (nextSize != null) {
            // Create the specified number of smaller asteroids
            for (int i = 0; i < size.getSplitCount(); i++) {
                Asteroid newAsteroid = new Asteroid(nextSize);
                
                // Position new asteroid at the same location
                newAsteroid.setX(getX());
                newAsteroid.setY(getY());
                
                // Give it a unique direction different from the parent
                float angle = random.nextFloat() * (float)(Math.PI * 2);
                float speed = random.nextFloat() * nextSize.getMaxSpeed();
                
                newAsteroid.setDx((float) Math.cos(angle) * speed);
                newAsteroid.setDy((float) Math.sin(angle) * speed);
                
                splitAsteroids.add(newAsteroid);
            }
        }
        
        return splitAsteroids;
    }
    
    /**
     * Updates the asteroid's rotation based on its rotation speed.
     */
    public void updateRotation(float deltaTime) {
        setRadians(getRadians() + rotationSpeed * deltaTime);
    }
    
    public Size getSize() {
        return size;
    }
    
    public int getPointValue() {
        return size.getPoints();
    }
    
    public float getRotationSpeed() {
        return rotationSpeed;
    }
    
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }
}

