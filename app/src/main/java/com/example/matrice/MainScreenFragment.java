package com.example.matrice;

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
        // Setup any handles to view objects here
        Button toAuthorsButton = (Button) view.findViewById(R.id.authorsButtonMain);
        toAuthorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(MainScreenFragmentDirections.actionMainScreenFragmentToAuthorsScreenFragment());

                /*NavHostFragment.findNavController(MainScreenFragment.this)
                        .navigate(R.id.action_mainScreenFragment_to_authorsScreenFragment);*/
            }
        });
    }
}
