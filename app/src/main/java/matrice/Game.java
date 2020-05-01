package matrice;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class Game {

    private GameLevel currentGame;

    private FigureSet figureSet;
    private long startTime;
    private StopWatch stopWatch;
    private boolean gameStopped;

    /* Constructors */
    public Game(Transformation transformation, int boardSize, FigureSet figureSet) {
        this.currentGame = new GameLevel(transformation, boardSize);
        this.figureSet = figureSet;
        this.gameStopped = false;
        this.startTime = System.currentTimeMillis();
        this.stopWatch = new StopWatch();
        stopWatch.start();
    }

    /* Getters and Setters */

    public FigureSet getFigureSet() {
        return figureSet;
    }
    public boolean isGameStopped() {
        return gameStopped;
    }
    public long getStartTime() {
        return startTime;
    }
    public long getDuration() {
        return stopWatch.getTime(TimeUnit.SECONDS);
    }

    public void setCurrentGame(GameLevel currentGame) {
        this.currentGame = currentGame;
    }
    public void setFigureSet(FigureSet figureSet) {
        this.figureSet = figureSet;
    }
    public void setTransformation(Transformation transformation) {
        this.currentGame.setTransformation(transformation);
    }

    /* Game Control */
    public void pause() {
        this.stopWatch.suspend();
    }
    public void resume() {
        this.stopWatch.resume();
    }
    public void restart() {
        this.startTime = System.currentTimeMillis();
        this.stopWatch.reset();
        this.currentGame.restart();
    }

    //TODO Implement new() and finish()
}
