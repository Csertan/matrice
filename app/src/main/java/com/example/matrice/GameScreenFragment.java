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

public class GameScreenFragment extends Fragment {

    private static final String TAG = GameScreenFragment.class.getSimpleName();

    private GameScreenViewModel mViewModel;
    private Game game;

    private TextView timer;
    private Handler timerHandler;
    private Runnable timerRunnable;

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
        //TODO Write documentation
        initGameUponPreferences();

        ImageButton toHomeButton = (ImageButton) view.findViewById(R.id.leftControlsHomeButton);
        toHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(MainNavGraphDirections.actionPopUpToMainScreenFragment());
            }
        });

        ImageButton pausePlayButton = (ImageButton) view.findViewById(R.id.rightControlsPausePlayButton);
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPausePlayButtonPressed(view);
            }
        });

        ImageButton stopButton = (ImageButton) view.findViewById(R.id.rightControlsStopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStopButtonPressed(view);
            }
        });

        ImageButton retryButton = (ImageButton) view.findViewById(R.id.rightControlsRetryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryButtonPressed(view);
            }
        });

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

    @Override
    public void onStart() {
        super.onStart();
        updateLayout(endLayout, this.game.getCurrentGame().getEndState());
        updateLayout(gameLayout, this.game.getCurrentGame().getCurrentState());
        this.game.start();
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.game.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.game.resume();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.game.stop();
        timerHandler.removeCallbacks(timerRunnable);
    }

    /* Handling User Commands*/

    //TODO Write documentation
    public void onPausePlayButtonPressed(View view) {
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

    //TODO Implement new game functionality after stop button pressed
    public void onStopButtonPressed(View view) {
        if(this.game.isGameStarted())
            this.game.stop();
    }

    public void onRetryButtonPressed(View view) {
        if(this.game.isGameStarted()) {
            this.game.restart();
            updateLayout(gameLayout, this.game.getCurrentGame().getCurrentState());
        }
    }


    private void initGameUponPreferences() {
        //TODO Write documentation
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        int boardSize = preferences.getInt(getString(R.string.key_game_boardsize), 3);
        int transformationId = Integer.parseInt(preferences.getString(getString(R.string.key_transition_type), "0"));
        int figureSetId = Integer.parseInt(preferences.getString(getString(R.string.key_figure_set), "0"));

        this.game = new Game(Transformation.fromId(transformationId), boardSize, FigureSet.fromId(figureSetId));
    }

    private void updateLayout(@NotNull GridLayout layout, @NotNull GameState state) {
        int count = layout.getChildCount();
        Boolean value;
        for(int i = 0; i < count; i++) {
            ImageView field = (ImageView) layout.getChildAt(i);
            value = state.getCell((i / 3), (i % 3));
            if(value) {
                field.setImageResource(R.drawable.ic_play_icon);
            }
            else
                field.setImageResource(R.drawable.ic_pause_icon);
        }
    }

}
