package matrice;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Class handling a Game Level, start and end states, current state, moves and history
 */
public class GameLevel {

    private GameState startState;
    private GameState endState;
    private GameState currentState;
    private ArrayList<GameState> sequence;

    /* Constructors */

    /**
     * Basic constructor for GameLevel objects. Generates random start and end states.
     * @param boardSize Size of the game board.
     */
    GameLevel(int boardSize) {

        //Initialising Level with random start-end states
        this.startState = new GameState(boardSize);
        this.endState = new GameState(boardSize);
        //Copying start state to current state
        this.currentState = new GameState(this.startState);

        this.sequence = new ArrayList<>();
        sequence.add(this.startState);
    }

    /**
     * String based constructor for restoring saved game level in order to continue it.
     * @param boardSize Size of the game board.
     * @param savedLevel String that stores the start, end and current states in this order.
     * @throws IllegalArgumentException If savedLevel does not contains all three states throws
     * exception.
     */
    GameLevel(int boardSize, @NotNull String savedLevel) throws IllegalArgumentException {
        String[] tokens = savedLevel.split(":");
        if(tokens.length != 3)
            throw new IllegalArgumentException("Not enough states to initialise level.");
        //Initialises States with the appropriate state strings
        this.startState = new GameState(tokens[0], boardSize);
        this.endState = new GameState(tokens[1], boardSize);
        this.currentState = new GameState(tokens[2], boardSize);

        this.sequence = new ArrayList<>();
        sequence.add(this.startState);
    }

    /* Getters and Setters */

    public GameState getCurrentState() {
        return currentState;
    }
    public GameState getEndState() {
        return endState;
    }
    public GameState getStartState() {
        return startState;
    }
    public int getStepSize() {
        return sequence.size();
    }

    private void setCurrentState(GameState state) {
        this.currentState = state;
    }
    private void setEndState(GameState state) {
        this.endState = state;
    }
    private void setStartState(GameState state) {
        this.startState = state;
    }

    /* Handling moves */

    /**
     * Calculating the Inversion type transformation of the game table.
     * @param move Specifies the direction of the move.
     * @param id Specifies affected row / column.
     * @throws IllegalArgumentException Throws exception when finding invalid id.
     */
    private void calculateInversion(Move move, int id) throws IllegalArgumentException {
        if(id < 0 || id > this.currentState.getBoardSize()-1)
            throw new IllegalArgumentException("Invalid selector for row/column.");
        switch (move) {
            case VERTICAL:
                for(int i = 0; i < this.currentState.getBoardSize(); i++)
                    this.currentState.invertCell(i, id);
                break;
            case HORIZONTAL:
                for(int i = 0; i < this.currentState.getBoardSize(); i++)
                    this.currentState.invertCell(id, i);
                break;
            case LEFTABRIGHTBE:
                for(int i = 0; i < this.currentState.getBoardSize(); i++)
                    this.currentState.invertCell(i,i);
                break;
            case LEFTBERIGHTAB:
                for(int i = 0; i < this.currentState.getBoardSize(); i++)
                    this.currentState.invertCell(i, this.currentState.getBoardSize()-1-i);
                break;
        }
    }

    /**
     * Calculates the rotation type transformation of the game table.
     * @param move Specifies the direction of the move.
     * @param id Specifies affected row / column.
     * @throws IllegalArgumentException Throws exception when finding invalid id.
     */
    private void calculateRotation(Move move, int id) throws IllegalArgumentException {
        if(id < 0 || id > this.getCurrentState().getBoardSize()-1)
            throw new IllegalArgumentException("Invalid selector for row/column.");
        switch (move) {
            case VERTICAL:
                this.currentState.rotateColumn(id);
                break;
            case HORIZONTAL:
                this.currentState.rotateRow(id);
                break;
                // No diagonal rotation as its effect can be achieved by rotating rows and columns
        }
    }

    /**
     * Handles the players move. Available outside of the class.
     * @param move Direction of the move.
     * @param id Specifies the affected row / column.
     */
    void handleMove(Move move, @NotNull Transformation transformation, int id) {
        switch (transformation) {
            case INVERT:
                calculateInversion(move, id);
                break;
            case ROTATE:
                calculateRotation(move, id);
        }
        this.sequence.add(currentState);
    }

    /* Game Control */

    /**
     * Restarts the current Level.
     */
    void restart() {
        this.setCurrentState(this.getStartState());
        this.sequence.clear();
        this.sequence.add(this.getStartState());
    }

    /**
     * Tells if the player won the game.
     * @return Returns true if current state equals the end state.
     */
    boolean isFinished() {
        return this.currentState.equals(this.endState);
    }

    /* Logging */

    /**
     * Creates a formatted String from the Game Level object data.
     * @return JSON formatted string that stores the start and end states, the state chain,
     * the length of the chain and the board size of the game.
     */
    String levelToJson() {
        StringBuilder output = new StringBuilder();
        output.append("\"startState\" : ").append(getStartState().getStateId()).append(", ");
        output.append("\"endState\" : ").append(getEndState().getStateId()).append(", ");
        output.append("\"stateChain\" : [ ");
        for(int i = 0; i < sequence.size(); i++) {
            output.append(sequence.get(i).getStateId()).append(", ");
        }
        output.append("], ");
        output.append("\"chainLength\" : ").append(getStepSize()).append(", ");
        output.append("\"boardSize\" : ").append(getStartState().getBoardSize()).append(", ");
        return output.toString();
    }

    /**
     * Writes the essential data of the game to a String. Used to save a game to be able to
     * continue it.
     * @return String in startState:endState:currentState format.
     */
    @NonNull
    @Override
    public String toString() {
        return this.startState.toString() + ":" +
                this.endState.toString() + ":" +
                this.currentState.toString();
    }

}
