package dk.sdu.cbse.core.score;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for score information.
 * This class represents score data that is transferred between the Core module
 * and the Scoring microservice. It contains only the essential properties
 * needed for data exchange.
 */
public class ScoreData {
    
    private Long id;
    private String playerName;
    private int scoreValue;
    private LocalDateTime gameDate;
    
    /**
     * Default constructor.
     */
    public ScoreData() {
    }
    
    /**
     * Constructor with required fields for creating a new score.
     *
     * @param playerName The name of the player
     * @param scoreValue The score value achieved
     */
    public ScoreData(String playerName, int scoreValue) {
        this.playerName = playerName;
        this.scoreValue = scoreValue;
        this.gameDate = LocalDateTime.now();
    }
    
    /**
     * Full constructor.
     *
     * @param id The unique identifier
     * @param playerName The name of the player
     * @param scoreValue The score value achieved
     * @param gameDate The date and time when the score was achieved
     */
    public ScoreData(Long id, String playerName, int scoreValue, LocalDateTime gameDate) {
        this.id = id;
        this.playerName = playerName;
        this.scoreValue = scoreValue;
        this.gameDate = gameDate;
    }
    
    /**
     * Gets the unique identifier for this score.
     *
     * @return The score ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier for this score.
     *
     * @param id The score ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the player name.
     *
     * @return The player name
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Sets the player name.
     *
     * @param playerName The player name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    /**
     * Gets the score value.
     *
     * @return The score value
     */
    public int getScoreValue() {
        return scoreValue;
    }
    
    /**
     * Sets the score value.
     *
     * @param scoreValue The score value
     */
    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }
    
    /**
     * Gets the date and time when this score was recorded.
     *
     * @return The game date
     */
    public LocalDateTime getGameDate() {
        return gameDate;
    }
    
    /**
     * Sets the date and time when this score was recorded.
     *
     * @param gameDate The game date
     */
    public void setGameDate(LocalDateTime gameDate) {
        this.gameDate = gameDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreData scoreData = (ScoreData) o;
        return scoreValue == scoreData.scoreValue &&
                Objects.equals(id, scoreData.id) &&
                Objects.equals(playerName, scoreData.playerName) &&
                Objects.equals(gameDate, scoreData.gameDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, playerName, scoreValue, gameDate);
    }
    
    @Override
    public String toString() {
        return "ScoreData{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", scoreValue=" + scoreValue +
                ", gameDate=" + gameDate +
                '}';
    }
}

