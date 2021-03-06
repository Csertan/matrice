package com.nosebite.matrice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass that displays the NavBar to the User.
 * It is included in other Fragments.
 */
public class NavBarFragment extends Fragment {

    private String parentFragmentName;

    public NavBarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav_bar, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getParentFragment() != null;
        parentFragmentName = getParentFragment().getClass().getSimpleName();

        /* Adds callback to the Home Button to navigate the user to the Main Screen. */
        ImageButton toHomeButton = (ImageButton) view.findViewById(R.id.navBarHomeButton);
        toHomeButton.setOnClickListener(view13 -> {
            //Uses popUpTo global action to remove Fragment instances from backstack
            Navigation.findNavController(view13)
                    .navigate(MainNavGraphDirections.actionPopUpToMainScreenFragment());
        });

        /* Displays different destinations upon current parent fragment */
        ImageButton levelsButton = (ImageButton) view.findViewById(R.id.navBarLevelsButton);
        if(parentFragmentName.equals("LevelsFragment"))
        {
            levelsButton.setImageResource(R.drawable.ic_play_icon);
        }
        else {
            levelsButton.setImageResource(R.drawable.ic_levels_icon);
        }
        levelsButton.setOnClickListener(view12 -> {
            if(parentFragmentName.equals("LevelsFragment")) {
                //TODO Navigate to GameScreen
            }
            else {
                //TODO Navigate to LevelsScreen
            }
            Toast.makeText(getContext(), getString(R.string.message_feature_coming_soon), Toast.LENGTH_SHORT).show();
        });

        /* On Click Listener for starting Settings Activity to adjust Preferences */
        ImageButton settingsButton = (ImageButton) view.findViewById(R.id.navBarSettingsButton);
        settingsButton.setOnClickListener(view1 -> {
            //Since it's another Activity we have to use an Intent
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        /* Updates displayed fragment title with parent fragments title */
        TextView fragmentTitle = (TextView) view.findViewById(R.id.navBarTitle);
        if(parentFragmentName.equals("LevelsFragment"))
        {
            fragmentTitle.setText(R.string.title_levels_fragment);
        }
        else {
            fragmentTitle.setText(R.string.text_menu_authors);
        }
    }
}
