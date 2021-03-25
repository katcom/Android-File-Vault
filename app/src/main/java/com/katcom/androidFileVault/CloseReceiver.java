package com.katcom.androidFileVault;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Close the app by killing all activities using broadcast
 * Ref:https://blog.csdn.net/totond/article/details/72960211
 */
public class CloseReceiver extends BroadcastReceiver {
    public static String CLOSE_INTENT = "com.katcom.androidFileVault.Exit";

    private Activity activity;

    // Default constructor
    public CloseReceiver(){};

    // Constructor using activity
    public CloseReceiver(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        activity.finish();
    }
}
