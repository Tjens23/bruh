package dk.sdu.cbse.scoring.repository;

import dk.sdu.cbse.scoring.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing and managing Score entities.
 * Extends JpaRepository to provide CRUD operations and additional custom finder methods.
 */
@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    /**
     * Find all scores for a specific player, ordered by score value in descending order.
     *
     * @param playerName the name of the player
     * @return a list of scores for the player, ordered by score value (highest first)
     */
    List<Score> findByPlayerNameOrderByScoreValueDesc(String playerName);

    /**
     * Find the highest score for a specific player.
     *
     * @param playerName the name of the player
     * @return the highest score for the player, or null if no scores found
     */
    Score findTopByPlayerNameOrderByScoreValueDesc(String playerName);
    
    /**
     * Find the top N highest scores across all players.
     *
     * @param limit the number of scores to return
     * @return a list of the top N scores, ordered by score value (highest first)
     */
    @Query("SELECT s FROM Score s ORDER BY s.scoreValue DESC")
    List<Score> findTopScores(int limit);
    
    /**
     * Find the top 10 highest scores across all players.
     *
     * @return a list of the top 10 scores, ordered by score value (highest first)
     */
    @Query(value = "SELECT * FROM scores ORDER BY score_value DESC LIMIT 10", nativeQuery = true)
    List<Score> findTop10Scores();
    
    /**
     * Count the number of scores above a certain value.
     *
     * @param scoreValue the minimum score value
     * @return the count of scores greater than or equal to the specified value
     */
    long countByScoreValueGreaterThanEqual(int scoreValue);
}

