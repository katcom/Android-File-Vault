/**
 * This fragment allows user to login the vault by entering the correct password.
 */
package com.katcom.androidFileVault.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.katcom.androidFileVault.R;
import com.katcom.androidFileVault.SecureSharePreference.SecureSharePreference;
import com.katcom.androidFileVault.VaultActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.katcom.androidFileVault.utils.PasswordHelper.getSHA256MessageDigest;

public class LoginFragment extends Fragment implements Login {
    private EditText mPasswordInput;
    private Button mLoginButton;
    private static final String TAG="LoginFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login,container,false);

        mPasswordInput = view.findViewById(R.id.password_input);
        mLoginButton = view.findViewById(R.id.login_button);

        // When the user clicks the login button, check if the password entered match the record
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        return view;
    }

    /**
     * This method checks if the password user enters match the password record in the database.
     * If it does, open the vault to the user
     * Otherwise, display an error message.
     */
    private void login() {
        String password = mPasswordInput.getText().toString();  // get the password
        String digestPassword = getPasswordHash(password);      // get the message digest of the password
        Log.v(TAG,"digest pwd : " + digestPassword);

        if(isPasswordCorrect(digestPassword)) {
            openFileVault();                // right password, open the vault activity
        }else{
            showLoginFailureDialog();       // Wrong password, display an error message
        }

    }

    /**
     * This method open the vault to user by starting the VaultActivity
     */
    private void openFileVault() {
        Intent i = new Intent(this.getContext(), VaultActivity.class);
        startActivity(i);
    }


    /**
     * Display a message using dialog when the user enters a wrong password
     */
    private void showLoginFailureDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("The password you entered is incorrect! Please try again")
                .setTitle("Incorrect Password")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialogBuilder.create().show();
    }

    /**
     * Check if the password the user entered match the record in the database
     * @param digestPassword
     * @return
     */
    private boolean isPasswordCorrect(String digestPassword) {
        SecureSharePreference secretShare = SecureSharePreference.getInstance(getContext(),Login.LOGIN_INFO_PREF_FILE);
        String passwordRecord = secretShare.getString(Login.TAG_PASSWORD,null);

        return digestPassword.equals(passwordRecord);
    }

    /**
     *
     * @param password
     * @return SHA-256 message digest of the password
     */
    private String getPasswordHash(String password){
        return getSHA256MessageDigest(password);
    }


}
