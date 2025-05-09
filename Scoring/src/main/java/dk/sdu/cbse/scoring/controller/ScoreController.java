package dk.sdu.cbse.scoring.controller;

import dk.sdu.cbse.scoring.model.Score;
import dk.sdu.cbse.scoring.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing game scores.
 * Provides endpoints for creating and retrieving scores.
 */
@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreRepository scoreRepository;

    /**
     * Constructor with dependency injection.
     *
     * @param scoreRepository the repository for score data access
     */
    @Autowired
    public ScoreController(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    /**
     * Create a new score.
     *
     * @param scoreRequest the score data transfer object
     * @return the created score with HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Score> createScore(@RequestBody ScoreRequest scoreRequest) {
        if (scoreRequest.getPlayerName() == null || scoreRequest.getPlayerName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Score score = new Score(scoreRequest.getPlayerName(), scoreRequest.getScoreValue());
        Score savedScore = scoreRepository.save(score);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedScore);
    }

    /**
     * Get all scores, optionally limited and sorted.
     *
     * @param limit optional parameter to limit the number of results
     * @return list of scores
     */
    @GetMapping
    public ResponseEntity<List<Score>> getAllScores(@RequestParam(required = false) Integer limit) {
        List<Score> scores;
        
        if (limit != null && limit > 0) {
            scores = scoreRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "scoreValue"))
            ).getContent();
        } else {
            scores = scoreRepository.findAll(Sort.by(Sort.Direction.DESC, "scoreValue"));
        }
        
        return ResponseEntity.ok(scores);
    }

    /**
     * Get the top 10 highest scores.
     *
     * @return list of the top 10 scores
     */
    @GetMapping("/top")
    public ResponseEntity<List<Score>> getTopScores() {
        List<Score> topScores = scoreRepository.findTop10Scores();
        return ResponseEntity.ok(topScores);
    }

    /**
     * Get scores for a specific player.
     *
     * @param playerName the name of the player
     * @return list of scores for the player
     */
    @GetMapping("/player/{playerName}")
    public ResponseEntity<List<Score>> getScoresByPlayer(@PathVariable String playerName) {
        List<Score> scores = scoreRepository.findByPlayerNameOrderByScoreValueDesc(playerName);
        
        if (scores.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(scores);
    }

    /**
     * Get the highest score for a specific player.
     *
     * @param playerName the name of the player
     * @return the highest score for the player
     */
    @GetMapping("/player/{playerName}/highest")
    public ResponseEntity<Score> getHighestScoreByPlayer(@PathVariable String playerName) {
        Score highestScore = scoreRepository.findTopByPlayerNameOrderByScoreValueDesc(playerName);
        
        if (highestScore == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(highestScore);
    }

    /**
     * Delete a score by ID.
     *
     * @param id the ID of the score to delete
     * @return HTTP status 204 (No Content) if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScore(@PathVariable Long id) {
        if (!scoreRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        scoreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DTO for score creation requests.
     */
    public static class ScoreRequest {
        private String playerName;
        private int scoreValue;

        public ScoreRequest() {
        }

        public ScoreRequest(String playerName, int scoreValue) {
            this.playerName = playerName;
            this.scoreValue = scoreValue;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public int getScoreValue() {
            return scoreValue;
        }

        public void setScoreValue(int scoreValue) {
            this.scoreValue = scoreValue;
        }
    }
}

