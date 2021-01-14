package com.katcom.androidFileVault;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    private static String TAG = "AndroidFileVault";
    public static void copyFilesFromAsset(Context context,String source,String target){
        AssetManager assets = context.getAssets();
        String[] files = null;
        target = "/" + target + "/";

        try{
            files = assets.list(source);
        }catch (IOException ioe){
            Log.e(TAG,"Could not list assets",ioe);
        }
        if(files != null){
            InputStream in = null;
            OutputStream out  = null;

            for(String filename :files){
                try{
                    String assetPath = source + "/"+ filename;
                    String targetPath = "/" + target + "/" + filename;

                    in = assets.open(assetPath);
                    File file = new File(context.getFilesDir()+targetPath);

                    if(file.exists()){
                       continue;
                    }

                    out = new BufferedOutputStream(new FileOutputStream(file));

                    Log.v("Assets:", assetPath);

                    byte[] buffer = new byte[1024];
                    int read;
                    while((read = in.read(buffer)) != -1){
                        out.write(buffer, 0, read);
                    }

                    out.flush();
                    out.close();
                    in.close();

                    out = null;
                    in = null;

                }catch(IOException ioe){
                    Log.e(TAG,"Failed to copy file from asset",ioe);
                }
                finally {
                    if(in != null){
                        try{
                            in.close();
                        }catch (IOException ioe){
                            Log.e(TAG,"Error occurs when closing input stream",ioe);
                        }
                    }

                    if(out != null){
                        try{
                            out.close();
                        }catch (IOException ioe){
                            Log.e(TAG,"Error occurs when closing output stream",ioe);
                        }
                    }
                }
            }
        }
    }
}
