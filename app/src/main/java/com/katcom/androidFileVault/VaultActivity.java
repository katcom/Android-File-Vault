package com.katcom.androidFileVault;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;

public class VaultActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new VaultFragment();
    }

    /*private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);

        mDrawer = findViewById(R.id.drawer_layout);
        mNavigation = findViewById(R.id.navigation_view);

        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                return false;
            }
        });

        File file = new File(this.getFilesDir() + "/FileVaultOne");
        if(!file.exists()){
            file.mkdir();
        }
        Utils.copyFilesFromAsset(this.getApplicationContext(),"sample_files","FileVaultOne");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.export_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

        }
        return true;
    }*/
}
