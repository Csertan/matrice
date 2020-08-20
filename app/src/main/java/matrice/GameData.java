package matrice;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder class for Game Data.
 * Used to send information to the Firebase Realtime Database in JSON format.
 */
@IgnoreExtraProperties
public class GameData {

    private int startState;
    private int endState;
    private String stateChain;
    private int chainLength;
    private int boardSize;
    private long startTime;
    private long duration;

    private int levelNumber;
    private int maxScore;

    /**
     * Default constructor required to be able to get data.
     */
    GameData() {}

    public GameData(int startState, int endState, String stateChain, int chainLength,
                    int boardSize, long startTime, long duration)
    {
        this.startState = startState;
        this.endState = endState;
        this.stateChain = stateChain;
        this.chainLength = chainLength;
        this.boardSize = boardSize;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getBoardSize() {
        return boardSize;
    }
    public int getChainLength() {
        return chainLength;
    }
    public int getEndState() {
        return endState;
    }
    public int getStartState() {
        return startState;
    }
    public long getDuration() {
        return duration;
    }
    public String getStateChain() {
        return stateChain;
    }
    public long getStartTime() {
        return startTime;
    }
    public int getLevelNumber() {
        return levelNumber;
    }
    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Writes the data into a consumable format for the Database.
     * @return HashMap containing the Game Data.
     */
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("startState", startState);
        data.put("endState", endState);
        data.put("stateChain", stateChain);
        data.put("chainLength", chainLength);
        data.put("boardSize", boardSize);
        data.put("startTime", startTime);
        data.put("duration", duration);
        return data;
    }
}
