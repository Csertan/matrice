package com.nosebite.matrice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.common.SignInButton;


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

        mainActivity = (MainActivity) getActivity();

        /*
          On Click Listener for the navigation to Authors Screen
         */
        Button toAuthorsButton = (Button) view.findViewById(R.id.authorsButtonMain);
        toAuthorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(MainScreenFragmentDirections.actionMainScreenFragmentToAuthorsScreenFragment());
            }
        });

        /*
          On Click Listener for the navigation to Game Screen
         */
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
                Toast.makeText(getContext(), getString(R.string.feature_coming_soon), Toast.LENGTH_SHORT).show();
            }
        });

        /*
          On Click Listener for starting Settings Activity to adjust Preferences
         */
        Button settingsButton = (Button) view.findViewById(R.id.settingsButtonMain);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Since it's another Activity we have to use an Intent
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.startSignInIntent();
            }
        });

        signOutButton = (Button) view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.signOut();
                Navigation.findNavController(view)
                        .navigate(MainScreenFragmentDirections.actionMainScreenFragmentToBootScreenFragment());
            }
        });

        updateUI(mainActivity.playerIsSignedIn());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(mainActivity.playerIsSignedIn());
    }

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
}
