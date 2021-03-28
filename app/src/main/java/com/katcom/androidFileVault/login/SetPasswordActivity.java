package com.katcom.androidFileVault.login;

import androidx.fragment.app.Fragment;

import com.katcom.androidFileVault.SingleFragmentActivity;

/**
 * This activity contains the fragment for setting password
 */
public class SetPasswordActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SetPasswordFragment();
    }
}
