module Player {
    requires Core;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.logging;
    
    exports dk.sdu.cbse.player;
    
    // Original service provisions
    provides dk.sdu.cbse.core.IGamePluginService with dk.sdu.cbse.player.PlayerPlugin;
    provides dk.sdu.cbse.core.IEntityProcessorService with dk.sdu.cbse.player.PlayerProcessor;
    
    // New component service provision
    provides dk.sdu.cbse.core.component.IComponentService with dk.sdu.cbse.player.PlayerComponent;
    
    // Optional: Weapon service for shooting capability
    provides dk.sdu.cbse.core.weapon.IWeaponService with dk.sdu.cbse.player.PlayerWeaponService;
    
    // Services used by this module
    uses dk.sdu.cbse.core.collision.ICollisionService;
    uses dk.sdu.cbse.core.bullet.IBulletService;
}

