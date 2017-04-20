package com.example.robertjackson.assignment4;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.util.List;

/**
 * Robert Jackson
 * 4/8/2017
 */
public class PrefsActivity extends PreferenceActivity {

    /**
     * On create method, this is the first method run when starting this activity.
     * all this does is, if the build version is within a certain range, it adds preferences from
     * the xml folder. Specifically the pref xml file.
     * @param savedInstanceState
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.prefs);
        }
    }

    /**
     * On Build headers, this loads headers from the xml pre_headers file, using a pre existing
     * header list.
     * @param target
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * Sync prefs inner static class, is for the xml prefs file, it adds the preferences from
     * our folder.
     */
    public static class SyncPrefs extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }
    }
}
