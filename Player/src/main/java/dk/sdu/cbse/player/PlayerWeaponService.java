package dk.sdu.cbse.player;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.bullet.IBullet;
import dk.sdu.cbse.core.bullet.IBulletService;
import dk.sdu.cbse.core.weapon.IWeaponService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service that handles player weapon functionality.
 * This class manages player shooting mechanics, cooldown, and bullet creation.
 */
public class PlayerWeaponService implements IWeaponService {
    
    private static final Logger logger = Logger.getLogger(PlayerWeaponService.class.getName());
    
    // Weapon properties
    private static final float DEFAULT_COOLDOWN = 0.3f; // seconds
    private static final float DEFAULT_BULLET_SPEED = 400.0f; // units per second
    private static final int DEFAULT_BULLET_DAMAGE = 1;
    
    // Weapon state
    private final Map<Entity, Float> cooldowns = new HashMap<>();
    private String weaponType = "standard";
    private float cooldown = DEFAULT_COOLDOWN;
    private int level = 1;
    private int bulletDamage = DEFAULT_BULLET_DAMAGE;
    private float bulletSpeed = DEFAULT_BULLET_SPEED;
    
    // External services
    private IBulletService bulletService;
    
    /**
     * Creates a new player weapon service.
     */
    public PlayerWeaponService() {
        lookupBulletService();
    }
    
    /**
     * Looks up the bullet service using ServiceLoader.
     */
    private void lookupBulletService() {
        try {
            ServiceLoader<IBulletService> services = ServiceLoader.load(IBulletService.class);
            for (IBulletService service : services) {
                bulletService = service;
                logger.info("Found bullet service: " + service.getClass().getName());
                break;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error looking up bullet service", e);
        }
    }
    
    @Override
    public List<Entity> shoot(Entity shooter) {
        // Check if weapon is on cooldown
        if (!canShoot(shooter)) {
            return null;
        }
        
        // Check if bullet service is available
        if (bulletService == null) {
            logger.warning("Cannot shoot: No bullet service available");
            return null;
        }
        
        List<Entity> bullets = new ArrayList<>();
        
        // Different shooting patterns based on weapon level
        switch (level) {
            case 1:
                // Single bullet straight ahead
                bullets.add(createBullet(shooter, shooter.getRadians()));
                break;
                
            case 2:
                // Double shot (two bullets slightly spread)
                float spread = 0.1f; // radians
                bullets.add(createBullet(shooter, shooter.getRadians() - spread));
                bullets.add(createBullet(shooter, shooter.getRadians() + spread));
                break;
                
            case 3:
                // Triple shot (one straight, two spread)
                bullets.add(createBullet(shooter, shooter.getRadians()));
                bullets.add(createBullet(shooter, shooter.getRadians() - 0.2f));
                bullets.add(createBullet(shooter, shooter.getRadians() + 0.2f));
                break;
                
            default:
                // Default to single shot
                bullets.add(createBullet(shooter, shooter.getRadians()));
                break;
        }
        
        // Start cooldown
        cooldowns.put(shooter, cooldown);
        
        return bullets;
    }
    
    /**
     * Creates a single bullet.
     * 
     * @param shooter The entity shooting the bullet
     * @param direction The direction in radians
     * @return The created bullet entity
     */
    private Entity createBullet(Entity shooter, float direction) {
        // Calculate bullet starting position (at the front of the shooter)
        float bulletX = shooter.getX() + (float) Math.cos(direction) * (shooter.getRadius() + 5);
        float bulletY = shooter.getY() + (float) Math.sin(direction) * (shooter.getRadius() + 5);
        
        // Create the bullet
        Entity bulletEntity = bulletService.createBullet(shooter, bulletX, bulletY, direction, bulletSpeed);
        
        // Set bullet properties
        if (bulletEntity instanceof IBullet) {
            IBullet bullet = (IBullet) bulletEntity;
            bullet.setDamage(bulletDamage);
            bullet.setLifetime(1.5f); // 1.5 seconds lifetime
        }
        
        return bulletEntity;
    }
    
    @Override
    public boolean canShoot(Entity shooter) {
        if (shooter == null || !shooter.isActive()) {
            return false;
        }
        
        // Check cooldown
        Float remainingCooldown = cooldowns.get(shooter);
        return remainingCooldown == null || remainingCooldown <= 0;
    }
    
    @Override
    public float getCooldown() {
        return cooldown;
    }
    
    @Override
    public float getRemainingCooldown(Entity shooter) {
        if (shooter == null) {
            return 0;
        }
        
        Float remaining = cooldowns.get(shooter);
        return remaining != null ? remaining : 0;
    }
    
    @Override
    public void update(Entity shooter, float deltaTime) {
        // Update cooldowns
        if (shooter != null && cooldowns.containsKey(shooter)) {
            float remaining = cooldowns.get(shooter) - deltaTime;
            if (remaining <= 0) {
                cooldowns.remove(shooter);
            } else {
                cooldowns.put(shooter, remaining);
            }
        }
    }
    
    @Override
    public String getWeaponType() {
        return weaponType;
    }
    
    /**
     * Sets the weapon type.
     * 
     * @param weaponType The weapon type
     */
    public void setWeaponType(String weaponType) {
        this.weaponType = weaponType;
    }
    
    /**
     * Gets the current weapon level.
     * 
     * @return The weapon level
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Upgrades the weapon to the next level.
     * 
     * @return The new weapon level
     */
    public int upgradeWeapon() {
        if (level < 3) {
            level++;
            
            // Increase damage with each level
            bulletDamage++;
            
            logger.info("Weapon upgraded to level " + level);
        }
        
        return level;
    }
    
    /**
     * Resets the weapon to level 1.
     */
    public void resetWeapon() {
        level = 1;
        bulletDamage = DEFAULT_BULLET_DAMAGE;
        logger.info("Weapon reset to level 1");
    }
    
    /**
     * Sets the cooldown time for this weapon.
     * 
     * @param cooldown Cooldown time in seconds
     */
    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }
    
    /**
     * Sets the bullet damage.
     * 
     * @param damage Bullet damage
     */
    public void setBulletDamage(int damage) {
        this.bulletDamage = damage;
    }
    
    /**
     * Sets the bullet speed.
     * 
     * @param speed Bullet speed in units per second
     */
    public void setBulletSpeed(float speed) {
        this.bulletSpeed = speed;
    }
    
    /**
     * Gets the bullet damage.
     * 
     * @return The bullet damage
     */
    public int getBulletDamage() {
        return bulletDamage;
    }
    
    /**
     * Gets the bullet speed.
     * 
     * @return The bullet speed in units per second
     */
    public float getBulletSpeed() {
        return bulletSpeed;
    }
    
    /**
     * Sets the bullet service.
     * 
     * @param bulletService The bullet service to use
     */
    public void setBulletService(IBulletService bulletService) {
        this.bulletService = bulletService;
    }
}

