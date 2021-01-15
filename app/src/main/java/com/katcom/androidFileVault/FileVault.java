package com.katcom.androidFileVault;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileVault {
    public static String sVaultDirectory = "FileVaultOne";
    private static FileVault sVault;
    private List<ProtectedFile> mFiles;
    private FileVault(Context context){
        // Singleton
        File directory = context.getFilesDir();
        File vaultFolder = new File(directory, sVaultDirectory);

        String[] files = vaultFolder.list();

        mFiles = new ArrayList<ProtectedFile>();

        for(String filename: files){
            ProtectedFile file = new ProtectedFile(filename,null,UUID.randomUUID());
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
}
