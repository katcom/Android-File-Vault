package com.katcom.androidFileVault.login;

import androidx.fragment.app.Fragment;

import com.katcom.androidFileVault.SingleFragmentActivity;

public class SetPasswordActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SetPasswordFragment();
    }
}
