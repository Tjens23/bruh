package dk.sdu.cbse.core.component;

/**
 * Service interface for component lifecycle management.
 * This interface defines methods for managing the lifecycle of game components.
 */
public interface IComponentService {
    
    /**
     * Initializes the component.
     * This is called before the component is started.
     */
    void init();
    
    /**
     * Starts the component.
     * This is called after initialization and when the component should become active.
     */
    void start();
    
    /**
     * Stops the component.
     * This is called when the component should become inactive but not disposed.
     */
    void stop();
    
    /**
     * Disposes of the component.
     * This is called when the component is no longer needed and should clean up resources.
     */
    void dispose();
    
    /**
     * Checks if the component is active.
     * 
     * @return True if the component is active, false otherwise
     */
    boolean isActive();
    
    /**
     * Sets whether the component is active.
     * 
     * @param active True to activate the component, false to deactivate it
     */
    void setActive(boolean active);
    
    /**
     * Gets the component's name.
     * 
     * @return The component's name
     */
    String getName();
    
    /**
     * Sets the component's name.
     * 
     * @param name The component's name
     */
    void setName(String name);
    
    /**
     * Registers a dependency for this component.
     * 
     * @param dependency The component to register as a dependency
     */
    void addDependency(IComponentService dependency);
    
    /**
     * Removes a dependency for this component.
     * 
     * @param dependency The component to remove as a dependency
     */
    void removeDependency(IComponentService dependency);
    
    /**
     * Gets all dependencies for this component.
     * 
     * @return Array of dependencies
     */
    IComponentService[] getDependencies();
}

