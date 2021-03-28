package com.katcom.androidFileVault.login;

import androidx.fragment.app.Fragment;

import com.katcom.androidFileVault.SingleFragmentActivity;

/**
 * This activity contains the fragment for login.
 */
public class LoginActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

}
