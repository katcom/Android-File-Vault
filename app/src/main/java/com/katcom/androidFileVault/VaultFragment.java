package com.katcom.androidFileVault;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class VaultFragment extends Fragment {
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private RecyclerView mFileRecyclerView;
    private FileAdapter mAdapter;
    private FileVault mVault;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_vault,container,false);
        setHasOptionsMenu(true);
        mDrawer = view.findViewById(R.id.drawer_layout);
        mNavigation = view.findViewById(R.id.navigation_view);

        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                return false;
            }
        });

        mFileRecyclerView = view.findViewById(R.id.vault_file_recycler_view);
        mFileRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        copySampleFiles();

        mVault = FileVault.get(this.getContext());
        updateUI();
        return view;
    }

    private void copySampleFiles() {
        File file = new File(this.getContext().getFilesDir() + "/" + FileVault.sVaultDirectory);
        if(!file.exists()){
            file.mkdir();
        }
        Utils.copyFilesFromAsset(this.getContext(),"sample_files",FileVault.sVaultDirectory);
    }

    private void updateUI() {
        List<ProtectedFile> files = mVault.getFiles();

        mAdapter = new FileAdapter(files);
        mFileRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.export_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

        }
        return true;
    }

    private class FileHolder extends RecyclerView.ViewHolder{
        public TextView mFileTextView;
        public FileHolder(@NonNull View itemView) {
            super(itemView);
            mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_filename_name_text_view);

        }
    }
    private class FileAdapter extends  RecyclerView.Adapter<FileHolder>{
        private List<ProtectedFile> mFiles;

        public FileAdapter(List<ProtectedFile> files){
            mFiles = files;
        }
        @NonNull
        @Override
        public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.item_file_preview_filename,parent,false);

            return new FileHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolder fileHolder, int position) {
            ProtectedFile file = mFiles.get(position);
            fileHolder.mFileTextView.setText(file.getFilename());
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }
}
