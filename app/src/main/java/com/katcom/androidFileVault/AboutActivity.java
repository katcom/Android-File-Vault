package com.katcom.androidFileVault;

import androidx.fragment.app.Fragment;

public class AboutActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AboutFragment();
    }
}
