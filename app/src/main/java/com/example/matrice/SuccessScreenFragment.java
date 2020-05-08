package com.example.matrice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass that displays a Success Screen upon finishing the actual Level.
 */
public class SuccessScreenFragment extends Fragment {

    public SuccessScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_success_screen, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
          Adds callback to Home Button to navigate the User to the Main Screen.
         */
        ImageButton toHomeButton = (ImageButton) view.findViewById(R.id.homeButtonSuccess);
        toHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uses popUpTo global action to remove Fragment instances from backstack
                Navigation.findNavController(view)
                        .navigate(MainNavGraphDirections.actionPopUpToMainScreenFragment());
            }
        });

        /*
        * Adds callback that listens to the results from the Game Fragment in order to display game details.
        */
        getParentFragmentManager()
                .setFragmentResultListener("gameData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String elapsedTime = result.getString("elapsedTime");
                String score = result.getString("score");
                String stepSize = result.getString("stepSize");

                TextView scoreView = (TextView) view.findViewById(R.id.scoreText);
                scoreView.setText(score);

                TextView scoreDetailView = (TextView) view.findViewById(R.id.scoreDetail);
                String scoreDetails = getString(R.string.score_details_start) + stepSize
                        + getString(R.string.score_details_middle) + elapsedTime
                        + getString(R.string.score_details_end);
                scoreDetailView.setText(scoreDetails);
            }
        });
    }
}
