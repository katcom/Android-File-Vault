package com.katcom.androidFileVault.SettingSection;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.katcom.androidFileVault.R;


public class SettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_settings);
    }
}
