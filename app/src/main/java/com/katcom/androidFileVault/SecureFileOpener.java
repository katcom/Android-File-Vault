package com.katcom.androidFileVault;

import android.content.Context;
import android.content.Intent;

public class SecureFileOpener implements FileOpener  {
    private Context mContext;
    private static SecureFileOpener mFileOpener;
    private  SecureFileOpener(Context context){
        mContext =context;
    }
    public static SecureFileOpener getInstance(Context context){
        if(mFileOpener == null){
            mFileOpener = new SecureFileOpener(context);
        }
        return mFileOpener;
    }
    @Override
    public void openFile(ProtectedFile file) {

    }

    @Override
    public void openPdf(ProtectedFile file) {

    }

    @Override
    public void openDocx(ProtectedFile file) {

    }

    @Override
    public void openTxt(ProtectedFile file) {

    }

    @Override
    public void openVideo(ProtectedFile file) {

    }

    @Override
    public void openAudio(ProtectedFile file) {

    }

    @Override
    public void openPicture(ProtectedFile file) {
        Intent intent = ImageViewerActivity.newIntent(mContext, file.getFilename(), file.getFilepath());
        mContext.startActivity(intent);
    }

    public void openBasedOnFileType(ProtectedFile file){
        openPicture(file);
    }
}
