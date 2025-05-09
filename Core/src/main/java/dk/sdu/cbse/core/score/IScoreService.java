package dk.sdu.cbse.core.score;

import java.util.List;

/**
 * Interface for the Score Service.
 * Defines methods for interacting with the Scoring microservice.
 * Implementations of this interface will handle communication with the
 * remote scoring service via HTTP.
 */
public interface IScoreService {
    
    /**
     * Submit a new score to the scoring service.
     *
     * @param playerName The name of the player
     * @param scoreValue The score value achieved
     * @return true if the score was successfully submitted, false otherwise
     */
    boolean submitScore(String playerName, int scoreValue);
    
    /**
     * Get the top scores from the scoring service.
     *
     * @param limit The maximum number of scores to retrieve (optional)
     * @return A list of score entries, ordered by score value (highest first)
     */
    List<ScoreData> getTopScores(int limit);
    
    /**
     * Get the highest score for a specific player.
     *
     * @param playerName The name of the player
     * @return The player's highest score, or null if no scores found
     */
    ScoreData getPlayerHighScore(String playerName);
    
    /**
     * Get all scores for a specific player.
     *
     * @param playerName The name of the player
     * @return A list of the player's scores, ordered by score value (highest first)
     */
    List<ScoreData> getPlayerScores(String playerName);
    
    /**
     * Initialize the score service.
     * This method should be called during system startup.
     */
    void initialize();
    
    /**
     * Check if the scoring service is available.
     *
     * @return true if the service is available, false otherwise
     */
    boolean isServiceAvailable();
}

