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

    private Transformation transformation;

    /* Constructors */

    /**
     * Basic constructor for GameLevel objects. Generates random start and end states.
     * @param transformation Type of the transformation used to evolve the game table.
     * @param boardSize Size of the game board.
     */
    GameLevel(Transformation transformation, int boardSize) {
        this.setTransformation(transformation);

        //Initialising Level with random start-end states
        this.startState = new GameState(boardSize);
        this.endState = new GameState(boardSize);
        //Copying start state to current state
        this.currentState = new GameState(this.startState);

        this.sequence = new ArrayList<>();
        sequence.add(this.startState);
    }

    //TODO Write documentation
    GameLevel(Transformation transformation, int boardSize, @NotNull String savedLevel) throws IllegalArgumentException {
        this.setTransformation(transformation);
        String[] tokens = savedLevel.split(":");
        if(tokens.length != 3)
            throw new IllegalArgumentException("Not enough states to initialise level.");
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
    public Transformation getTransformation() {
        return transformation;
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
    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }

    /* Handling moves */

    /**
     * Calculating the Inversion type transformation of the game table
     * @param move Specifies the direction of the move.
     * @param id Specifies affected row / column.
     * @throws IllegalArgumentException Throws exception when finding invalid id.
     */
    private void calculateInversion(Move move, int id) throws IllegalArgumentException {
        if(id < 0 || id > this.currentState.getBoardSize()-1)
            throw new IllegalArgumentException("Invalid selector for move.");
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
     * Calculates the rotation type transformation of the game table. NOT IMPLEMENTED IN THIS VERSION.
     * @param move Specifies the direction of the move.
     * @param id Specifies affected row / column.
     */
    private void calculateRotation(Move move, int id) {

    }

    /**
     * Handles the players move. Available outside of the class.
     * @param move Direction of the move.
     * @param id Specifies the affected row / column.
     */
    void handleMove(Move move, int id) {
        switch (this.transformation) {
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
    //TODO Implement logging
    public void logLevel() {

    }

    //TODO Write documentation
    @NonNull
    @Override
    public String toString() {
        return this.startState.toString() + ":" +
                this.endState.toString() + ":" +
                this.currentState.toString();
    }

}
