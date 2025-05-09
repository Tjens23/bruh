package dk.sdu.cbse.core.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class for game components.
 * This class provides a base implementation of the IComponentService interface
 * that can be extended by game components.
 */
public abstract class GameComponent implements IComponentService {
    
    // Logger for this component
    private static final Logger logger = Logger.getLogger(GameComponent.class.getName());
    
    // Component state
    private boolean initialized = false;
    private boolean active = false;
    private boolean disposed = false;
    
    // Component properties
    private String name;
    
    // Dependencies
    private final List<IComponentService> dependencies = new ArrayList<>();
    
    /**
     * Creates a new game component with the given name.
     * 
     * @param name The component's name
     */
    public GameComponent(String name) {
        this.name = name;
    }
    
    /**
     * Creates a new game component with a default name based on the class name.
     */
    public GameComponent() {
        this.name = getClass().getSimpleName();
    }
    
    @Override
    public void init() {
        if (initialized) {
            logger.warning("Component already initialized: " + name);
            return;
        }
        
        try {
            // Initialize dependencies first
            for (IComponentService dependency : dependencies) {
                if (!dependency.isActive()) {
                    dependency.init();
                }
            }
            
            // Perform component-specific initialization
            doInit();
            
            initialized = true;
            logger.info("Component initialized: " + name);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing component: " + name, e);
            throw new RuntimeException("Error initializing component: " + name, e);
        }
    }
    
    /**
     * Performs component-specific initialization.
     * Subclasses should override this method to provide their initialization logic.
     */
    protected void doInit() {
        // Default implementation does nothing
    }
    
    @Override
    public void start() {
        if (!initialized) {
            logger.warning("Component not initialized: " + name);
            init();
        }
        
        if (active) {
            logger.warning("Component already started: " + name);
            return;
        }
        
        try {
            // Start dependencies first
            for (IComponentService dependency : dependencies) {
                if (!dependency.isActive()) {
                    dependency.start();
                }
            }
            
            // Perform component-specific start
            doStart();
            
            active = true;
            logger.info("Component started: " + name);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting component: " + name, e);
            throw new RuntimeException("Error starting component: " + name, e);
        }
    }
    
    /**
     * Performs component-specific start logic.
     * Subclasses should override this method to provide their start logic.
     */
    protected void doStart() {
        // Default implementation does nothing
    }
    
    @Override
    public void stop() {
        if (!active) {
            logger.warning("Component not active: " + name);
            return;
        }
        
        try {
            // Perform component-specific stop
            doStop();
            
            active = false;
            logger.info("Component stopped: " + name);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error stopping component: " + name, e);
            throw new RuntimeException("Error stopping component: " + name, e);
        }
    }
    
    /**
     * Performs component-specific stop logic.
     * Subclasses should override this method to provide their stop logic.
     */
    protected void doStop() {
        // Default implementation does nothing
    }
    
    @Override
    public void dispose() {
        if (disposed) {
            logger.warning("Component already disposed: " + name);
            return;
        }
        
        try {
            // Stop the component if it's still active
            if (active) {
                stop();
            }
            
            // Perform component-specific disposal
            doDispose();
            
            disposed = true;
            initialized = false;
            logger.info("Component disposed: " + name);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error disposing component: " + name, e);
            throw new RuntimeException("Error disposing component: " + name, e);
        }
    }
    
    /**
     * Performs component-specific disposal logic.
     * Subclasses should override this method to provide their disposal logic.
     */
    protected void doDispose() {
        // Default implementation does nothing
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        
        if (active) {
            start();
        } else {
            stop();
        }
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void addDependency(IComponentService dependency) {
        if (dependency == null) {
            throw new IllegalArgumentException("Dependency cannot be null");
        }
        
        if (!dependencies.contains(dependency)) {
            dependencies.add(dependency);
            logger.fine("Added dependency " + dependency.getName() + " to " + name);
        }
    }
    
    @Override
    public void removeDependency(IComponentService dependency) {
        if (dependency == null) {
            throw new IllegalArgumentException("Dependency cannot be null");
        }
        
        if (dependencies.remove(dependency)) {
            logger.fine("Removed dependency " + dependency.getName() + " from " + name);
        }
    }
    
    @Override
    public IComponentService[] getDependencies() {
        return dependencies.toArray(new IComponentService[0]);
    }
    
    /**
     * Checks if this component is initialized.
     * 
     * @return True if the component is initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Checks if this component is disposed.
     * 
     * @return True if the component is disposed, false otherwise
     */
    public boolean isDisposed() {
        return disposed;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + 
               ", active=" + active + 
               ", initialized=" + initialized + 
               ", disposed=" + disposed + 
               ", dependencies=" + Arrays.toString(getDependencies()) + "]";
    }
}

