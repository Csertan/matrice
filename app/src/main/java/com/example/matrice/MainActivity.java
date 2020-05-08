package com.example.matrice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main Activity of the Application.
 * Contains most of the Fragments via a NavHostFragment.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
