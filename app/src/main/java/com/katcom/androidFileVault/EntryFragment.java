/**
 * The entry point of the file vault.
 * When the user opens the vault for the first time, it shows a screen to help user setup password.
 * And open the vault after the sign up.
 * If the user has setup password before, it displays login screen to allow user login to the vault.
 */
package com.katcom.androidFileVault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.katcom.androidFileVault.SecureSharePreference.SecureSharePreference;
import com.katcom.androidFileVault.login.Login;
import com.katcom.androidFileVault.login.LoginActivity;
import com.katcom.androidFileVault.login.SetPasswordActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

public class EntryFragment extends Fragment {
    private static final String TAG = "EntryFragment";
    private Button mVaultOpenButton;
    private FileManager mVault;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_entry,container,false);

        mVaultOpenButton= (Button) view.findViewById(R.id.button_open_vault);

        mVaultOpenButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openVault();
            }
        });

        mVault = FileManager.get(getContext());
        return view;
    }

    /**
     * If it is the first time user opens the vault, it setup the vault by displaying the SetPassword activity,
     * generating a private key for encryption and decryption, and copying sample files into the vault
     * Otherwise, shows the screen where user can login by entering the password.
     */
    private void openVault() {
        if(!hasPassword()){
            // Launch activity to setup password
            setPassword();

            // Generate private key for encryption before copying sample files
            generatePrivateKey();

            // Put some sample files in the vault
            copySampleFiles();
        }else{
            Intent i= new Intent(this.getContext(), LoginActivity.class);
            startActivity(i);
        }
    }

    /**
     * Generate a private key in the vault for encryption and decryption of files
     */
    private void generatePrivateKey() {
            mVault.generateKey();
    }

    /**
     * Import some sample files from assets to the vault
     */
    private void copySampleFiles() {
        File file = new File(this.getContext().getFilesDir() + "/" + FileManager.sVaultDirectory);
        if (!file.exists()) {
            file.mkdir();
        }

        String source = "sample_files";
        String vaultPath = getActivity().getFilesDir() + "/" + FileManager.sVaultDirectory;
        AssetManager assets = getActivity().getAssets();
        try {
            String[] files = assets.list(source);
            for(String filename:files){
                String filepath = source + "/" + filename;
                String targetPath = vaultPath + "/" + filename;
                File outFile = new File(targetPath);
                if(outFile.exists()){
                    continue;
                }

                //FileDescriptor in = assets.openFd(filepath).getFileDescriptor().;

                //vault.importFile(filename,in,targetPath);
                //assets.openFd(filepath).close();
                mVault.importFileFromAsset(filename,filepath,targetPath);

            }
        }catch (IOException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableEntryException e){
            Log.e(TAG,e.toString());
        }
    }

    /**
     * Launch the activity to allow user setup a password
     */
    private void setPassword() {
        Intent intent = new Intent(this.getContext(), SetPasswordActivity.class);
        startActivity(intent);
    }

    /**
     * Check if the user has set a password before
     * @return if a password has been set
     */
    private boolean hasPassword(){
        SecureSharePreference secretShare = SecureSharePreference.getInstance(getContext(), Login.LOGIN_INFO_PREF_FILE);
        return secretShare.contains(Login.TAG_PASSWORD);
    }
}
