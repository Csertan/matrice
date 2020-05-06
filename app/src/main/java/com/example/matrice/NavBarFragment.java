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
 * A simple {@link Fragment} subclass.
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
        // Setup any handles to view objects here
        ImageButton toMainButton = (ImageButton) view.findViewById(R.id.navBarHomeButton);
        toMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(NavBarFragmentDirections.actionGlobalMainScreenFragment());

                /*NavHostFragment.findNavController(NavBarFragment.this)
                        .navigate(R.id.action_global_mainScreenFragment);*/
            }
        });
    }
}
