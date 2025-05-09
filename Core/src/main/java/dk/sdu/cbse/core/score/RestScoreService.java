package dk.sdu.cbse.core.score;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the IScoreService interface that communicates with
 * the Scoring microservice using Java's HttpURLConnection.
 * <p>
 * This service handles sending score data to and retrieving score data from
 * the remote Scoring microservice via HTTP.
 */
public class RestScoreService implements IScoreService {

    private static final Logger logger = Logger.getLogger(RestScoreService.class.getName());
    
    private static final String DEFAULT_SERVICE_URL = "http://localhost:8080/api/scores";
    private static final int DEFAULT_TIMEOUT_MS = 5000;
    
    private final String serviceUrl;
    private boolean serviceAvailable;
    
    /**
     * Default constructor.
     */
    public RestScoreService() {
        this(DEFAULT_SERVICE_URL);
    }
    
    /**
     * Constructor with configurable service URL.
     *
     * @param serviceUrl The URL of the scoring service
     */
    public RestScoreService(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        this.serviceAvailable = false;
    }
    
    @Override
    public void initialize() {
        logger.info("Initializing RestScoreService with service URL: " + serviceUrl);
        
        // Test if the service is available
        try {
            HttpURLConnection connection = createConnection(serviceUrl, "GET");
            int responseCode = connection.getResponseCode();
            serviceAvailable = responseCode >= 200 && responseCode < 300;
            connection.disconnect();
            
            logger.info("Score service is " + (serviceAvailable ? "available" : "unavailable"));
        } catch (IOException e) {
            serviceAvailable = false;
            logger.log(Level.WARNING, "Score service is unavailable: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isServiceAvailable() {
        return serviceAvailable;
    }
    
    @Override
    public boolean submitScore(String playerName, int scoreValue) {
        if (!serviceAvailable) {
            logger.warning("Cannot submit score: service is unavailable");
            return false;
        }
        
        try {
            HttpURLConnection connection = createConnection(serviceUrl, "POST");
            connection.setRequestProperty("Content-Type", "application/json");
            
            // Create JSON payload
            String jsonPayload = String.format(
                "{\"playerName\":\"%s\",\"scoreValue\":%d}",
                playerName, scoreValue
            );
            
            // Send POST data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            boolean success = responseCode >= 200 && responseCode < 300;
            
            if (success) {
                logger.info("Score submitted successfully: " + playerName + " - " + scoreValue);
            } else {
                logger.warning("Failed to submit score: HTTP " + responseCode);
            }
            
            connection.disconnect();
            return success;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error submitting score: " + e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<ScoreData> getTopScores(int limit) {
        if (!serviceAvailable) {
            logger.warning("Cannot get top scores: service is unavailable");
            return Collections.emptyList();
        }
        
        try {
            String url = limit > 0 ? serviceUrl + "?limit=" + limit : serviceUrl + "/top";
            HttpURLConnection connection = createConnection(url, "GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                String response = readResponse(connection);
                List<ScoreData> scores = parseScoresJson(response);
                connection.disconnect();
                return scores;
            } else {
                logger.warning("Failed to get top scores: HTTP " + responseCode);
                connection.disconnect();
                return Collections.emptyList();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error getting top scores: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public ScoreData getPlayerHighScore(String playerName) {
        if (!serviceAvailable) {
            logger.warning("Cannot get player high score: service is unavailable");
            return null;
        }
        
        try {
            String url = serviceUrl + "/player/" + playerName + "/highest";
            HttpURLConnection connection = createConnection(url, "GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                String response = readResponse(connection);
                ScoreData score = parseScoreJson(response);
                connection.disconnect();
                return score;
            } else {
                logger.warning("Failed to get player high score: HTTP " + responseCode);
                connection.disconnect();
                return null;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error getting player high score: " + e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public List<ScoreData> getPlayerScores(String playerName) {
        if (!serviceAvailable) {
            logger.warning("Cannot get player scores: service is unavailable");
            return Collections.emptyList();
        }
        
        try {
            String url = serviceUrl + "/player/" + playerName;
            HttpURLConnection connection = createConnection(url, "GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                String response = readResponse(connection);
                List<ScoreData> scores = parseScoresJson(response);
                connection.disconnect();
                return scores;
            } else {
                logger.warning("Failed to get player scores: HTTP " + responseCode);
                connection.disconnect();
                return Collections.emptyList();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error getting player scores: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Creates a HTTP URL connection.
     *
     * @param url The URL to connect to
     * @param method The HTTP method (GET, POST, etc.)
     * @return The configured HttpURLConnection
     * @throws IOException If an I/O error occurs
     */
    private HttpURLConnection createConnection(String url, String method) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(DEFAULT_TIMEOUT_MS);
        connection.setReadTimeout(DEFAULT_TIMEOUT_MS);
        
        if ("POST".equals(method)) {
            connection.setDoOutput(true);
        }
        
        return connection;
    }
    
    /**
     * Reads the response from an HTTP connection.
     *
     * @param connection The HTTP connection
     * @return The response as a string
     * @throws IOException If an I/O error occurs
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
    
    /**
     * Parses a JSON array of scores into a list of ScoreData objects.
     * This is a simple implementation and assumes well-formed JSON.
     *
     * @param json The JSON array string
     * @return A list of ScoreData objects
     */
    private List<ScoreData> parseScoresJson(String json) {
        List<ScoreData> scores = new ArrayList<>();
        
        // Simple JSON array parsing
        if (json == null || json.isEmpty() || !json.startsWith("[")) {
            return scores;
        }
        
        // Remove outer brackets
        json = json.substring(1, json.length() - 1);
        
        // Split by objects (looking for },{)
        String[] scoreObjects = json.split("\\},\\{");
        
        for (int i = 0; i < scoreObjects.length; i++) {
            String scoreJson = scoreObjects[i];
            
            // Fix the JSON object string
            if (i == 0 && !scoreJson.startsWith("{")) {
                scoreJson = "{" + scoreJson;
            }
            if (i == scoreObjects.length - 1 && !scoreJson.endsWith("}")) {
                scoreJson = scoreJson + "}";
            }
            if (i > 0 && i < scoreObjects.length - 1) {
                scoreJson = "{" + scoreJson + "}";
            }
            
            ScoreData score = parseScoreJson(scoreJson);
            if (score != null) {
                scores.add(score);
            }
        }
        
        return scores;
    }
    
    /**
     * Parses a JSON object into a ScoreData object.
     * This is a simple implementation and assumes well-formed JSON.
     *
     * @param json The JSON object string
     * @return A ScoreData object
     */
    private ScoreData parseScoreJson(String json) {
        if (json == null || json.isEmpty() || !json.startsWith("{")) {
            return null;
        }
        
        Long id = null;
        String playerName = null;
        int scoreValue = 0;
        LocalDateTime gameDate = LocalDateTime.now();
        
        // Extract id
        int idStart = json.indexOf("\"id\":");
        if (idStart >= 0) {
            idStart += 5; // Move past "id":
            int idEnd = json.indexOf(",", idStart);
            if (idEnd < 0) {
                idEnd = json.indexOf("}", idStart);
            }
            if (idEnd >= 0) {
                String idStr = json.substring(idStart, idEnd).trim();
                try {
                    id = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    logger.warning("Failed to parse id: " + idStr);
                }
            }
        }
        
        // Extract playerName
        int nameStart = json.indexOf("\"playerName\":");
        if (nameStart >= 0) {
            nameStart = json.indexOf("\"", nameStart + 13) + 1; // Move past "playerName":"
            int nameEnd = json.indexOf("\"", nameStart);
            if (nameEnd >= 0) {
                playerName = json.substring(nameStart, nameEnd);
            }
        }
        
        // Extract scoreValue
        int scoreStart = json.indexOf("\"scoreValue\":");
        if (scoreStart >= 0) {
            scoreStart += 13; // Move past "scoreValue":
            int scoreEnd = json.indexOf(",", scoreStart);
            if (scoreEnd < 0) {
                scoreEnd = json.indexOf("}", scoreStart);
            }
            if (scoreEnd >= 0) {
                String scoreStr = json.substring(scoreStart, scoreEnd).trim();
                try {
                    scoreValue = Integer.parseInt(scoreStr);
                } catch (NumberFormatException e) {
                    logger.warning("Failed to parse score: " + scoreStr);
                }
            }
        }
        
        // For simplicity, we're not parsing the gameDate field in this example
        
        if (playerName != null) {
            if (id != null) {
                return new ScoreData(id, playerName, scoreValue, gameDate);
            } else {
                return new ScoreData(playerName, scoreValue);
            }
        }
        
        return null;
    }
}

