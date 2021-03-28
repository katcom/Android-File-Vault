package com.katcom.androidFileVault;

import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.katcom.androidFileVault.CloseReceiver.CLOSE_INTENT;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();
    protected CloseReceiver closeReceiver;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }

        // Register a broadcast for closing the app
        // when user clicks the exit button, the exit broadcast is triggered and sent to all activities
        // Upon which the activity receiving the exit broadcast would close itself

        closeReceiver = new CloseReceiver(this);
        registerReceiver(closeReceiver,new IntentFilter(CLOSE_INTENT));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(closeReceiver);
    }
}
