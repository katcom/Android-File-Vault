package com.katcom.androidFileVault;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {
    private EditText mPasswordInput;
    private Button mLoginButton;

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
        Intent i=new Intent(this.getContext(),VaultActivity.class);
        startActivity(i);
    }
}
