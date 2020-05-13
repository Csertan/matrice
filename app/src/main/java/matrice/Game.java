package matrice;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * Class for a Game upon given Settings, handles game control, timing and logging out statistics
 */
public class Game {

    private GameLevel currentGame;

    private FigureSet figureSet;
    private long startTime;
    private StopWatch stopWatch;
    private boolean isGameStarted;
    private boolean isGamePaused;

    /* Constructors */

    /**
     * Basic constructor for a Game object
     * @param transformation Type of the transformation of the move
     * @param boardSize Size of the game board
     * @param figureSet Set of figures on the game board screen
     */
    public Game(Transformation transformation, int boardSize, FigureSet figureSet) {
        //Initialises GameLevel with random start-end states
        this.currentGame = new GameLevel(transformation, boardSize);
        this.setFigureSet(figureSet);
        this.setGameStarted(false);
        this.setGamePaused(true);
        this.setStartTime(0);
        this.stopWatch = new StopWatch();
    }

    /**
     * String based creation of a Game to be able to continue saved game
     * @param transformation Type of the transformation of the move
     * @param boardSize Size of the game board
     * @param figureSet Set of figures on the game board screen
     * @param savedState String that stores the start, end and current states
     */
    public Game(Transformation transformation, int boardSize, FigureSet figureSet, String savedState) {
        this.currentGame = new GameLevel(transformation, boardSize, savedState);
        this.setFigureSet(figureSet);
        this.setGameStarted(false);
        this.setGamePaused(true);
        this.setStartTime(0);
        this.stopWatch = new StopWatch();
    }

    /* Getters and Setters */

    public GameLevel getCurrentGame() {
        return currentGame;
    }
    public FigureSet getFigureSet() {
        return figureSet;
    }
    public boolean isGameStarted() {
        return isGameStarted;
    }
    public boolean isGamePaused() {
        return isGamePaused;
    }
    public long getStartTime() {
        return startTime;
    }
    public long getDuration() {
        if(this.stopWatch.isStarted())
            return stopWatch.getTime(TimeUnit.SECONDS);
        return 0;
    }

    /**
     * Function that gives the elapsed time as a formatted String.
     * @return Returns a MM:SS formatted value
     */
    public String getFormattedDuration() {
        String formattedTime = "";
        long unformatted = stopWatch.getTime(TimeUnit.SECONDS);
        if(unformatted > 59)
        {
            formattedTime += (unformatted / 60) + ":";
            if((unformatted % 60) < 10 )
            {
                formattedTime += "0" + (unformatted % 60);
            }
            else
                formattedTime += (unformatted % 60);
        }
        else
            formattedTime += unformatted;
        return formattedTime;
    }

    /**
     * Calculates score earned by the current game.
     * @return integer value from 50 to 6050
     */
    public int getScore() {
        double score = 50 + 1000.0 / this.getCurrentGame().getStepSize() + 5000.0 / this.getDuration();
        return (int)score;
    }

    public void setCurrentGame(GameLevel currentGame) {
        this.currentGame = currentGame;
    }
    private void setFigureSet(FigureSet figureSet) {
        this.figureSet = figureSet;
    }
    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }
    private void setGamePaused(boolean gamePaused) {
        isGamePaused = gamePaused;
    }
    private void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /* Game Control */

    /**
     * Starts the game. Starts timers.
     */
    public void start() {
        this.setGameStarted(true);
        this.setGamePaused(false);
        this.setStartTime(System.currentTimeMillis());
        stopWatch.start();
    }

    /**
     * Pauses game, halts timers. When game is stopped players can not make moves.
     */
    public void pause() {
        if(!this.isGamePaused)
        {
            this.stopWatch.suspend();
        }
        this.setGamePaused(true);
    }

    /**
     * Resumes game, continues timers.
     */
    public void resume() {
        if(this.isGamePaused)
        {
            this.stopWatch.resume();
        }
        this.setGamePaused(false);
    }

    /**
     * Restarts game upon user action.
     */
    public void restart() {
        if(this.isGameStarted) {
            this.startTime = System.currentTimeMillis();
            this.stopWatch.reset();
            this.stopWatch.start();
            this.setGamePaused(false);
            this.setGameStarted(true);
            if (this.currentGame.getStepSize() > 1) {
                this.currentGame.restart();
            }
        }
    }

    /**
     * Stops the game.
     */
    public void stop() {
        if(!this.isGamePaused()) {
            this.stopWatch.stop();
            this.setGamePaused(true);
        }
    }

    /**
     * Creates new game upon user action.
     * @param transformation Type of the transformation of the move
     * @param boardSize Size of the game board
     * @param figureSet Set of figures on the game board screen
     */
    public void newGame(Transformation transformation, int boardSize, FigureSet figureSet) {
        this.currentGame = new GameLevel(transformation, boardSize);
        this.setFigureSet(figureSet);
        this.setStartTime(System.currentTimeMillis());
        this.stopWatch.reset();
        this.setGamePaused(true);
        this.setGameStarted(false);
    }

    /**
     * Handles moves of the player. Checks if the game is finished (the end state is reached).
     * @param move Specifies the direction of the move.
     * @param id Specifies affected row/column.
     * @return Returns true when game is finished, false otherwise.
     */
    public boolean handleMove(Move move, int id) {
        if(!this.isGamePaused && isGameStarted)
        {
            this.currentGame.handleMove(move, id);
        }
        return this.currentGame.isFinished();
    }

    /* Logging */

    /**
     * Creates a JSON formatted String from the Game data.
     * @return JSON formatted String, used when Game finishes.
     */
    public String gameToJson() {
        StringBuilder output = new StringBuilder();
        output.append("{ ");
        output.append(getCurrentGame().levelToJson());
        output.append("\"startTime\" : \"").append(getStartTime()).append("\", ");
        output.append("\"gameDuration\" : ").append(getDuration()).append(" }");
        return output.toString();
    }
}
