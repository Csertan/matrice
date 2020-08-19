package com.nosebite.matrice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass that is displayed at the right start of the Application.
 */
public class BootScreenFragment extends Fragment {

    private MainActivity mainActivity;
    private ImageButton toMainButton;
    private TextView continueText;

    public BootScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_boot_screen, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* Gets the reference of the Main Activity */
        mainActivity = (MainActivity) getActivity();

        /* Adds callback to the launcher Button to navigate the user to the Main Screen */
        toMainButton = (ImageButton) view.findViewById(R.id.goToMainButton);
        continueText = (TextView) view.findViewById(R.id.continueText);
        toMainButton.setOnClickListener(view1 -> {
            if (!mainActivity.playerIsSignedIn()) {
                mainActivity.startSignInIntent();
            } else {
                Navigation.findNavController(view1)
                        .navigate(BootScreenFragmentDirections.actionBootScreenFragmentToMainScreenFragment());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        toMainButton.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate));
        continueText.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in));
    }
}
