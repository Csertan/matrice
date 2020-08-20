package com.nosebite.matrice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameHelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameHelpFragment extends Fragment {

    private String previousGame;

    public GameHelpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment GameHelpFragment.
     */
    public static GameHelpFragment newInstance() {
        return new GameHelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* Sets onClick behaviour for the close button */
        ImageButton closeButton = (ImageButton) view.findViewById(R.id.closeButtonGameHelp);
        closeButton.setOnClickListener(v -> {
            //Sets the data of the previous game as a result for GameScreenFragment
            Bundle previousGameData = new Bundle();
            previousGameData.putString("previousGame", previousGame);
            getParentFragmentManager().setFragmentResult("replayGameData", previousGameData);

            Navigation.findNavController(v).navigate(GameHelpFragmentDirections.actionGameHelpFragmentToGameScreenFragment());
        });

        /*
         * Adds callback that listens to the results from the Game Fragment.
         */
        getParentFragmentManager()
                .setFragmentResultListener("gameData", this, (FragmentResultListener) (requestKey, result) -> {
                    String elapsedTime = result.getString("elapsedTime");
                    String score = result.getString("score");
                    String stepSize = result.getString("stepSize");

                    previousGame = result.getString("previousGame");
                });
    }
}