package com.katcom.androidFileVault;

import android.net.Uri;

import javax.crypto.CipherOutputStream;

public interface Crypto {

    CipherOutputStream getEncryptedFileOutStream(String targetPath, String key, byte[] iv);

    /**
     * This method gets the data in the file and encrypt it
     */
    void importAndEncrypt(Uri uri);
    /*{
        // get the selected file and encrypt it
        InputStream in = getInputStreamFromUri(uri);
        String filename = getFilenameFromUri(uri);
        String targetPath = "/Vault/" + filename;

        // Above we get the data in the file,
        // Encrypt the data here
        byte[] iv = filename.getBytes();
        CipherOutputStream out = getEncryptedFileOutStream(targetPath, key,iv);

        // write encrypted data
        writeData(in,out);

    }*/

}
