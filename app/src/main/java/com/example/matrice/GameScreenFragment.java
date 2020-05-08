package com.example.matrice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import matrice.FigureSet;
import matrice.Game;
import matrice.GameState;
import matrice.Transformation;

/**
 * Fragment responsible for the actual game
 */
public class GameScreenFragment extends Fragment {

    //TAG for Logging & Debugging
    private static final String TAG = GameScreenFragment.class.getSimpleName();

    private GameScreenViewModel mViewModel;
    private Game game;

    /**
     * Indicator used to switch between New Game / Stop Game buttons
     */
    private boolean isGameStopped = false;

    /**
     * Local variables to dynamically update the TextView showing elapsed time from the game start
     */
    private TextView timer;
    private Handler timerHandler;
    private Runnable timerRunnable;

    /**
     * Storing the gridLayouts to not have to look up them in every function call
     */
    private GridLayout gameLayout;
    private GridLayout endLayout;

    public static GameScreenFragment newInstance() {
        return new GameScreenFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_screen_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * Initialising Game Object upon saved Preferences
         */
        initGameUponPreferences();

        /**
         * Adds callback to Home Button which navigates the user to the Main Screen
         */
        ImageButton toHomeButton = (ImageButton) view.findViewById(R.id.leftControlsHomeButton);
        toHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uses popUpTo global action to remove Fragment instances from backstack
                Navigation.findNavController(view).navigate(MainNavGraphDirections.actionPopUpToMainScreenFragment());
            }
        });

        /**
         * Adds callback to Play/Pause Button to suspend/resume measuring elapsed time
         */
        ImageButton pausePlayButton = (ImageButton) view.findViewById(R.id.rightControlsPausePlayButton);
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPausePlayButtonPressed(view);
            }
        });

        /**
         * Adds callback to Stop/New Game Button to let the user stop the game or create new game
         */
        ImageButton stopButton = (ImageButton) view.findViewById(R.id.rightControlsStopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStopButtonPressed(view);
            }
        });

        /**
         * Adds callback to Retry Button in order to let the user go back to the start
         * states of the level
         */
        ImageButton retryButton = (ImageButton) view.findViewById(R.id.rightControlsRetryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryButtonPressed(view);
            }
        });

        /**
         * Handling TextView to displaying elapsed time in every second
         */
        timer = (TextView) view.findViewById(R.id.rightControlsGameDuration);
        timer.setText(this.game.getFormattedDuration());

        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timer.setText(game.getFormattedDuration());
                timerHandler.postDelayed(this, 1000);
            }
        };

        /**
         * Initialising local variables that store grid layouts
         */
        gameLayout = (GridLayout) view.findViewById(R.id.startStateLayout);
        endLayout = (GridLayout) view.findViewById(R.id.endStateLayout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(GameScreenViewModel.class);
        // TODO: Use the ViewModel
    }

    /* Handling Fragment Lifecycle Changes */

    /**
     * Overrides default onStart() method of Fragment.
     * Updates Grid Layouts with start and end states of the current Game.
     * Starts the Game - starts Stopwatch measuring time.
     */
    @Override
    public void onStart() {
        super.onStart();
        updateLayout(endLayout, this.game.getCurrentGame().getEndState());
        updateLayout(gameLayout, this.game.getCurrentGame().getStartState());
        this.game.start();
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    /**
     * Overrides default onPause() method of Fragment.
     * When Fragment's paused pauses the Game.
     */
    @Override
    public void onPause() {
        super.onPause();
        this.game.pause();
    }

    /**
     * Overrides default onResume() method of Fragment.
     * When the user gets back to the App resumes the Game.
     */
    @Override
    public void onResume() {
        super.onResume();
        this.game.resume();
    }

    /**
     * Overrides default onStop() method of Fragment.
     * When Fragment's about to be destroyed and gets stopped stops and logs out the Game.
     */
    @Override
    public void onStop() {
        super.onStop();
        this.game.stop();
        timerHandler.removeCallbacks(timerRunnable);
    }

    /* Handling User Commands*/

    /**
     * Callback function firing when Pause/Play Button is clicked.
     * @param view View which the click happened in. Default parameter of onClick callbacks.
     */
    public void onPausePlayButtonPressed(@NotNull View view) {
        ImageButton button = (ImageButton) view.findViewById(R.id.rightControlsPausePlayButton);
        if(!this.game.isGamePaused()) {
            this.game.pause();
            button.setImageResource(R.drawable.ic_play_icon);
        }
        else {
            this.game.resume();
            button.setImageResource(R.drawable.ic_pause_icon);
        }
    }

    /**
     * Callback function firing when Stop/New Game Button is clicked.
     * @param view View which the click happened in. Default parameter of onCLick callbacks.
     */
    public void onStopButtonPressed(@NotNull View view) {
        ImageButton button = (ImageButton) view.findViewById(R.id.rightControlsStopButton);
        if(this.game.isGameStarted()) {
            if(!this.isGameStopped) {
                //Stops current Game
                this.game.stop();
                this.isGameStopped = true;
                button.setImageResource(R.drawable.ic_new_game_icon);
            }
            else {
                //Initialises New Game
                initGameUponPreferences();
                updateLayout(endLayout, this.game.getCurrentGame().getEndState());
                updateLayout(gameLayout, this.game.getCurrentGame().getStartState());
                this.isGameStopped = false;
                button.setImageResource(R.drawable.ic_stop_icon);
            }
        }
    }

    /**
     * Callback function firing when Retry Button is clicked.
     * @param view View which the click happened in. Default parameter of onCLick callbacks.
     */
    public void onRetryButtonPressed(View view) {
        if(this.game.isGameStarted()) {
            this.game.restart();
            updateLayout(gameLayout, this.game.getCurrentGame().getCurrentState());
        }
    }

    /**
     * Creates and initialises a Game Object upon User Preferences stored in Shared Preferences.
     */
    private void initGameUponPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        int boardSize = preferences.getInt(getString(R.string.key_game_boardsize), 3);
        //ListPreference items are stored as Strings. To get int values we need to cast with Integer.parseInt()
        int transformationId = Integer.parseInt(preferences.getString(getString(R.string.key_transition_type), "0"));
        int figureSetId = Integer.parseInt(preferences.getString(getString(R.string.key_figure_set), "0"));

        //To pass correct instances of the Enum types we need to cast ints with .fromInt()
        this.game = new Game(Transformation.fromId(transformationId), boardSize, FigureSet.fromId(figureSetId));
    }

    /**
     * Updates layout with the appropriate figures in each cell of the Game board matrix
     * @param layout Layout to be updated (GridLayout)
     * @param state State with which the layout will be updated (GameState)
     * TODO Implement functionality to switch between Figure Sets and update icons accordingly
     * TODO Change figures from that icons to the real ones
     */
    private void updateLayout(@NotNull GridLayout layout, @NotNull GameState state) {
        int count = layout.getChildCount();
        Boolean value;
        int boardSize = this.game.getCurrentGame().getCurrentState().getBoardSize();
        //Loops through the Childs of the Layout
        for(int i = 0; i < count; i++) {
            ImageView field = (ImageView) layout.getChildAt(i);
            //Gets the value of the appropriate board Cell and sets the Figure
            value = state.getCell((i / boardSize), (i % boardSize));
            if(value) {
                field.setImageResource(R.drawable.ic_play_icon);
            }
            else
                field.setImageResource(R.drawable.ic_pause_icon);
        }
    }

}
