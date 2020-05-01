package matrice;

import androidx.annotation.Nullable;

import java.sql.Array;
import java.util.Arrays;
import java.util.Random;

/**
 * Class representing the current state of the game table
 *
 * Variables:
 * boardsize (int): the size of the game board. Can be 3 or higher.
 * state (Boolean[][]): the state of the table.
 */
public class GameState {

    private int boardSize;
    private Boolean[][] state;

    /* Static functions */

    /**
     *Static function that generates random state. Used to get arbitrary start and end states for a game.
     * @param boardSize Size of the game board.
     * @return Returns an arbitrary game state.
     */
    public static Boolean[][] generate(int boardSize) {
        Boolean[][] state = new Boolean[boardSize][boardSize];
        Random rand = new Random();

        for(int i = 0; i < boardSize; i++)
        {
            for(int j = 0; j < boardSize; j++) {
                state[i][j] = rand.nextBoolean();
            }
        }
        return state;
    }

    /* Constructors */

    /**
     * Base Constructor function. Initialises state with random values.
     * @param boardSize Size of the game table
     */
    public GameState(int boardSize) {
        this.setBoardSize(boardSize);
        this.state = GameState.generate(boardSize);
    }

    /**
     * Copy constructor for GameState objects
     * @param other The state that is wished to be copied to the new object.
     */
    public GameState(GameState other) {
        this.setBoardSize(other.getBoardSize());
        this.setState(other.getState());
    }

    /* Getters and Setters*/

    public int getBoardSize() {
        return boardSize;
    }
    private void setBoardSize(int boardSize) throws IllegalArgumentException {
        /* Board size can be 3 or higher */
        if(boardSize < 3)
        {
            throw new IllegalArgumentException("Invalid board size: " + boardSize);
        }
        this.boardSize = boardSize;
    }
    public Boolean[][] getState() {
        return state;
    }
    public void setState(Boolean[][] state) {
        this.state = state;
    }

    /* Other */

    /**
     * Updates the requested cell of the game state with the given value
     * @param row Row of the table to be updated.
     * @param col Column of the table to be updated.
     * @param value New value.
     */
    public void updateCell(int row, int col, Boolean value) throws IllegalArgumentException {
        if(value == null)
            throw new IllegalArgumentException("Value of a field cell can not be null.");
        this.state[row][col] = value;
    }

    /**
     * Gets the value of the wished cell of the game state
     * @param row Row of the table to be got.
     * @param col Column of the table to be got.
     * @return Value of the specified location.
     */
    public Boolean getCell(int row, int col) {
        return this.state[row][col];
    }

    /**
     * Calculates the integer format of the given current state. Used for logging user path.
     * @return Id of the current State.
     */
    public int getStateId() {
        int id = 0;
        int exponent = (boardSize * boardSize) - 1;
        for(int i = 0; i < this.boardSize; i++)
        {
            for(int j = 0; j < this.boardSize; j++)
            {
                if(this.state[i][j] == Boolean.TRUE)
                {
                    id += (int) Math.pow(2, exponent);
                }
                exponent--;
            }
        }
        return id;
    }

    /* Equality check */

    /**
     * Overrides the default equals() method
     * @param obj Object to be compared with this.
     * @return Result of the inspection.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        /* If compares to itself returns true */
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        /* If not an instance of the GameState class returns false*/
        if(!(obj instanceof GameState))
            return false;

        /* Casting type so that variables are comparable */
        GameState other = (GameState)obj;

        if(this.boardSize!=other.getBoardSize())
            return false;

        /* Checking state values */
        for(int i = 0; i < this.boardSize; i++)
        {
            for(int j = 0; j < this.boardSize; j++)
            {
                if(!(this.state[i][j].equals(other.getCell(i,j))))
                    return false;
            }
        }
        return true;
    }
}
