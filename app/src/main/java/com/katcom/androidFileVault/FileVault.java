package com.katcom.androidFileVault;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

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

    /**
     *
     * @param filepath
     */
    public void importFileToRootDirectory(String filepath, String filename){
        importFile(filepath, mContext.getFilesDir()+"/" + sVaultDirectory +"/" + filename, filename);
    }

    public void importFile(String filepath, String targetPath,String filename){
       Utils.copyFile(filepath,targetPath);
       addFileEntry(filename,targetPath);
    }

    private void addFileEntry(String filename, String targetPath) {
    }

    public void exportFile(String filename, String sourcePath,String targetPath){
        // TODO
    }

    public void importAndEncryptFileToRootDirectory(String filename,InputStream in){

    }

    public void importAndEncryptFile(InputStream in,String targetPath){
        CipherInputStream cin = getEncodedInputStream(in);
        copyEncryptedFile(cin,targetPath);
    }

    private CipherInputStream getEncodedInputStream(InputStream in) {
        // TODO
        return  (CipherInputStream)in;
    }

    private void copyEncryptedFile(CipherInputStream cin, String targetPath){
        try{
            byte[] buffer = new byte[1024];
            File outFile = new File(targetPath);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));

            int i;
            while ((i = cin.read(buffer)) != -1) {
                out.write(buffer, 0, i);
            }
            out.close();
            cin.close();

            out = null;
            cin = null;
        } catch (FileNotFoundException e) {
            Log.e(TAG,"Cannot find file ",e);
        } catch (IOException e) {
            Log.e(TAG,"IO Error",e);
        }

    }
}
