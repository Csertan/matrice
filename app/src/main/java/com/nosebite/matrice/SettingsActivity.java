package com.nosebite.matrice;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import org.jetbrains.annotations.NotNull;

/**
 * Settings Activity that helps the User adjust his/her Preferences.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            //Summary provider for Figure Set Preferences
            ListPreference figureSetPreferences = findPreference(getString(R.string.key_figure_set));
            if(figureSetPreferences != null) {
                figureSetPreferences.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            }

            //Summary provider for Transition Type Preferences
            ListPreference transitionTypePreferences = findPreference(getString(R.string.key_transition_type));
            if(transitionTypePreferences != null) {
                transitionTypePreferences.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            }

            //Summary provider for Language Preferences
            ListPreference languagePreferences = findPreference(getString(R.string.key_app_language));
            if(languagePreferences != null) {
                languagePreferences.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

            }

            //Summary provider for Gender Preferences
            ListPreference genderPreferences = findPreference(getString(R.string.key_gender_info));
            if(genderPreferences != null) {
                genderPreferences.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            }

            //Switches between Day and Night UI modes
            SwitchPreference darkModePreference = findPreference(getString(R.string.key_enable_dark_mode));
            assert darkModePreference != null;
            darkModePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(darkModePreference.isChecked()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                }
            });

            //On Click Listener for Send Feedback Preference
            Preference sendFeedbackPreference = findPreference(getString(R.string.key_send_feedback));
            assert sendFeedbackPreference != null;
            sendFeedbackPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(requireActivity());
                    return true;
                }
            });

            Preference privacyPolicy = findPreference(getString(R.string.key_privacy_policy));
            if(privacyPolicy != null) {
                privacyPolicy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        openWebPage(getString(R.string.url_privacy));
                        return true;
                    }
                });
            }
        }

        void openWebPage(String url) {
            Uri webPage = Uri.parse(url);
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(webPage);
            if(webIntent.resolveActivity(requireActivity().getPackageManager()) != null)
            {
                startActivity(webIntent);
            }
            else {
                Toast.makeText(getContext(), getString(R.string.view_webpage_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Sends Feedback with device specifications to the developer.
     * @param context Actual context from the function is called.
     */
    public static void sendFeedback(@NotNull Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n--------------------\nPlease don't remove the " +
                    "information below!\n--------------------\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"csertant@edu.bme.hu"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from android app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}