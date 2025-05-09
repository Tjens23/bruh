package dk.sdu.cbse.core.weapon;

import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.bullet.IBullet;

import java.util.List;

/**
 * Service interface for weapon functionality.
 * This interface defines methods for shooting and managing weapon state.
 */
public interface IWeaponService {
    
    /**
     * Fires a weapon from the specified entity.
     * 
     * @param shooter The entity that is firing the weapon
     * @return A list of created bullet entities or empty list if no bullets were created
     */
    List<Entity> shoot(Entity shooter);
    
    /**
     * Checks if the weapon can shoot (based on cooldown, ammo, etc.)
     * 
     * @param shooter The entity that would fire the weapon
     * @return True if the weapon can shoot, false otherwise
     */
    boolean canShoot(Entity shooter);
    
    /**
     * Gets the cooldown time for this weapon in seconds.
     * 
     * @return Cooldown time in seconds
     */
    float getCooldown();
    
    /**
     * Gets the current remaining cooldown time for the specified entity.
     * 
     * @param shooter The entity to check cooldown for
     * @return Remaining cooldown time in seconds
     */
    float getRemainingCooldown(Entity shooter);
    
    /**
     * Updates the weapon state (e.g., cooldown timer).
     * 
     * @param shooter The entity with the weapon
     * @param deltaTime Time passed since last update in seconds
     */
    void update(Entity shooter, float deltaTime);
    
    /**
     * Gets the weapon type identifier.
     * 
     * @return String identifier for the weapon type
     */
    String getWeaponType();
}

