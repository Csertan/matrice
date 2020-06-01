package com.nosebite.matrice;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.gridlayout.widget.GridLayout;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import matrice.FigureSet;
import matrice.Game;
import matrice.GameData;
import matrice.GameState;
import matrice.Move;
import matrice.Transformation;

/**
 * Fragment responsible for the actual game
 */
public class GameScreenFragment extends Fragment {

    //TAG for Logging & Debugging
    private static final String TAG = GameScreenFragment.class.getSimpleName();

    private MainActivity mainActivity;
    private String userId;

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
     *  Local variable to display stepCount to the screen
     */
    private TextView stepCounter;

    /**
     * Storing the gridLayouts to not have to look up them in every function call
     */
    private GridLayout gameLayout;
    private GridLayout endLayout;

    /**
     * Detector for detecting common user gestures. Used on the Game Board where users can swipe to
     * move.
     */
    private GestureDetectorCompat mDetector;

    private DatabaseReference dataBase;

    public static GameScreenFragment newInstance() {
        return new GameScreenFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_screen_fragment, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /* Initialising Game Object upon saved Preferences */
        initGameUponPreferences(null);

        /* Requiring player id from Main Activity */
        mainActivity = (MainActivity) getActivity();
        if(mainActivity != null) {
            userId = mainActivity.getUserID();
        }

        /* Get Instance of Firebase Database Reference */
        dataBase = FirebaseDatabase.getInstance().getReference();

        /* Adds callback to Home Button which navigates the user to the Main Screen */
        ImageButton toHomeButton = (ImageButton) view.findViewById(R.id.leftControlsHomeButton);
        toHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uses popUpTo global action to remove Fragment instances from backstack
                Navigation.findNavController(view).navigate(MainNavGraphDirections.actionPopUpToMainScreenFragment());
            }
        });

        /* Adds callback to Play/Pause Button to suspend/resume measuring elapsed time */
        ImageButton pausePlayButton = (ImageButton) view.findViewById(R.id.rightControlsPausePlayButton);
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPausePlayButtonPressed(view);
            }
        });

        /* Adds callback to Stop/New Game Button to let the user stop the game or create new game */
        ImageButton stopButton = (ImageButton) view.findViewById(R.id.rightControlsStopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStopButtonPressed(view);
            }
        });

        /* Adds callback to Retry Button in order to let the user go back to the start
          states of the level */
        ImageButton retryButton = (ImageButton) view.findViewById(R.id.rightControlsRetryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryButtonPressed(view);
            }
        });

        /* Adds callback to Levels Button to navigate the user to the Level choser Screen */
        ImageButton levelsButton = (ImageButton) view.findViewById(R.id.leftControlsLevelsButton);
        levelsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Navigate to Levels Screen
                Toast.makeText(getContext(), getString(R.string.feature_coming_soon), Toast.LENGTH_SHORT).show();
            }
        });

        /* Adds callback to Back Button to navigate the user down in the back stack */
        ImageButton backButton = (ImageButton) view.findViewById(R.id.leftControlsBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        /* Handling TextView to displaying elapsed time in every second */
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

        /* Displaying stepCount */
        stepCounter = (TextView) view.findViewById(R.id.topDetailsStepCount);
        stepCounter.setText(Integer.toString(this.game.getCurrentGame().getStepSize()));

        /* Initialising local variables that store grid layouts */
        gameLayout = (GridLayout) view.findViewById(R.id.startStateLayout);
        endLayout = (GridLayout) view.findViewById(R.id.endStateLayout);

        /* Apply Gesture listener */
        mDetector = new GestureDetectorCompat(getActivity(), new FlingGestureListener());

        /* Setting on touch listener to the Game Board */
        gameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });

        /* Getting previous game when returning from success screen to replay level. */
        getParentFragmentManager()
                .setFragmentResultListener("replayGameData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String previousGame = result.getString("previousGame");
                initGameUponPreferences(previousGame);
                updateLayout(endLayout, game.getCurrentGame().getEndState());
                updateLayout(gameLayout, game.getCurrentGame().getStartState());
                game.start();
                timerHandler.postDelayed(timerRunnable, 1000);
            }
        });
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
    private void onPausePlayButtonPressed(@NotNull View view) {
        ImageButton button = (ImageButton) view.findViewById(R.id.rightControlsPausePlayButton);
        if(!this.isGameStopped) {
            if (!this.game.isGamePaused()) {
                this.game.pause();
                button.setImageResource(R.drawable.ic_play_icon);
            } else {
                this.game.resume();
                button.setImageResource(R.drawable.ic_pause_icon);
            }
        }
    }

    /**
     * Callback function firing when Stop/New Game Button is clicked.
     * @param view View which the click happened in. Default parameter of onCLick callbacks.
     */
    private void onStopButtonPressed(@NotNull View view) {
        ImageButton stopButton = (ImageButton) view.findViewById(R.id.rightControlsStopButton);
        if(this.game.isGameStarted()) {
            if(!this.isGameStopped) {
                //Stops current Game
                this.game.stop();
                this.isGameStopped = true;
                stopButton.setImageResource(R.drawable.ic_new_game_icon);
            }
            else {
                //Initialises New Game
                initGameUponPreferences(null);
                updateLayout(endLayout, this.game.getCurrentGame().getEndState());
                updateLayout(gameLayout, this.game.getCurrentGame().getStartState());
                this.game.start();
                this.isGameStopped = false;
                stopButton.setImageResource(R.drawable.ic_stop_icon);
            }
        }
    }

    /**
     * Callback function firing when Retry Button is clicked.
     * @param view View which the click happened in. Default parameter of onCLick callbacks.
     */
    private void onRetryButtonPressed(View view) {
        if(this.game.isGameStarted()) {
            this.game.restart();
            this.isGameStopped = false;
            updateLayout(gameLayout, this.game.getCurrentGame().getCurrentState());
        }
    }

    /**
     * Handles user swipes.
     * @param move Direction of the swipe. Types specified in {@link Move} class.
     * @param id Identity of the row or column on which the swipe occurs.
     */
    private void onSwipe(Move move, Transformation transformation, int id) {
        //Handles swipe
        boolean finished = this.game.handleMove(move, transformation, id);
        updateLayout(gameLayout, this.game.getCurrentGame().getCurrentState());
        stepCounter.setText(Integer.toString(this.game.getCurrentGame().getStepSize()));
        //If the game is finished stops it and navigates the user to the Success Screen
        if(finished) {
            this.game.stop();
            this.isGameStopped = true;

            gameToDatabase();

            setScoreDetails();
            Navigation.findNavController(this.getView())
                    .navigate(GameScreenFragmentDirections.actionGameScreenFragmentToSuccessScreenFragment());
        }
    }

    /* Helper functions and Classes */

    /**
     * Creates and initialises a Game Object upon User Preferences stored in Shared Preferences.
     */
    private void initGameUponPreferences(@Nullable String savedState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        int boardSize = preferences.getInt(getString(R.string.key_game_boardsize), 3);
        //ListPreference items are stored as Strings. To get int values we need to cast with Integer.parseInt()
        int figureSetId = Integer.parseInt(preferences.getString(getString(R.string.key_figure_set), "0"));

        //If there is a saved state start from there, else start new random game
        if(savedState != null)
        {
            //To pass correct instances of the Enum types we need to cast ints with .fromId()
            this.game = new Game(boardSize, FigureSet.fromId(figureSetId), savedState);
        }
        else {
            //To pass correct instances of the Enum types we need to cast ints with .fromId()
            this.game = new Game(boardSize, FigureSet.fromId(figureSetId));
        }
    }

    /**
     * Updates layout with the appropriate figures in each cell of the Game board matrix
     * @param layout Layout to be updated (GridLayout)
     * @param state State with which the layout will be updated (GameState)
     * TODO Implement functionality to switch between Figure Sets and update icons accordingly
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
                field.setImageResource(R.drawable.ic_figure_o);
            }
            else
                field.setImageResource(R.drawable.ic_figure_x);
        }
    }

    /**
     * Upon finishing game the function sets the score details needed to be sent to
     * {@link SuccessScreenFragment}.
     * FragmentResult is a feature in androidx.fragment:1.3.0-alpha04 version. Might be not stable.
     */
    private void setScoreDetails() {
        Bundle result = new Bundle();
        result.putString("elapsedTime", this.game.getFormattedDuration());
        result.putString("score", String.valueOf(this.game.getScore()));
        result.putString("stepSize", String.valueOf(this.game.getCurrentGame().getStepSize()));

        /* Deleting current state (that equals to end state) from Previous Game and replacing
         it with start state */
        String[] previousGameTemp = this.game.getCurrentGame().toString().split(":");
        String previousGame = previousGameTemp[0] + ":" + previousGameTemp[1] + ":" + previousGameTemp[0];
        result.putString("previousGame", previousGame);

        getParentFragmentManager().setFragmentResult("gameData", result);
    }

    /**
     * Writes the game into the Firebase Realtime Database.
     */
    private void gameToDatabase() {
        GameData gameData = new GameData(game.getCurrentGame().getStartState().getStateId(),
                game.getCurrentGame().getEndState().getStateId(),
                game.getCurrentGame().sequenceToString(),
                game.getCurrentGame().getStepSize(),
                game.getCurrentGame().getStartState().getBoardSize(),
                game.getStartTime(),
                game.getDuration());
        Map<String, Object> gameDataValues = gameData.toMap();

        DatabaseReference userReference = dataBase.child("users").child(userId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    dataBase.child("users").child(userId).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
            }
        });

        String key = dataBase.child("games").child(userId).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/games/" + userId + "/" + key, gameDataValues);
        dataBase.updateChildren(childUpdates);
    }

    /**
     * Gesture Listener class used to handle common user gestures such as swipes.
     */
    class FlingGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        /* Constants that help decide whether the Motion has to be handled */
        private static final int SWIPE_MIN_DISTANCE = 20;
        private static final int SWIPE_THRESHOLD_VELOCITY = 10;

        /* Needed to Override onDown() method to listen to any motion */
        @Override
        public boolean onDown(@NotNull MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        /* Overrides default onFling method */
        @Override
        public boolean onFling(@NotNull MotionEvent event1, @NotNull MotionEvent event2,
                               float velocityX, float velocityY) {
            // Log.d(DEBUG_TAG, "onFLing: " + event1.toString() + event2.toString());

            float x1 = event1.getX();
            float y1 = event1.getY();
            float x2 = event2.getX();
            float y2 = event2.getY();

            if(Math.abs(x1 - x2) < SWIPE_MIN_DISTANCE || Math.abs(y1 - y2) < SWIPE_MIN_DISTANCE
                    || Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY
                    || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY)
            {
                return false;
            }

            double angle = getAngle(x1, y1, x2, y2);
            Move move = Move.fromAngle(angle);
            Transformation transformation = Transformation.fromAngle(angle);
            int id = getMoveId(move, x1, y1);
            onSwipe(move, transformation, id);
            return true;
        }

        /**
         * Calculates the angle of the motion.
         * @param x1 x coordinate of the start event
         * @param y1 y coordinate of the start event
         * @param x2 x coordinate of the end event
         * @param y2 y coordinate of the end event
         * @return (double) angle of the motion
         */
        private double getAngle(float x1, float y1, float x2, float y2) {
            double angleInDegrees = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
            if(angleInDegrees < 0) {
                return 360 + angleInDegrees;
            }
            else return angleInDegrees;
        }

        /**
         * Calculates the id of the row or column on which the motion started
         * @param move Type of the move
         * @param x1 x coordinate of the start event
         * @param y1 y coordinate of the start event
         * @return (int) id of the row/column affected by user swipe
         */
        @Contract(pure = true)
        private int getMoveId(@NotNull Move move, float x1, float y1) {
            int gameLayoutSize = getParentFragment().getView().findViewById(R.id.startStateLayout).getWidth();
            int boardSize = game.getCurrentGame().getCurrentState().getBoardSize();
            int scale = gameLayoutSize / boardSize;
            switch (move) {
                case HORIZONTAL:
                    return (int) (y1 / scale);
                case VERTICAL:
                    return (int) (x1 / scale);
                default:
                    return 0;
            }
        }
    }

}