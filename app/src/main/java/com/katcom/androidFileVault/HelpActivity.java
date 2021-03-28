package com.katcom.androidFileVault;

import androidx.fragment.app.Fragment;

public class HelpActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new HelpFragment();
    }
}
