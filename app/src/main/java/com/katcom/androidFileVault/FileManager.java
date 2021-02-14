package com.katcom.androidFileVault;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class FileManager{
    public static String TAG ="FileVault"; // For debug
    public static String sVaultDirectory = "FileVaultOne";  // The directory of the vault in the private storage of this app
    private static FileManager sVault;    // This class is a singleton, only one instance allowed
    private List<ProtectedFile> mFiles;     // Entries of all files
    private Context mContext;           // Android context, used to get to the private storage
    private final static String keyAlias = "FileVault";
    private KeyStore keystore;
    private FileManager(Context context){
        mContext= context;

        mFiles = new ArrayList<ProtectedFile>();

        setupKeyStore();
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

        if(!vaultFolder.exists()){
            vaultFolder.mkdirs();
        }
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

    private void importAndEncrypt(FileDescriptor inFile, String targetPath) throws FileNotFoundException {
        OutputStream out=null;
        InputStream in =null;
        in = new FileInputStream(inFile);
        out = getFileOutputStream(targetPath);

        writeFile(in,out);
    }

    public void importFileFromAsset(String filename,String filepath, String targetPath) throws IOException {
        AssetManager asset = mContext.getAssets();

        OutputStream out=null;
        InputStream in =null;

        in = asset.open(filepath);
        out = getFileOutputStream(targetPath);

        writeFile(in,out);
        addFileEntry(filename,targetPath);
    }

    public void importImage(Bitmap image) throws IOException {
        File outFile = createImageFile();
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(outFile));
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();

            addFileEntry(outFile.getName(),outFile.getPath());
        } finally {
            out.close();
        }
    }


    public CipherOutputStream getEncryptedOutputStream(String targetPath) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, FileNotFoundException {
        SecretKey key = getSecretKey();
        final Cipher cipher = getEncryptCipher(key);
        saveEncryptionIv(cipher.getIV());
        OutputStream out = getFileOutputStream(targetPath);
        return new CipherOutputStream(out, cipher);
    }

    private Cipher getEncryptCipher(SecretKey secretKey) {
        final Cipher cipher;
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(getDefaultEncryptionIv());

            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,ivParameterSpec);

            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Cipher getDecryptCipher(SecretKey secretKey){
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(getDefaultEncryptionIv());

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,ivParameterSpec);

            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    private byte[] getDefaultEncryptionIv() {
        return "1234567891234567".getBytes();
    }


    private void saveEncryptionIv(byte[] iv) {
    }

    private byte[] getEncryptionIv(String filepath){
        return new byte[16];
    }

    public CipherInputStream getDecryptedInputStream(String filepath) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        CipherInputStream in = null;

        SecretKey key =getSecretKey();
        Cipher cipher = getDecryptCipher(key);
        in = new CipherInputStream(in,cipher);
        return in;
    }
    private OutputStream getFileOutputStream(String targetPath) throws FileNotFoundException {
        return new FileOutputStream(targetPath);

    };

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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(mContext.getFilesDir() +"/" +sVaultDirectory);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    protected void generateKey(){
        final int expiredYearOfKey = 2048;
        try {
            final KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(keyAlias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build();

                keyGenerator.init(keyGenParameterSpec);
                keyGenerator.generateKey();

            }else{
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

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }


    private void setupKeyStore() {
        try {
            keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean hasPrivateKey(){
        try {
            return keystore.isKeyEntry(keyAlias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    private SecretKey getSecretKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        if(hasPrivateKey()) return getSecretKey();

        final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keystore
                .getEntry(keyAlias, null);

        final SecretKey secretKey = secretKeyEntry.getSecretKey();

        return secretKey;
    }

}
