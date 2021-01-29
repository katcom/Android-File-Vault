package com.katcom.androidFileVault;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

   /* private EditText mPasswordInput;
    private Button mLoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        mPasswordInput = findViewById(R.id.password_input);
        mLoginButton = findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVault();
            }
        });

    }

    private void openVault() {
        Intent i=new Intent(LoginActivity.this,VaultActivity.class);
        startActivity(i);
    }*/
}
