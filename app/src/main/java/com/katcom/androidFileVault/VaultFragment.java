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
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class VaultFragment extends Fragment {
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private RecyclerView mFileRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Button mZoomInButton;
    private Button mZoomOutButton;
    private FileVault mVault;

    private String mPreviewMode;
    private List<String> modes = PreviewMode.getModeList();
    private int currentModeIndex = 0;

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
        mPreviewMode = PreviewMode.PREVIEW_SMALL;
        updateUI();

        mZoomInButton = view.findViewById(R.id.button_zoom_in);
        mZoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseBiggerItemLayout();
            }
        });

        mZoomInButton = view.findViewById(R.id.button_zoom_out);
        mZoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseSmallerItemLayout();
            }
        });
        return view;
    }

    private void chooseSmallerItemLayout() {
        if(currentModeIndex > 0){
            currentModeIndex --;
            mPreviewMode = modes.get(currentModeIndex);
        }
        updateUI();
    }

    private void chooseBiggerItemLayout() {
        if(currentModeIndex < modes.size()-1){
            currentModeIndex ++;
            mPreviewMode = modes.get(currentModeIndex);
        }
        updateUI();
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
        switch(mPreviewMode){
            case PreviewMode.FILE_DETAIL:
                showFileDetail(files);
                break;
            case PreviewMode.PREVIEW_SMALL:
                showSmallPreview(files);
                break;
        }
        currentModeIndex = modes.indexOf(mPreviewMode);

        /* original code
        List<ProtectedFile> files = mVault.getFiles();
        mAdapter = new FileAdapterFileDetail(files);
        mFileRecyclerView.setAdapter(mAdapter);
        */
    }

    private void showSmallPreview(List<ProtectedFile> files) {
        mAdapter = new FileAdapterPreviewSmall(files);
        mFileRecyclerView.setAdapter(mAdapter);
    }

    private void showFileDetail(List<ProtectedFile> files) {
        mAdapter = new FileAdapterFileDetail(files);
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

    private class FileHolderFileDetail extends RecyclerView.ViewHolder{
        public TextView mFileTextView;
        public FileHolderFileDetail(@NonNull View itemView) {
            super(itemView);
            mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_filename_name_text_view);

        }
    }
    private class FileAdapterFileDetail extends  RecyclerView.Adapter<FileHolderFileDetail>{
        private List<ProtectedFile> mFiles;

        public FileAdapterFileDetail(List<ProtectedFile> files){
            mFiles = files;
        }
        @NonNull
        @Override
        public FileHolderFileDetail onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.item_file_preview_filename,parent,false);

            return new FileHolderFileDetail(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolderFileDetail fileHolder, int position) {
            ProtectedFile file = mFiles.get(position);
            fileHolder.mFileTextView.setText(file.getFilename());
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }


    private class FileHolderPreviewSmall extends RecyclerView.ViewHolder{
        public TextView mFileTextView;
        public FileHolderPreviewSmall(@NonNull View itemView) {
            super(itemView);
            mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_filename_text_view);
        }
    }
    private class FileAdapterPreviewSmall extends  RecyclerView.Adapter<FileHolderPreviewSmall>{
        private List<ProtectedFile> mFiles;

        public FileAdapterPreviewSmall(@NonNull List<ProtectedFile> files){
            mFiles = files;
        }

        @Override
        public FileHolderPreviewSmall onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.item_file_preview_small,parent,false);

            return new FileHolderPreviewSmall(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolderPreviewSmall fileHolder, int position) {
            ProtectedFile file = mFiles.get(position);
            fileHolder.mFileTextView.setText(file.getFilename());

        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }

}
