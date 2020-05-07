package com.example.matrice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import matrice.FigureSet;
import matrice.Game;
import matrice.Transformation;

public class GameScreenFragment extends Fragment {

    private GameScreenViewModel mViewModel;
    private Game game;

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
        this.game.start();
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
    }

    /* Handling User Commands*/

    //TODO Write documentation
    public void onPausePlayButtonPressed(View view) {
        if(!this.game.isGamePaused()) {
            this.game.pause();
            //TODO update Button image
        }
        else {
            this.game.resume();
            //update Button image
        }
    }

    public void onStopButtonPressed(View view) {
        if(this.game.isGameStarted())
            this.game.stop();
    }

    public void onRetryButtonPresse(View view) {
        if(this.game.isGameStarted())
            this.game.restart();
    }


    private void initGameUponPreferences() {
        //TODO Write documentation
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        int boardsize = preferences.getInt(getString(R.string.key_game_boardsize), 3);
        int transformationId = preferences.getInt(getString(R.string.key_transition_type), 0);
        int figureSetId = preferences.getInt(getString(R.string.key_figure_set), 0);

        this.game = new Game(Transformation.fromId(transformationId), boardsize, FigureSet.fromId(figureSetId));
    }

}
