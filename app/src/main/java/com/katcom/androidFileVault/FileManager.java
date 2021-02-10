package com.katcom.androidFileVault;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
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

public class FileManager{
    public static String TAG ="FileVault"; // For debug
    public static String sVaultDirectory = "FileVaultOne";  // The directory of the vault in the private storage of this app
    private static FileManager sVault;    // This class is a singleton, only one instance allowed
    private List<ProtectedFile> mFiles;     // Entries of all files
    private Context mContext;           // Android context, used to get to the private storage

    private FileManager(Context context){
        mContext= context;

        mFiles = new ArrayList<ProtectedFile>();

        update();
    }
    public static FileManager get(Context context){
        if(sVault == null) {
            sVault = new FileManager(context);
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

    public void update(){
        File directory = mContext.getFilesDir();
        File vaultFolder = new File(directory, sVaultDirectory);
        mFiles = new ArrayList<ProtectedFile>();
        String[] files = vaultFolder.list();

        for(String filename: files){
            String path = mContext.getFilesDir() + "/" + sVaultDirectory +"/" + filename;
            ProtectedFile file = new ProtectedFile(filename,path,UUID.randomUUID());
            mFiles.add(file);
        }
    }
    public void addFileEntry(String filename, String filepath){
        ProtectedFile file = new ProtectedFile(filename,filepath,UUID.randomUUID());
        mFiles.add(file);
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
    public void importFile(String filename,FileDescriptor in, String targetPath) throws FileNotFoundException {
        importAndEncrypt(in,targetPath);
        addFileEntry(filename,targetPath);
    }
    public void exportFile(ProtectedFile file, FileDescriptor fd) throws FileNotFoundException {
        //File outFile = new File(targetPath);
        OutputStream out=null;
        InputStream in =null;
        out = new FileOutputStream(fd);
        in = new FileInputStream(file.getFilepath());
        writeFile(in,out);

    }

    public CipherOutputStream getEncryptedFileOutStream(String targetPath, String key, byte[] iv) {
        return null;
    }

    private OutputStream getFileOutputStream(String targetPath) throws FileNotFoundException {
        return new FileOutputStream(targetPath);

    };

    public void importAndEncrypt(FileDescriptor inFile, String targetPath) throws FileNotFoundException {
        OutputStream out=null;
        InputStream in =null;
        in = new FileInputStream(inFile);
        out = getFileOutputStream(targetPath);

        writeFile(in,out);
    }

    public CipherInputStream getDecryptedInputStream(String filepath) {
        CipherInputStream out = null;

        return out;
    }


    private void writeFile(InputStream in, OutputStream out){
        try {

            byte[] buffer = new byte[1024];
            int i;

            int tot = 0;
            while ((i = in.read(buffer)) != -1) {
                tot += i;
                out.write(buffer, 0, i);
            }

            out.flush();
        } catch (FileNotFoundException e) {
            Log.e(TAG,e.toString());
        } catch (IOException e) {
            Log.e(TAG,e.toString());
        } finally {
            try{
                if(out != null) out.close();
                if(in != null) in.close();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
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
