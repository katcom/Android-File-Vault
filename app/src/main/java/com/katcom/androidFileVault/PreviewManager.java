package com.katcom.androidFileVault;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

public class PreviewManager {
    private Context mContext;
    private final static String TAG = "Preview Manager";
    public PreviewManager(Context context) {
        mContext =context;
    }

    /**
     * This method returns a scaled thumbnail of a picture in form of a Bitmap,
     * given the size of the container of the thumbnail picture.
     * We need to make it run on the back ground for fast loading speed
     * @param file
     * @param sizeX
     * @param sizeY
     * @return
     */
    public Bitmap getPreview(ProtectedFile file,int sizeX,int sizeY){
            Bitmap bitmap;
            try {
                FileManager vault = FileManager.get(mContext);

                InputStream in = vault.getDecryptedInputStream(file.getFilepath());
                //ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in,null,options);

                float srcWidth = options.outWidth;
                float srcHeight = options.outHeight;
                String message = "Width : " + srcWidth + " Height : " + srcHeight;
                Log.i(TAG,message);

                int inSampleSize = Utils.calculateInSampleSize(options,sizeX,sizeY);

                options = new BitmapFactory.Options();
                options.inSampleSize = inSampleSize;
                in = vault.getDecryptedInputStream(file);
                //in.close();
                Bitmap preview = BitmapFactory.decodeStream(in,null,options);

                // If loading with options succeed, return the image
                if(preview != null) return preview;

                // If failed to load the image with options, drop the options and load it again
                in = vault.getDecryptedInputStream(file);
                return BitmapFactory.decodeStream(in);

            } catch (UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
                Log.e(TAG,e.toString());
            }

            bitmap = getDefaultPreview();
            return bitmap;
        }

    private Bitmap getDefaultPreview() {
        return BitmapFactory.decodeResource(mContext.getResources(),R.drawable.default_file_preview);
    }


}
