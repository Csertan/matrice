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

    //TODO Implement String based creation of Game to be able to continue saved game
    public Game(Transformation transformation, int boardSize, FigureSet figureSet, String savedState) {

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
    public String getFormattedDuration() {
        String formattedTime = "";
        long unformatted = stopWatch.getTime(TimeUnit.SECONDS);
        if(unformatted > 59)
        {
            formattedTime += (unformatted / 60) + ":" + (unformatted % 60);
        }
        else
            formattedTime += unformatted;
        return formattedTime;
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
        this.startTime = System.currentTimeMillis();
        this.stopWatch.reset();
        if(this.currentGame.getStepSize() > 1)
        {
            this.currentGame.restart();
        }
    }

    /**
     * Stops the game. Available publicly in contrast with finishGame() function.
     */
    public void stop() {
        this.finishGame();
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
    }

    /**
     * Finishes game. Stops timer and writes out statistics.
     */
    private void finishGame() {
        this.stopWatch.stop();
        this.setGamePaused(true);
        this.logGame();
    }

    /**
     * Handles moves of the player. Checks if the game is finished (the end state is reached).
     * @param move Specifies the direction of the move.
     * @param id Specifies affected row/column.
     */
    public void handleMove(Move move, int id) {
        if(!this.isGamePaused && isGameStarted)
        {
            this.currentGame.handleMove(move, id);
        }
        if(this.currentGame.isFinished())
        {
            this.finishGame();
        }
    }

    /* Logging */
    //TODO Implement logging
    public void logGame() {

    }
}
