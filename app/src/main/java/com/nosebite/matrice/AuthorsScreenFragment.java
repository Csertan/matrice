package com.nosebite.matrice;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass that displays the Authors and Copyright information.
 */
public class AuthorsScreenFragment extends Fragment {

    public AuthorsScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authors_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateBackgroundNightMode();
    }

    private void updateBackgroundNightMode() {
        int nightModeFlags = getContext().getResources().getConfiguration()
                .uiMode & Configuration.UI_MODE_NIGHT_MASK;
        ConstraintLayout layout = (ConstraintLayout) getView().findViewById(R.id.authorsScreenFragmentLayout);
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                layout.setBackgroundResource(R.drawable.ic_game_surface2);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                layout.setBackgroundResource(R.drawable.ic_game_surface3);
                break;
        }
    }
}
