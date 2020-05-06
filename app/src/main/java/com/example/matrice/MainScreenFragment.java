package com.example.matrice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainScreenFragment extends Fragment {

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

        //On Click Listener for the navigation to Authors Screen
        Button toAuthorsButton = (Button) view.findViewById(R.id.authorsButtonMain);
        toAuthorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(MainScreenFragmentDirections.actionMainScreenFragmentToAuthorsScreenFragment());
            }
        });

        //On Click Listener for the navigation to Game Screen
        Button playButton = (Button) view.findViewById(R.id.playButtonMain);
        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(MainScreenFragmentDirections.actionMainScreenFragmentToGameScreenFragment());
            }
        });

        //On Click Listener for starting Settings Activity to adjust Preferences
        Button settingsButton = (Button) view.findViewById(R.id.settingsButtonMain);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
