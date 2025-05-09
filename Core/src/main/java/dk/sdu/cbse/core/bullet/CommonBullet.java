package dk.sdu.cbse.core.bullet;

import dk.sdu.cbse.core.Entity;

/**
 * Common implementation of a bullet entity.
 * This class provides basic bullet functionality that can be used by different weapon types.
 */
public class CommonBullet extends Entity implements IBullet {
    
    // Default values
    private static final float DEFAULT_RADIUS = 3.0f;
    private static final float DEFAULT_LIFETIME = 2.0f;
    private static final int DEFAULT_DAMAGE = 1;
    private static final String DEFAULT_BULLET_TYPE = "common";
    
    // Bullet properties
    private Entity shooter;
    private int damage;
    private float speed;
    private float lifetime;
    private float age;
    private String bulletType;
    
    /**
     * Creates a new bullet with default properties.
     */
    public CommonBullet() {
        this(null, DEFAULT_DAMAGE, 0f, DEFAULT_LIFETIME, DEFAULT_BULLET_TYPE);
    }
    
    /**
     * Creates a new bullet with the specified properties.
     * 
     * @param shooter The entity that fired this bullet
     * @param damage The damage this bullet deals
     * @param speed The speed of this bullet
     * @param lifetime The maximum lifetime of this bullet
     * @param bulletType The type of this bullet
     */
    public CommonBullet(Entity shooter, int damage, float speed, float lifetime, String bulletType) {
        this.shooter = shooter;
        this.damage = damage;
        this.speed = speed;
        this.lifetime = lifetime;
        this.age = 0f;
        this.bulletType = bulletType;
        
        // Set entity properties
        setRadius(DEFAULT_RADIUS);
        setType("projectile");
        setActive(true);
    }
    
    /**
     * Initializes the bullet with position, direction, and speed.
     * 
     * @param x Starting X position
     * @param y Starting Y position
     * @param direction Direction in radians
     * @param speed Speed in units per second
     */
    public void init(float x, float y, float direction, float speed) {
        setX(x);
        setY(y);
        setRadians(direction);
        this.speed = speed;
        
        // Calculate velocity components based on direction and speed
        float dx = (float) Math.cos(direction) * speed;
        float dy = (float) Math.sin(direction) * speed;
        setDx(dx);
        setDy(dy);
    }
    
    @Override
    public Entity getShooter() {
        return shooter;
    }
    
    /**
     * Sets the entity that fired this bullet.
     * 
     * @param shooter The shooter entity
     */
    public void setShooter(Entity shooter) {
        this.shooter = shooter;
    }
    
    @Override
    public int getDamage() {
        return damage;
    }
    
    @Override
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    @Override
    public float getSpeed() {
        return speed;
    }
    
    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
        
        // Update velocity components based on current direction
        float dx = (float) Math.cos(getRadians()) * speed;
        float dy = (float) Math.sin(getRadians()) * speed;
        setDx(dx);
        setDy(dy);
    }
    
    @Override
    public float getLifetime() {
        return lifetime;
    }
    
    @Override
    public void setLifetime(float lifetime) {
        this.lifetime = lifetime;
    }
    
    @Override
    public float getAge() {
        return age;
    }
    
    /**
     * Sets the current age of this bullet.
     * 
     * @param age Age in seconds
     */
    public void setAge(float age) {
        this.age = age;
    }
    
    @Override
    public boolean updateAge(float deltaTime) {
        age += deltaTime;
        
        // Check if the bullet has exceeded its lifetime
        if (age >= lifetime) {
            setActive(false);
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getBulletType() {
        return bulletType;
    }
    
    /**
     * Sets the bullet type identifier.
     * 
     * @param bulletType String identifier for the bullet type
     */
    public void setBulletType(String bulletType) {
        this.bulletType = bulletType;
    }
}

