package com.nosebite.matrice;

import android.app.Application;
import android.content.Context;

public class Matrice extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
