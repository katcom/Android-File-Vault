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
    /*public CipherOutputStream getDecryptedInputStream(String filepath){

    }*/

    /**
     * This method returns an IntputStream pointed to an encrypted file which decrypts file on the fly.
     * In other words, it is connected to an encrypted file, when you read data via the CipherOutputStream,
     * you get the decrypted data instead of the encrypted.
     *
     * @param filepath
     * @return
     */
    CipherOutputStream getDecryptedInputStream(String filepath);


}
