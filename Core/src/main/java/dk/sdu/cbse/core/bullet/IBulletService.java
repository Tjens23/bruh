package dk.sdu.cbse.core.bullet;

import dk.sdu.cbse.core.Entity;

import java.util.List;

/**
 * Service interface for bullet creation and management.
 * This interface defines methods for creating and configuring bullets.
 */
public interface IBulletService {
    
    /**
     * Creates a new bullet fired by the specified entity.
     * 
     * @param shooter The entity firing the bullet
     * @param x Starting X position
     * @param y Starting Y position
     * @param direction Direction in radians
     * @param speed Speed in units per second
     * @return The created bullet entity
     */
    Entity createBullet(Entity shooter, float x, float y, float direction, float speed);
    
    /**
     * Creates multiple bullets in a spread pattern.
     * 
     * @param shooter The entity firing the bullets
     * @param x Starting X position
     * @param y Starting Y position
     * @param direction Center direction in radians
     * @param speed Speed in units per second
     * @param count Number of bullets to create
     * @param spreadAngle Total angle of spread in radians
     * @return List of created bullet entities
     */
    List<Entity> createBulletSpread(Entity shooter, float x, float y, float direction, 
                                   float speed, int count, float spreadAngle);
    
    /**
     * Sets the default damage for bullets created by this service.
     * 
     * @param damage Default damage amount
     */
    void setDefaultDamage(int damage);
    
    /**
     * Gets the default damage for bullets created by this service.
     * 
     * @return Default damage amount
     */
    int getDefaultDamage();
    
    /**
     * Sets the default lifetime for bullets created by this service.
     * 
     * @param lifetime Default lifetime in seconds
     */
    void setDefaultLifetime(float lifetime);
    
    /**
     * Gets the default lifetime for bullets created by this service.
     * 
     * @return Default lifetime in seconds
     */
    float getDefaultLifetime();
    
    /**
     * Gets the bullet type identifier.
     * 
     * @return String identifier for the bullet type
     */
    String getBulletType();
}

