package com.katcom.androidFileVault;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    private static String TAG = "AndroidFileVault";
    public static void copyFilesFromAsset(Context context,String source,String target){
        AssetManager assets = context.getAssets();
        String[] files = null;
        target = context.getFilesDir()+"/"+target;

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
                    String targetPath = target + "/" + filename;

                    in = assets.open(assetPath);
                    File file = new File(targetPath);

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

    /**
     * Load scaled picture from file
     * @param context
     * @param filepath the location of  file
     * @param sizeX  the desired width, measured in dp
     * @param sizeY  the desired height, measured in dp
     * @return
     */
    public static Bitmap getScaledBitmap(Context context,String filepath,int sizeX,int sizeY){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath,options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        float targetWidth = Utils.dip2px(context,sizeX);
        float targetHeight = Utils.dip2px(context,sizeY);

        // Scale the picture
        int inSampleSize = 1;
        if(srcHeight > targetHeight || srcHeight > targetHeight){
            if(srcWidth > targetWidth){
                inSampleSize = Math.round(srcWidth/targetWidth);
            }else{
                inSampleSize = Math.round(srcHeight/targetHeight);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(filepath,options);
    }


    /**
     *  Calculate px (pixel) from dp
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Calculate dp from px (pixel)
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void copyFile(InputStream in, String target){
        BufferedOutputStream out = null;
        File outFile = new File(target);
        try{
            out = new BufferedOutputStream(new FileOutputStream(outFile));

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

        } catch (FileNotFoundException e) {
            Log.e(TAG,"Failed to copy file from asset",e);
        } catch (IOException e) {
            Log.e(TAG,"Error",e);
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

    public static void copyFile(String source,String target){
        File inFile = new File(source);
        File outFile = new File(target);
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try{
            in = new BufferedInputStream(new FileInputStream(inFile));
            out = new BufferedOutputStream(new FileOutputStream(outFile));

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

        } catch (FileNotFoundException e) {
            Log.e(TAG,"Failed to copy file from asset",e);
        } catch (IOException e) {
            Log.e(TAG,"Error",e);
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
