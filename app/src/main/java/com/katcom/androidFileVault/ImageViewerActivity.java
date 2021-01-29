package com.katcom.androidFileVault;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

public class ImageViewerActivity extends SingleFragmentActivity {
    public static final String EXTRA_FILE_NAME= "com.katcom.adnroidFileVault.imageviewerIntent.filename";
    public static final String EXTRA_FILE_PATH = "com.katcom.adnroidFileVault.imageviewerIntent.filepath";

    @Override
    protected Fragment createFragment() {
        String filename = (String)getIntent().getSerializableExtra(EXTRA_FILE_NAME);
        String filepath = (String)getIntent().getSerializableExtra(EXTRA_FILE_PATH);

        return ImageViewerFragment.newInstance(filename,filepath);
    }

    public static Intent newIntent(Context packageContext, String filename,String filepath){
        Intent intent = new Intent(packageContext, ImageViewerActivity.class);
        intent.putExtra(EXTRA_FILE_NAME,filename);
        intent.putExtra(EXTRA_FILE_PATH,filepath);
        return intent;
    }
}
