module Core {
// Required external modules
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.logging;
    
    // Spring Framework modules - optional at runtime
    requires static spring.core;
    requires static spring.beans;
    requires static spring.context;
    requires static spring.web;
    requires static spring.aop;
    
// Exported packages - these are accessible to other modules
    exports dk.sdu.cbse;                    // Main package with App class
    exports dk.sdu.cbse.core;               // Core interfaces and classes
    exports dk.sdu.cbse.core.bullet;        // Bullet interfaces and common implementations
    exports dk.sdu.cbse.core.weapon;        // Weapon interfaces
    exports dk.sdu.cbse.core.collision;     // Collision handling interfaces
    exports dk.sdu.cbse.core.component;     // Component lifecycle interfaces
    exports dk.sdu.cbse.core.score;         // Score service interface and data
    
// Services consumed by this module
    uses dk.sdu.cbse.core.IGamePluginService;            // Game entity creation plugins
    uses dk.sdu.cbse.core.IEntityProcessorService;       // Entity processing services
    uses dk.sdu.cbse.core.IPostEntityProcessorService;   // Post-processing services
    
    // New granular services
    uses dk.sdu.cbse.core.weapon.IWeaponService;         // Weapon functionality
    uses dk.sdu.cbse.core.bullet.IBulletService;         // Bullet creation and management
    uses dk.sdu.cbse.core.collision.ICollisionService;   // Collision detection and response
    uses dk.sdu.cbse.core.component.IComponentService;   // Component lifecycle management
    uses dk.sdu.cbse.core.score.IScoreService;           // Score management and tracking
    
    // Self-provided services (implementations in Core module)
    provides dk.sdu.cbse.core.collision.ICollisionService 
        with dk.sdu.cbse.core.collision.DefaultCollisionService;
    provides dk.sdu.cbse.core.score.IScoreService
        with dk.sdu.cbse.core.score.RestScoreService;
        
    // Open packages to allow reflection for both service loaders and Spring DI
    
// Open packages for reflection and Spring dependency injection
    opens dk.sdu.cbse to java.base, spring.core, spring.beans, spring.context;
    opens dk.sdu.cbse.core to java.base, spring.core, spring.beans, spring.context;
    opens dk.sdu.cbse.core.collision to java.base, spring.core, spring.beans, spring.context;
    opens dk.sdu.cbse.core.bullet to java.base, spring.core, spring.beans, spring.context;
    opens dk.sdu.cbse.core.weapon to java.base, spring.core, spring.beans, spring.context;
    opens dk.sdu.cbse.core.component to java.base, spring.core, spring.beans, spring.context;
    opens dk.sdu.cbse.core.score to java.base, spring.core, spring.beans, spring.context, spring.web;
}

