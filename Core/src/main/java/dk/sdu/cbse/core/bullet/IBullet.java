package dk.sdu.cbse.core.bullet;

import dk.sdu.cbse.core.Entity;

/**
 * Interface representing a bullet entity in the game.
 * This interface adds additional methods specific to bullet behavior.
 */
public interface IBullet {
    
    /**
     * Gets the entity that fired this bullet.
     * 
     * @return The shooter entity or null if not available
     */
    Entity getShooter();
    
    /**
     * Gets the damage this bullet deals on impact.
     * 
     * @return Damage amount
     */
    int getDamage();
    
    /**
     * Sets the damage this bullet deals on impact.
     * 
     * @param damage Damage amount
     */
    void setDamage(int damage);
    
    /**
     * Gets the speed of this bullet.
     * 
     * @return Speed in units per second
     */
    float getSpeed();
    
    /**
     * Sets the speed of this bullet.
     * 
     * @param speed Speed in units per second
     */
    void setSpeed(float speed);
    
    /**
     * Gets the maximum lifetime of this bullet in seconds.
     * 
     * @return Lifetime in seconds
     */
    float getLifetime();
    
    /**
     * Sets the maximum lifetime of this bullet in seconds.
     * 
     * @param lifetime Lifetime in seconds
     */
    void setLifetime(float lifetime);
    
    /**
     * Gets the current age of this bullet in seconds.
     * 
     * @return Current age in seconds
     */
    float getAge();
    
    /**
     * Updates the bullet's age.
     * 
     * @param deltaTime Time passed since last update in seconds
     * @return True if the bullet is still active, false if it should be removed
     */
    boolean updateAge(float deltaTime);
    
    /**
     * Gets the bullet type identifier.
     * 
     * @return String identifier for the bullet type
     */
    String getBulletType();
}

