package com.katcom.androidFileVault.SettingSection;

import androidx.fragment.app.Fragment;

import com.katcom.androidFileVault.SingleFragmentActivity;

public class SettingActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return (Fragment) new SettingFragment();
    }
}
