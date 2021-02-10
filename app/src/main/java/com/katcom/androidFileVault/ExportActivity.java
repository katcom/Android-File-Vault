package com.katcom.androidFileVault;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class ExportActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ExportFragment();
    }

}