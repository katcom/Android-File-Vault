package com.katcom.androidFileVault;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

public class EntryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new EntryFragment();
    }


}

