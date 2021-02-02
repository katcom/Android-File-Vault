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

        if(isPasswordCorrect(digestPassword)) {
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

    private boolean isPasswordCorrect(String digestPassword) {
        SecureSharePreference secretShare = SecureSharePreference.getInstance(getContext(),Login.LOGIN_INFO_PREF_FILE);
        String passwordRecord = secretShare.getString(Login.TAG_PASSWORD,null);

        return digestPassword.equals(passwordRecord);
    }

    private String getPasswordHash(String password){
        return getSHA256MessageDigest(password);
    }


}
