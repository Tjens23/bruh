package dk.sdu.cbse;

import dk.sdu.cbse.core.CollisionProcessor;
import dk.sdu.cbse.core.Entity;
import dk.sdu.cbse.core.IEntityProcessorService;
import dk.sdu.cbse.core.IGamePluginService;
import dk.sdu.cbse.core.IPostEntityProcessorService;
import dk.sdu.cbse.core.component.IComponentService;
import dk.sdu.cbse.core.collision.ICollisionService;
import dk.sdu.cbse.core.score.IScoreService;
import dk.sdu.cbse.core.score.ScoreData;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class for the Asteroids game.
 * Sets up the game window, game loop, and entity processing.
 */
@Component
public class App extends Application {
    // Logger
    private static final Logger logger = Logger.getLogger(App.class.getName());
    
    // Window dimensions
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    
    // Game state
    private List<Entity> entities = new ArrayList<>();
    
    // Spring-injected GameManager
    @Autowired
    private GameManager gameManager;
    
    // Component system
    @Autowired
    private List<IComponentService> components;
    
    private final Map<String, IComponentService> componentMap = new HashMap<>();
    
    // Game state tracking
    private boolean gameRunning = true;
    private boolean gameOver = false;
    private int score = 0;
    private int lives = 3;
    
    // JavaFX components
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private Scene scene;
    
    // Game timing
    private long lastTime = 0;
    
    // Services
    @Autowired
    private CollisionProcessor collisionProcessor;
    
    @Autowired
    private ICollisionService collisionService;
    
    @Autowired
    private IScoreService scoreService;
    
    // Player name for score tracking
    private String playerName = "Player";
    
    // Spring application context
    private static ApplicationContext applicationContext;
    
    /**
     * Entry point for the application
     */
    public static void main(String[] args) {
        // Initialize Spring ApplicationContext
        applicationContext = new AnnotationConfigApplicationContext(CoreConfig.class);
        
        // Register the App bean with Spring
        ((AnnotationConfigApplicationContext) applicationContext).registerBean(App.class);
        
        launch(args);
    }
    
