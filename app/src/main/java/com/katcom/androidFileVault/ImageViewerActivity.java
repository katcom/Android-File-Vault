package com.katcom.androidFileVault;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

public class ImageViewerActivity extends SingleFragmentActivity {
    public static final String EXTRA_FILE = "com.katcom.adnroidFileVault.imageviewerIntent.file";

    @Override
    protected Fragment createFragment() {
        ProtectedFile file= (ProtectedFile) getIntent().getParcelableExtra(EXTRA_FILE);

        return ImageViewerFragment.newInstance(file);
    }

    public static Intent newIntent(Context packageContext, ProtectedFile file){
        Intent intent = new Intent(packageContext, ImageViewerActivity.class);
        intent.putExtra(EXTRA_FILE,file);
        return intent;
    }
}
