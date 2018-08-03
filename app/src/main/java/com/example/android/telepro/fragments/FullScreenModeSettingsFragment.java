package com.example.android.telepro.fragments;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;


import com.example.android.telepro.R;


public class FullScreenModeSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main_telepro_preference);
    }
}
