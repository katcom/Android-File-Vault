package com.katcom.androidFileVault;

import android.view.Menu;
import android.view.MenuInflater;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

public class VaultActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new VaultFragment();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return true;
    }

}
