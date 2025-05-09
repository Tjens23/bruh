module Enemy {
    requires Core;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.logging;
    
    exports dk.sdu.cbse.enemy;
    
    // Original service provisions
    provides dk.sdu.cbse.core.IGamePluginService with dk.sdu.cbse.enemy.EnemyPlugin;
    provides dk.sdu.cbse.core.IEntityProcessorService with dk.sdu.cbse.enemy.EnemyProcessor;
    
    // New component service provision
    provides dk.sdu.cbse.core.component.IComponentService with dk.sdu.cbse.enemy.EnemyComponent;
    
    // Services used by this module
    uses dk.sdu.cbse.core.collision.ICollisionService;
}

