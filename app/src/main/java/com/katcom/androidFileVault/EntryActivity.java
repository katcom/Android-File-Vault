package com.katcom.androidFileVault;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EntryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new EntryFragment();
    }


}

