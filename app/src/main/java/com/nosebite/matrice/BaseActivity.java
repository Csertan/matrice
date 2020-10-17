package com.nosebite.matrice;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocale(getBaseContext(), LocaleHelper.getLanguage(getBaseContext()));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // add this to fix androidx.appcompat:appcompat 1.1.0 bug
            // which happens on Android 6.x ~ 7.x
            getResources();
        }

        super.applyOverrideConfiguration(overrideConfiguration);
    }
}
