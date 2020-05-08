package com.example.matrice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass that displays the NavBar to the User.
 * It is included in other Fragments.
 */
public class NavBarFragment extends Fragment {

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

        /**
         * Adds callback to the Home Button to navigate the user to the Main Screen.
         */
        ImageButton toHomeButton = (ImageButton) view.findViewById(R.id.navBarHomeButton);
        toHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Uses popUpTo global action to remove Fragment instances from backstack
                Navigation.findNavController(view)
                        .navigate(MainNavGraphDirections.actionPopUpToMainScreenFragment());
            }
        });
    }
}
