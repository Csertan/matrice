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
    private boolean isGameStopped;

    /* Constructors */

    /**
     * Basic constructor for a Game object
     * @param transformation Type of the transformation of the move
     * @param boardSize Size of the game board
     * @param figureSet Set of figures on the game board screen
     */
    public Game(Transformation transformation, int boardSize, FigureSet figureSet) {
        this.currentGame = new GameLevel(transformation, boardSize);
        this.setFigureSet(figureSet);
        this.setGameStopped(true);
        this.setStartTime(0);
        this.stopWatch = new StopWatch();
    }

    //TODO Implement String based creation of Game to be able to continue saved game
    public Game(Transformation transformation, int boardSize, FigureSet figureSet, String savedState) {

    }

    /* Getters and Setters */

    public FigureSet getFigureSet() {
        return figureSet;
    }
    public boolean isGameStopped() {
        return isGameStopped;
    }
    public long getStartTime() {
        return startTime;
    }
    public long getDuration() {
        return stopWatch.getTime(TimeUnit.SECONDS);
    }
    public String getFormattedTime() {
        String formattedTime = "";
        long unformatted = stopWatch.getTime(TimeUnit.SECONDS);
        if(unformatted > 59)
        {
            formattedTime += unformatted/60 + ":" + unformatted%60;
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
    private void setGameStopped(boolean gameStopped) {
        isGameStopped = gameStopped;
    }
    private void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /* Game Control */

    public void start() {
        this.setGameStopped(false);
        this.setStartTime(System.currentTimeMillis());
        stopWatch.start();
    }

    /**
     * Pauses game, halts timers. When game is stopped players can not make moves.
     */
    public void pause() {
        if(!this.isGameStopped)
        {
            this.stopWatch.suspend();
        }
        this.setGameStopped(true);
    }

    /**
     * Resumes game, continues timers.
     */
    public void resume() {
        if(this.isGameStopped)
        {
            this.stopWatch.resume();
        }
        this.setGameStopped(false);
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
        this.setGameStopped(true);
        this.logGame();
    }

    /**
     * Handles moves of the player. Checks if the game is finished (the end state is reached).
     * @param move Specifies the direction of the move.
     * @param id Specifies affected row/column.
     */
    public void handleMove(Move move, int id) {
        if(!this.isGameStopped)
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
