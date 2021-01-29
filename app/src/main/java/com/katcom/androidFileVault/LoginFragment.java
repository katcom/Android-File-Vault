package com.katcom.androidFileVault;

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginFragment extends Fragment {
    private EditText mPasswordInput;
    private Button mLoginButton;
    private static final String TAG="LoginFragment";
    private static final String SHA_256_TAG = "SHA-256";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login,container,false);

        mPasswordInput = view.findViewById(R.id.password_input);
        mLoginButton = view.findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        return view;
    }
    private void login() {
        String password = mPasswordInput.getText().toString();
        String digestPassword = getPasswordHash(password);
        Log.v(TAG,"digest pwd : " + digestPassword);

        boolean isPasswordCorrect = checkPassword(digestPassword);

        if(isPasswordCorrect) {
            Intent i = new Intent(this.getContext(), VaultActivity.class);
            startActivity(i);
        }else{
            showLoginFailureDialog();

        }

    }

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

    private boolean checkPassword(String digestPassword) {
        return digestPassword.equals(getPasswordHash("abc"));
    }

    private String getPasswordHash(String password){
        return getSHA256MessageDigest(password);
    }

    private String getSHA256MessageDigest(String str) {
        String digestText = "";
        try{

            MessageDigest md  = MessageDigest.getInstance(SHA_256_TAG);
            md.update(str.getBytes());
            byte[] tempDigestArray = md.digest();
            digestText = Arrays.toString(tempDigestArray);

        }catch (NoSuchAlgorithmException e){
            Log.e(TAG,"No algorithm found with tag : "+ SHA_256_TAG,e);
        }
        return digestText;
    }

}
