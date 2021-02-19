/**
 * The FileManager handles import and export, and the encryption and decryption of the files in the vault.
 * It holds the alias to the private key.
 * It supports import / export with encryption and decryption on the fly.
 * The FileManager is a Singleton which allows only one instance to be created.
 *
 */
package com.katcom.androidFileVault;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.katcom.androidFileVault.CustomedCrypto.CustomCipherInputStream;
import com.katcom.androidFileVault.SecureSharePreference.SecureSharePreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class FileManager{
    public static String TAG ="FileVault"; // For debug
    public static String sVaultDirectory = "FileVaultOne";  // The directory of the vault in the private storage of this app
    private static FileManager sVault;    // This class is a singleton, only one instance allowed
    private List<ProtectedFile> mFiles;     // Entries of all files
    private final Context mContext;           // Android context, used to get to the private storage
    private final static String keyAlias = "FileVault";    //Reference to the private key
    private KeyStore keystore;
    private static final String SHARE_PREFERENCE_TAG = "fileVault";
    private FileManager(Context context){
        mContext= context;

        mFiles = new ArrayList<ProtectedFile>();

        setupKeyStore();

        update();   // create file entries based on the files in the vault foldere
    }

    /**
     * Get an instance of the FileManager.
     * If there is an instance, return it.
     * Otherwise, create a new instance.
     * @param context
     * @return
     */
    public static FileManager get(Context context){
        if(sVault == null) {
            sVault = new FileManager(context);
        }
        return sVault;
    }

    public List<ProtectedFile> getFiles(){
        return mFiles;
    }

    /**
     * Given an id, this method returns the protected file with this id.
     * @param id
     * @return ProtectedFile with given id
     */
    public ProtectedFile getFile(UUID id){
        for(ProtectedFile file : mFiles){
            if(file.getId().equals(id)){
                return file;
            }
        }
        return null;
    }

    /**
     * Update the entries of files by scanning all files within the vault folder
     */
    public void update(){
        File directory = mContext.getFilesDir();
        File vaultFolder = new File(directory, sVaultDirectory);

        if(!vaultFolder.exists()){
            vaultFolder.mkdirs();
        }

        mFiles = new ArrayList<ProtectedFile>();
        String[] files = vaultFolder.list();

        for(String filename: files){
            String path = mContext.getFilesDir() + "/" + sVaultDirectory +"/" + filename;
            ProtectedFile file = new ProtectedFile(filename,path,UUID.randomUUID());

            int size = mContext.getResources().getDimensionPixelSize(R.dimen.small_preview_image_size);
            //file.setPreview(getPreview(file,size,size));
            mFiles.add(file);
        }
    }

    //****************** Start Import Functions ************************************//
    /** @deprecated
     * This method copy file directly from its original path to the vault.
     * @param filepath
     * @param targetPath
     * @param filename
     */
    public void importFile(String filepath, String targetPath,String filename){
        Utils.copyFile(filepath,targetPath);
        addFileEntry(filename,targetPath);
    }

    /**
     * This method read file from an InputStream to the vault, and encrypt the file when copying,
     * Also it creates an entry for this file.
     *
     * @param file
     * @param in
     * @throws FileNotFoundException
     */
    public void importFile(File file, InputStream in) throws FileNotFoundException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        importAndEncrypt(in,file.getPath());
        addFileEntry(file.getName(),file.getPath());
    }

    /**
     * This method copies file from an InputStream, and encrypt it during the import,
     * finally it writes the encrypted file to the target path(usually the folders within the vault)
     *
     * @param in
     * @param targetPath
     * @throws FileNotFoundException
     */
    private void importAndEncrypt(InputStream in, String targetPath) throws FileNotFoundException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        OutputStream out=null;
        out = getEncryptedOutputStream(targetPath);

        writeFile(in,out);
    }

    /**
     * This method import a file from the asset, with encryption on the fly.
     * @param filename
     * @param filepath
     * @param targetPath
     * @throws IOException
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public void importFileFromAsset(String filename,String filepath, String targetPath) throws IOException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        AssetManager asset = mContext.getAssets();

        OutputStream out=null;
        InputStream in =null;

        in = asset.open(filepath);
        out = getEncryptedOutputStream(targetPath);

        writeFile(in,out);
        addFileEntry(filename,targetPath);
    }

    /**
     * Add an file entry given its file name and path
     * @param filename the name of file
     * @param filepath the path of the file in the vault
     */
    public void addFileEntry(String filename, String filepath){
        ProtectedFile file = new ProtectedFile(filename,filepath,UUID.randomUUID());
        mFiles.add(file);
    }
    //********************** End Import Functions ******************************//


    //********************** Start Export Functions **********************************//

    /**
     * Given an output stream, this method writes the particular file to the output stream,
     * and decrypt the file on the fly.
     * @param file
     * @param out
     * @throws FileNotFoundException
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public void exportFile(ProtectedFile file, OutputStream out) throws FileNotFoundException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, IOException {
        //File outFile = new File(targetPath);
        InputStream in =null;
        in = getDecryptedInputStream(file.getFilepath());

        writeFile(in,out);
    }

    //********************** End Export Functions ************************************//

    //********************** Start Encrypt Functions *********************************//

    /**
     * Given a filepath, this method creates an OutputStream that can write data to this location and with encryption on the fly
     * @param targetPath
     * @return CipherOutputStream that links to the path and that writes data with encryption on the fly
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws FileNotFoundException
     */
    public CipherOutputStream getEncryptedOutputStream(String targetPath) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, FileNotFoundException {
        SecretKey key = getSecretKey();
        final Cipher cipher = getEncryptCipher(key);

        saveEncryptionIv(targetPath,cipher.getIV());

        OutputStream out = getFileOutputStream(targetPath);
        return new CipherOutputStream(out, cipher);
    }

    /**
     * This method returns a cipher object for encryption using AES-GCM with no padding
     * @param secretKey
     * @return cipher in Encrypt mode with AES_GCM algorithm
     */
    private Cipher getEncryptCipher(SecretKey secretKey) {
        final Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            Log.e(TAG,e.toString());
        }

        return null;
    }

    /**
     * This method save the initial vector (iv) for encryption and decryption in the persistent storage
     * Using the filepath as the key to retrieve the iv
     * @param filePath
     * @param iv
     */
    private void saveEncryptionIv(String filePath, byte[] iv) {
        SecureSharePreference secretShare = SecureSharePreference.getInstance(mContext, SHARE_PREFERENCE_TAG);
        SharedPreferences.Editor editor = secretShare.edit();

        String encodeIv = Base64.encodeToString(iv, Base64.DEFAULT);
        editor.putString(filePath,encodeIv);
        editor.commit();
    }

    //************************* End Encrypt Functions  ************************************//


    //************************* Start Decrypt Functions **********************************//
    /**
     * This method returns a cipher object for decryption using AES-GCM with no padding
     * @param secretKey
     * @param encryptionIv
     * @return cipher in Decrypt mode with AES_GCM algorithm
     */
    private Cipher getDecryptCipher(SecretKey secretKey,byte[] encryptionIv){
        try {

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            Log.e(TAG,e.toString());
        }

        return null;
    }

    /**
     * Get the Initial Vector (IV) from the persistent storage for decryption
     * @param filepath
     * @return
     */
    private byte[] getEncryptionIv(String filepath) throws IOException {
        SecureSharePreference secretShare = SecureSharePreference.getInstance(mContext, SHARE_PREFERENCE_TAG);

        String encodeIv = secretShare.getString(filepath,null);

        if(encodeIv == null) throw new IOException("Cannot find encryption IV for the file: " +filepath);
        byte[] decodedArr = Base64.decode(encodeIv,Base64.DEFAULT);
        return decodedArr;
    }

    /**
     * Return the CustomCipherInputStream that reads the encrypted data and decrypted it on the fly.
     * @param filepath
     * @return CipherInputStream that decrypts data on the fly
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws FileNotFoundException
     */
    public CustomCipherInputStream getDecryptedInputStream(String filepath) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, FileNotFoundException, IOException {
        CustomCipherInputStream cin = null;

        FileInputStream in = new FileInputStream(filepath);

        SecretKey key =getSecretKey();
        byte[] encryptionIv = getEncryptionIv(filepath);
        Cipher cipher = getDecryptCipher(key,encryptionIv);
        cin = new CustomCipherInputStream(in,cipher);
        return cin;
    }

    /**
     * Return the CustomCipherInputStream that reads the encrypted data and decrypted it on the fly.
     * Noted that the CustomCipherInputStream finishes the decryption when the close() method is called,
     * which means that the encrypted data can be read only after the all the data is decrypted.
     * @param file
     * @return CipherInputStream that decrypts data on the fly
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
     * @throws UnrecoverableEntryException
     * @throws KeyStoreException
     */
    public CustomCipherInputStream getDecryptedInputStream(ProtectedFile file) throws NoSuchAlgorithmException, FileNotFoundException, UnrecoverableEntryException, KeyStoreException, IOException {
        return getDecryptedInputStream(file.getFilepath());
    }
    //************************* End Decrypt Functions **********************************//


    //************************* Start Private Key Functions ****************************//

    /**
     * This method generate a private in the keystore for encrypting and decrypting the files.
     * According to different version of Android, it uses different approaches to create private keys.
     * Noted that now it only works on Android 6 and above
     */
    protected void generateKey(){

        final int expiredYearOfKey = 2048;
        try {
            if(hasPrivateKey()) return; // if there is already a private key, stop the method

            // If it is Android 6 or above, create the AES key directly in the keystore.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                final KeyGenerator keyGenerator = KeyGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(keyAlias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build();

                keyGenerator.init(keyGenParameterSpec);
                keyGenerator.generateKey();

            }else{
                // if it is Android 5 or lower, do it differently
                // TODO

                /*Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.set(Calendar.YEAR,expiredYearOfKey);

                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(mContext)
                        .setAlias(keyAlias)
                        .setSubject(new X500Principal("CN="+R.string.app_name))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                */

            }

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            Log.e(TAG,e.toString());
        }
    }

    /**
     * This method setup the keystore object to manage the private key
     */
    private void setupKeyStore() {
        try {
            keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            Log.e(TAG,e.toString());
        }
    }

    /**
     * Check if there is already a private key in the vault
     * @return if there is a private key
     */
    protected boolean hasPrivateKey(){
        try {
            return keystore.isKeyEntry(keyAlias);
        } catch (KeyStoreException e) {
            Log.e(TAG,e.toString());
            return false;
        }
    }

    /**
     * Get the Secret key entry stored in the Android KeyStore with the default keyAlias
     * @return
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private SecretKey getSecretKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {

        final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keystore
                .getEntry(keyAlias, null);

        final SecretKey secretKey = secretKeyEntry.getSecretKey();

        return secretKey;
    }

    //************************* End Private Key Functions ***************************//

    /**
     * Return the folder's name of the vault
     * @return
     */
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

    /**
     * This method returns a scaled thumbnail of a picture in form of a Bitmap,
     * given the size of the container of the thumbnail picture.
     * We need to make it run on the back ground for fast loading speed
     * @param file
     * @param sizeX
     * @param sizeY
     * @return
     */
    private Bitmap getPicturePreview(ProtectedFile file,int sizeX,int sizeY) {
        //return Utils.getScaledBitmap(mContext,file.getFilepath(),sizeX,sizeY);
        Bitmap bitmap;

        try {
            InputStream in = getDecryptedInputStream(file.getFilepath());
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
            in = getDecryptedInputStream(file);
            //in.close();
            return BitmapFactory.decodeStream(in,null,options);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            Log.e(TAG,e.toString());
        }

        return null;
    }


    /**
     * Get an OutPutStream to a particular file
     * @param targetPath
     * @return
     * @throws FileNotFoundException
     */
    private OutputStream getFileOutputStream(String targetPath) throws FileNotFoundException {
        return new FileOutputStream(targetPath);

    };

    /**
     * This method reads all the data from an InputStream and write them to the OutPutStream
     * @param in
     * @param out
     */
    private void writeFile(InputStream in, OutputStream out){
        try {

            byte[] buffer = new byte[1024];
            int i;

            int tot = 0;
            while ((i = in.read(buffer)) != -1) {
                tot += i;
                out.write(buffer, 0, i);
            }

            Log.v(TAG,"File Length:"+tot);
            out.flush();
            out.close();
            in.close();

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



    /**
     * Test if the encryption works properly by encrypting a text and decrypting it,
     * then showing the results on the logcat
     */
    public void showEncryptTest() {
        SecretKey key = null;
        try {
            key = getSecretKey();
            String tag = "test_text";
            String text = "text text text";
            Cipher en = getEncryptCipher(key);
            saveEncryptionIv(tag,en.getIV());

            byte[] encryptedText  = en.doFinal(text.getBytes(StandardCharsets.UTF_8));

            Cipher de = getDecryptCipher(key,getEncryptionIv(tag));

            byte[] decryptedText =de.doFinal(encryptedText);

            Log.v(tag,"Encrypted text:" + Base64.encodeToString(encryptedText,Base64.DEFAULT));
            Log.v(tag,"Decrypted text:" + new String(decryptedText, StandardCharsets.UTF_8));

        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException | BadPaddingException | IllegalBlockSizeException | IOException e) {
            Log.e(TAG,e.toString());
        }


    }
}
