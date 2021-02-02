package com.katcom.androidFileVault.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.katcom.androidFileVault.R;
import com.katcom.androidFileVault.SecureSharePreference.SecureSharePreference;
import com.katcom.androidFileVault.VaultActivity;

import static com.katcom.androidFileVault.utils.PasswordHelper.getPasswordHash;

public class SetPasswordConfirmFragment extends Fragment implements Login {
    private static final String ARG_FIRST_PASS_HASH = "first_password_hash_code";
    private Button mPreviousButton;
    private Button mFinishButton;
    private TextView mPasswordTextView;
    private String firstPasswordHash;
    private String secondPasswordHash;

    private SetPasswordConfirmFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_set_password_again,container,false);
        mPasswordTextView = v.findViewById(R.id.set_password_text_view_again);

        mFinishButton = v.findViewById(R.id.button_set_password_again);
        mPreviousButton = v.findViewById(R.id.button_set_password_back);

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPassword();
            }
        });

        getFirstPasswordHash();
        return v;
    }

    private void getFirstPasswordHash() {
        firstPasswordHash = (String)getArguments().getSerializable(ARG_FIRST_PASS_HASH);
    }

    public static SetPasswordConfirmFragment newInstance(String firstPasswordHash){
        Bundle args = new Bundle();
        args.putSerializable(ARG_FIRST_PASS_HASH,firstPasswordHash);

        SetPasswordConfirmFragment fragment = new SetPasswordConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void confirmPassword(){
        String secondPassword = mPasswordTextView.getText().toString();
        secondPasswordHash = getPasswordHash(secondPassword);

        if(isTwoPasswordEqual()){
            storePassword(secondPasswordHash);
            openFileVault();
        }else{
            showPasswordsNotMatchError();
            clearInputTextView();
        }
    }

    private void storePassword(String passwordHash) {
        SecureSharePreference secretShare = SecureSharePreference.getInstance(getContext(),LOGIN_INFO_PREF_FILE);
        Editor editor = secretShare.edit();
        editor.putString(TAG_PASSWORD,passwordHash);
        editor.commit();
    }

    private void clearInputTextView() {
        mPasswordTextView.setText("");
    }

    private void showPasswordsNotMatchError() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(R.string.two_password_not_match)
                .setTitle(R.string.two_password_not_match_title)
                .setPositiveButton(R.string.enter_password_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialogBuilder.create().show();
    }

    private void openFileVault() {
        Intent i = new Intent(this.getContext(), VaultActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private boolean isTwoPasswordEqual() {
        return secondPasswordHash.equals(firstPasswordHash);
    }

}
