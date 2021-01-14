package com.katcom.androidFileVault;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        Intent i= new Intent(this.getContext(),LoginActivity.class);
        startActivity(i);
    }
}
