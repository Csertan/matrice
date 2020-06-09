package com.nosebite.matrice;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;


/**
 * A simple {@link Fragment} subclass that displays the Main Screen for the User.
 */
public class MainScreenFragment extends Fragment {

    private MainActivity mainActivity;

    private SignInButton signInButton;
    private Button signOutButton;

    public MainScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateBackgroundNightMode();

        /* Getting the reference of the Main Activity */
        mainActivity = (MainActivity) getActivity();

        /* On Click Listener for the navigation to Authors Screen */
        Button toAuthorsButton = (Button) view.findViewById(R.id.authorsButtonMain);
        toAuthorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(MainScreenFragmentDirections.actionMainScreenFragmentToAuthorsScreenFragment());
            }
        });

        /* On Click Listener for the navigation to Game Screen */
        Button playButton = (Button) view.findViewById(R.id.playButtonMain);
        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(MainScreenFragmentDirections.actionMainScreenFragmentToGameScreenFragment());
            }
        });

        /* On Click listener for the navigation to Levels Screen */
        Button levelsButton = (Button) view.findViewById(R.id.campaignButtonMain);
        levelsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Navigate to LevelsScreen
                Toast.makeText(getContext(), getString(R.string.message_feature_coming_soon), Toast.LENGTH_SHORT).show();
            }
        });

        /* On Click Listener for starting Settings Activity to adjust Preferences */
        Button settingsButton = (Button) view.findViewById(R.id.settingsButtonMain);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Since it's another Activity we have to use an Intent
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        /* On CLick Listener for showing Achievements to the player */
        ImageButton showAchievementsButton = (ImageButton) view.findViewById(R.id.achievementsButtonMain);
        showAchievementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAchievements();
            }
        });

        /* On CLick Listener for showing Leaderboards to the player */
        ImageButton showLeaderboardsButton = (ImageButton) view.findViewById(R.id.leaderBoardsButtonMain);
        showLeaderboardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLeaderboards();
            }
        });

        /* On CLick Listener for signing the player in */
        signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.startSignInIntent();
            }
        });

        /* On CLick Listener for signing the player out */
        signOutButton = (Button) view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.signOut();
                Navigation.findNavController(view)
                        .navigate(MainScreenFragmentDirections.actionMainScreenFragmentToBootScreenFragment());
            }
        });

        /* Updates buttons */
        updateUI(mainActivity.playerIsSignedIn());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(mainActivity.playerIsSignedIn());
    }

    /**
     * Updates Sign in and out Buttons upon changes.
     * @param isSignedIn Determines whether the player is signed in.
     */
    private void updateUI(boolean isSignedIn) {
        if(isSignedIn) {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
        }
        else {
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
    }

    private void updateBackgroundNightMode() {
        int nightModeFlags = getContext().getResources().getConfiguration()
                .uiMode & Configuration.UI_MODE_NIGHT_MASK;
        ConstraintLayout layout = (ConstraintLayout) getView().findViewById(R.id.mainScreenFragmentLayout);
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                layout.setBackgroundResource(R.drawable.ic_game_surface2);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                layout.setBackgroundResource(R.drawable.ic_game_surface3);
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                layout.setBackgroundResource(R.drawable.ic_game_surface3);
        }
    }


    /**
     * Shows the Achievements List to the player.
     */
    private void showAchievements() {
        mainActivity.getAchievementsClient().getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, MainActivity.RC_ACHIEVEMENTS_UI);
                    }
                });
    }

    /**
     * Shows the Leaderboards List to the player.
     */
    private void showLeaderboards() {
        mainActivity.getLeaderboardsClient().getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, MainActivity.RC_LEADERBOARD_UI);
                    }
                });
    }
}
