package com.katcom.androidFileVault;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileVault {
    public static String TAG ="FileVault"; // For debug
    public static String sVaultDirectory = "FileVaultOne";  // The directory of the vault in the private storage of this app
    private static FileVault sVault;    // This class is a singleton, only one instance allowed
    private List<ProtectedFile> mFiles;     // Entries of all files
    private Context mContext;           // Android context, used to get to the private storage

    private FileVault(Context context){
        mContext= context;
        File directory = context.getFilesDir();
        File vaultFolder = new File(directory, sVaultDirectory);

        String[] files = vaultFolder.list();

        mFiles = new ArrayList<ProtectedFile>();

        for(String filename: files){
            String path = mContext.getFilesDir() + "/" + sVaultDirectory +"/" + filename;
            ProtectedFile file = new ProtectedFile(filename,path,UUID.randomUUID());
            mFiles.add(file);
        }
    }
    public static FileVault get(Context context){
        if(sVault == null) {
            sVault = new FileVault(context);
        }
        return sVault;
    }

    public List<ProtectedFile> getFiles(){
        return mFiles;
    }

    public ProtectedFile getFile(UUID id){
        for(ProtectedFile file : mFiles){
            if(file.getId().equals(id)){
                return file;
            }
        }
        return null;
    }

    public String getVaultDirectory(){
        return sVaultDirectory;
    }

    /**
     *  Get the preview picture of the given file based on its type
     * @param file
     * @param sizeX
     * @param sizeY
     * @return
     */
    public Bitmap getPreview(ProtectedFile file,int sizeX,int sizeY){
        // Currently all the sample files are pictures, we just need to return the preview of pictures
           return getPicturePreview(file,sizeX,sizeY);
    }

    private Bitmap getPicturePreview(ProtectedFile file,int sizeX,int sizeY) {
        return Utils.getScaledBitmap(mContext,file.getFilepath(),sizeX,sizeY);
    }

    public void importFile(String filepath){
        // TODO
    }
    public void exportFile(String filename, String sourcePath,String targetPath){
        // TODO
    }

}
