/**
 * This fragment allow users enter the password again to complete the sign up.
 */
package com.katcom.androidFileVault.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
        View v= inflater.inflate(R.layout.fragment_set_password_confirm,container,false);
        mPasswordTextView = v.findViewById(R.id.set_password_text_view_again);

        mFinishButton = v.findViewById(R.id.button_set_password_again); // the finish button
        mPreviousButton = v.findViewById(R.id.button_set_password_back); // the go back button

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPassword();
            }
        });

        getFirstPasswordHash(); // get the password user has entered before
        return v;
    }

    /**
     * Get the first password user has entered in the previous fragment
     */
    private void getFirstPasswordHash() {
        firstPasswordHash = (String)getArguments().getSerializable(ARG_FIRST_PASS_HASH);
    }

    /**
     * Return an instance of this fragment, storing the first password user has set as an argument
     * When this fragment shown, it will get the first password,
     * and compare it with the second password user just entered in this fragment
     * @param firstPasswordHash
     * @return SetPasswordConfirmFragment
     */
    public static SetPasswordConfirmFragment newInstance(String firstPasswordHash){
        Bundle args = new Bundle();
        args.putSerializable(ARG_FIRST_PASS_HASH,firstPasswordHash);

        SetPasswordConfirmFragment fragment = new SetPasswordConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This method check if the password entered is equal to the first password user has entered in the SetPasswordFragment.
     * If they are, save the password and launch the vault.
     * If not, display a Error message and ask the user to enter the password again.
     */
    private void confirmPassword(){
        String secondPassword = mPasswordTextView.getText().toString(); // password the user enters for the second  time
        secondPasswordHash = getPasswordHash(secondPassword);

        if(isTwoPasswordEqual()){
            storePassword(secondPasswordHash);  //save password in the disk
            openFileVault();                    // Launch the vault activity
        }else{
            showPasswordsNotMatchError();       // tell user the password is wrong
            clearInputTextView();               // clear the wrong password entered to enter password again
        }
    }

    /**
     * Save the digest of the password to the share preference
     * @param passwordHash
     */
    private void storePassword(String passwordHash) {
        SecureSharePreference secretShare = SecureSharePreference.getInstance(getContext(),LOGIN_INFO_PREF_FILE);
        Editor editor = secretShare.edit();
        editor.putString(TAG_PASSWORD,passwordHash);
        editor.commit();
    }


    /**
     * This method shows a dialog informing the user that the password entered doesn't match first password they entered before
     */
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

    /**
     * This method open the file vault by starting the VaultActivity
     */
    private void openFileVault() {
        Intent i = new Intent(this.getContext(), VaultActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /**
     * Check if the passwords user has entered are the same
     * @return if the passwords are equal
     */
    private boolean isTwoPasswordEqual() {
        return secondPasswordHash.equals(firstPasswordHash);
    }


    /**
     * This method empties the input view for user to enter the password again
     */
    private void clearInputTextView() {
        mPasswordTextView.setText("");
    }
}