    /**
     * JavaFX application start method.
     * Sets up the game window, initializes components, and starts the game loop.
     */
    @Override
    public void start(Stage primaryStage) {
        // Set up JavaFX window
        primaryStage.setTitle("AsteroidsFX");
        
        // Set up canvas
        canvas = new Canvas(WIDTH, HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        
        // Set up scene
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        
        // Add keyboard event handlers
        setupInputHandlers();
        
        // Make entities accessible from scene
        root.getProperties().put("entities", entities);
        
        try {
            // Get the App bean from Spring context and update this instance
            App appBean = applicationContext.getBean(App.class);
            
            // Initialize component system first
            initializeComponentSystem();
            
            // Initialize game manager
            initializeGameManager();
            
            // Set up game loop
            setupGameLoop();
            
            // Show the window
            primaryStage.show();
            
            logger.info("Application started successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting application", e);
        }
    }
    
    /**
     * Initializes the component system by loading and starting all components.
     */
    private void initializeComponentSystem() {
        logger.info("Initializing component system");
        
        // Components are already loaded by Spring via dependency injection
        
        // First pass: collect all components
        for (IComponentService component : components) {
            logger.info("Found component: " + component.getName());
            components.add(component);
            componentMap.put(component.getName(), component);
        }
        
        // Second pass: initialize all components
        for (IComponentService component : components) {
            try {
                logger.info("Initializing component: " + component.getName());
                component.init();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error initializing component: " + component.getName(), e);
            }
        }
        
        // Third pass: start all components
        for (IComponentService component : components) {
            try {
                if (!component.isActive()) {
                    logger.info("Starting component: " + component.getName());
                    component.start();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error starting component: " + component.getName(), e);
            }
        }
        
        logger.info("Component system initialized with " + components.size() + " components");
    }
    
    /**
     * Initializes the game manager.
     */
    private void initializeGameManager() {
        logger.info("Initializing game manager");
        
        // Initialize game manager (which will start all game plugins)
        gameManager.initialize();
        
        // Get the entities from the game manager
        entities = gameManager.getEntities();
        
        // Initialize score service
        initializeScoreService();
        
        logger.info("Game manager initialized");
    }
    
    /**
     * Initializes the score service.
     */
    private void initializeScoreService() {
        logger.info("Initializing score service");
        
        try {
            scoreService.initialize();
            if (scoreService.isServiceAvailable()) {
                logger.info("Score service initialized successfully");
            } else {
                logger.warning("Score service is not available");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to initialize score service", e);
        }
    }
    
    /**
     * Sets up input handlers for keyboard events
     */
    private void setupInputHandlers() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R && gameOver) {
                restartGame();
            }
        });
    }
    
    /**
     * Restarts the game after game over
     */
    private void restartGame() {
        logger.info("Restarting game");
        
        // Reset game state
        gameOver = false;
        gameRunning = true;
        score = 0;
        lives = 3;
        
        // Reset collision processor
        if (collisionProcessor != null) {
            collisionProcessor.setScore(0);
            collisionProcessor.setLives(3);
        }
        
        // Re-initialize score service if needed
        if (scoreService != null && !scoreService.isServiceAvailable()) {
            initializeScoreService();
        }
        
        // Clear entities
        entities.clear();
        
        // Restart components
        restartComponents();
        
        // Restart game manager
        gameManager.shutdown();
        gameManager.initialize();
        
        // Update entities reference
        entities = gameManager.getEntities();
        
        logger.info("Game restarted successfully");
    }
    
    /**
     * Restarts all game components.
     */
    private void restartComponents() {
        // First stop all components
        for (IComponentService component : components) {
            try {
                if (component.isActive()) {
                    logger.info("Stopping component: " + component.getName());
                    component.stop();
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error stopping component: " + component.getName(), e);
            }
        }
        
        // Then start all components again
        for (IComponentService component : components) {
            try {
                logger.info("Restarting component: " + component.getName());
                component.start();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error restarting component: " + component.getName(), e);
            }
        }
    }
    
    /**
     * Sets up the game loop using AnimationTimer
     */
    private void setupGameLoop() {
        lastTime = System.nanoTime();
        
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    // Calculate delta time (in seconds)
                    float deltaTime = (now - lastTime) / 1_000_000_000.0f;
                    lastTime = now;
                    
        // Clear the canvas
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Update entities reference in case GameManager modified it
        entities = gameManager.getEntities();
                    
                    if (gameRunning) {
                        // Update components (if any need per-frame updates)
                        updateComponents(deltaTime);
                        
                        // Update game state through the game manager
                        gameManager.update(deltaTime);
                        
                        // Update game state from collision processor
                        updateGameState();
                    }
                    
                    // Render entities
                    renderEntities();
                    
                    // Render game state (UI)
                    renderGameState();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error in game loop", e);
                }
            }
        };
        
        gameLoop.start();
    }
    
    /**
     * Updates component-specific logic each frame.
     * This is for components that need per-frame updates beyond the standard entity processing.
     */
    private void updateComponents(float deltaTime) {
        // In a complete implementation, we might have component-specific update methods
        // For now, we'll just use this as a placeholder
    }
    
    /**
     * Updates game state from the collision processor
     */
    private void updateGameState() {
        if (collisionProcessor != null) {
            // Get the current score and check if it changed
            int currentScore = collisionProcessor.getPlayerScore();
            if (currentScore > score) {
                // Score increased, submit to scoring service
                submitScore(currentScore);
            }
            
            score = currentScore;
            lives = collisionProcessor.getPlayerLives();
            
            // Check for game over
            if (lives <= 0) {
                gameOver = true;
                gameRunning = false;
                
                // Submit final score when game ends
                submitFinalScore();
            }
        }
    }
    
    /**
     * Submits the current score to the scoring service.
     * 
     * @param currentScore The current score to submit
     */
    private void submitScore(int currentScore) {
        if (scoreService != null && scoreService.isServiceAvailable()) {
            try {
                scoreService.submitScore(playerName, currentScore);
                logger.info("Score submitted: " + currentScore);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to submit score", e);
            }
        }
    }
    
    /**
     * Submits the final score at game over.
     */
    private void submitFinalScore() {
        if (scoreService != null && scoreService.isServiceAvailable() && score > 0) {
            try {
                scoreService.submitScore(playerName, score);
                logger.info("Final score submitted: " + score);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to submit final score", e);
            }
        }
    }
    
    /**
     * Renders all active entities on the canvas
     */
    private void renderEntities() {
        for (Entity entity : entities) {
            if (entity.isActive()) {
                // Use different colors and styles based on entity type
                switch (entity.getType()) {
                    case "player":
                        renderPlayer(entity);
                        break;
                    case "enemy":
                        renderEnemy(entity);
                        break;
                    case "asteroid":
                        renderAsteroid(entity);
                        break;
                    case "projectile":
                        renderProjectile(entity);
                        break;
                    default:
                        // Default rendering for unknown entities
                        graphicsContext.setStroke(Color.WHITE);
                        graphicsContext.setLineWidth(2);
                        graphicsContext.strokeOval(
                            entity.getX() - entity.getRadius(),
                            entity.getY() - entity.getRadius(),
                            entity.getRadius() * 2,
                            entity.getRadius() * 2
                        );
                        break;
                }
            }
        }
    }
    
    /**
     * Renders the player ship
     */
    private void renderPlayer(Entity player) {
        graphicsContext.setStroke(Color.LIMEGREEN);
        graphicsContext.setLineWidth(2);
        
        // Draw a triangle representing the player's ship
        double x = player.getX();
        double y = player.getY();
        double radius = player.getRadius();
        double rotation = player.getRadians();
        
        double x1 = x + Math.cos(rotation) * radius * 1.5;
        double y1 = y + Math.sin(rotation) * radius * 1.5;
        
        double x2 = x + Math.cos(rotation + 2.5) * radius;
        double y2 = y + Math.sin(rotation + 2.5) * radius;
        
        double x3 = x + Math.cos(rotation - 2.5) * radius;
        double y3 = y + Math.sin(rotation - 2.5) * radius;
        
        graphicsContext.beginPath();
        graphicsContext.moveTo(x1, y1);
        graphicsContext.lineTo(x2, y2);
        graphicsContext.lineTo(x3, y3);
        graphicsContext.closePath();
        graphicsContext.stroke();
    }
    
    /**
     * Renders an enemy
     */
    private void renderEnemy(Entity enemy) {
        graphicsContext.setStroke(Color.RED);
        graphicsContext.setLineWidth(2);
        
        // Draw a diamond shape for enemies
        double x = enemy.getX();
        double y = enemy.getY();
        double radius = enemy.getRadius();
        
        graphicsContext.strokeRect(x - radius, y - radius, radius * 2, radius * 2);
    }
    
    /**
     * Renders an asteroid
     */
    private void renderAsteroid(Entity asteroid) {
        graphicsContext.setStroke(Color.WHITE);
        graphicsContext.setLineWidth(1.5);
        
        // Draw a irregular circle for asteroids
        double x = asteroid.getX();
        double y = asteroid.getY();
        double radius = asteroid.getRadius();
        
        graphicsContext.strokeOval(
            x - radius,
            y - radius,
            radius * 2,
            radius * 2
        );
        
        // Add some interior lines to make it look more like a rock
        graphicsContext.strokeLine(
            x - radius * 0.5, y - radius * 0.5,
            x + radius * 0.5, y + radius * 0.5
        );
        graphicsContext.strokeLine(
            x + radius * 0.5, y - radius * 0.5,
            x - radius * 0.5, y + radius * 0.5
        );
    }
    
    /**
     * Renders a projectile
     */
    private void renderProjectile(Entity projectile) {
        graphicsContext.setFill(Color.YELLOW);
        
        // Draw a small filled circle for projectiles
        graphicsContext.fillOval(
            projectile.getX() - projectile.getRadius(),
            projectile.getY() - projectile.getRadius(),
            projectile.getRadius() * 2,
            projectile.getRadius() * 2
        );
    }
    
    /**
     * Renders game state information (score, lives, game over)
     */
    private void renderGameState() {
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font("Arial", 20));
        
        // Display score
        graphicsContext.setTextAlign(TextAlignment.LEFT);
        graphicsContext.fillText("Score: " + score, 20, 30);
        
        // Display lives
        graphicsContext.setTextAlign(TextAlignment.RIGHT);
        graphicsContext.fillText("Lives: " + lives, WIDTH - 20, 30);
        
        // Display game over if applicable
        if (gameOver) {
            graphicsContext.setFill(Color.RED);
            graphicsContext.setFont(Font.font("Arial", 40));
            graphicsContext.setTextAlign(TextAlignment.CENTER);
            graphicsContext.fillText("GAME OVER", WIDTH / 2, HEIGHT / 2);
            
            graphicsContext.setFont(Font.font("Arial", 20));
            graphicsContext.fillText("Press R to Restart", WIDTH / 2, HEIGHT / 2 + 40);
            
            // Show high scores if available
            renderHighScores();
        }
    }
    
    /**
     * Renders the high scores from the scoring service.
     */
    private void renderHighScores() {
        if (scoreService != null && scoreService.isServiceAvailable()) {
            try {
                List<ScoreData> topScores = scoreService.getTopScores(5);
                
                if (topScores != null && !topScores.isEmpty()) {
                    graphicsContext.setFill(Color.YELLOW);
                    graphicsContext.setFont(Font.font("Arial", 20));
                    graphicsContext.setTextAlign(TextAlignment.CENTER);
                    graphicsContext.fillText("HIGH SCORES", WIDTH / 2, HEIGHT / 2 + 80);
                    
                    graphicsContext.setFont(Font.font("Arial", 16));
                    int yPos = HEIGHT / 2 + 110;
                    
                    for (ScoreData score : topScores) {
                        String scoreText = score.getPlayerName() + ": " + score.getScoreValue();
                        graphicsContext.fillText(scoreText, WIDTH / 2, yPos);
                        yPos += 25;
                    }
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to render high scores", e);
            }
        }
    }
}
