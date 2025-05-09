module Asteroid {
    requires Core;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.logging;
    
    exports dk.sdu.cbse.asteroid;
    
    // Original service provisions
    provides dk.sdu.cbse.core.IGamePluginService with dk.sdu.cbse.asteroid.AsteroidPlugin;
    provides dk.sdu.cbse.core.IEntityProcessorService with dk.sdu.cbse.asteroid.AsteroidProcessor;
    
    // New component service provision
    provides dk.sdu.cbse.core.component.IComponentService with dk.sdu.cbse.asteroid.AsteroidComponent;
    
    // Services used by this module
    uses dk.sdu.cbse.core.collision.ICollisionService;
}

