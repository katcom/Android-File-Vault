/**
 * In this fragment, user sets a password for entering the fault, then is directed to another
 * fragment to enter the password again for confirmation.
 */
package com.katcom.androidFileVault.login;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.katcom.androidFileVault.R;
import com.katcom.androidFileVault.utils.PasswordHelper;

import org.w3c.dom.Text;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

import static com.katcom.androidFileVault.utils.PasswordHelper.getPasswordHash;
import static com.katcom.androidFileVault.utils.PasswordHelper.getSHA256MessageDigest;

public class SetPasswordFragment extends Fragment  implements Login{
    private Button mButtonSetPassword;

    private TextView mPasswordInput;

    private String firstPassword;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_set_password,container,false);

        mButtonSetPassword = view.findViewById(R.id.button_set_password);
        mPasswordInput = view.findViewById(R.id.set_password_text_view);

        mButtonSetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rawPassword = mPasswordInput.getText().toString(); // get the password
                firstPassword = getPasswordHash(rawPassword);   // make message digest of the password
                startConfirmPassword();
            }

        });

        return view;
    }

    /**
     * Show a new fragment where user would enter the password again.
     */
    private void startConfirmPassword() {
        // The new fragment needs the first password user has entered for comparision.
        Fragment confirmFragment = SetPasswordConfirmFragment.newInstance(firstPassword);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container,confirmFragment)
                .commit();
    }



}
