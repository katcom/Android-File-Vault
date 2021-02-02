package com.katcom.androidFileVault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.katcom.androidFileVault.SecureSharePreference.SecureSharePreference;
import com.katcom.androidFileVault.login.Login;
import com.katcom.androidFileVault.login.LoginActivity;
import com.katcom.androidFileVault.login.SetPasswordActivity;

public class EntryFragment extends Fragment {
    private Button mVaultOpenButton;

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

        return view;
    }

    private void openVault() {
        if(!hasPassword()){
            setPassword();
        }else{
            Intent i= new Intent(this.getContext(), LoginActivity.class);
            startActivity(i);
        }
    }

    private void setPassword() {
        Intent intent = new Intent(this.getContext(), SetPasswordActivity.class);
        startActivity(intent);
    }

    private boolean hasPassword(){
        SecureSharePreference secretShare = SecureSharePreference.getInstance(getContext(), Login.LOGIN_INFO_PREF_FILE);
        return secretShare.contains(Login.TAG_PASSWORD);
    }
}
