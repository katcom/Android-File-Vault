package com.katcom.androidFileVault;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    private int columnsInGrid = 1;
    private String mPreviewMode;
    private List<String> modes = PreviewMode.getModeList();
    private int currentModeIndex = 0;
    private final String DIALOG_IMAGE_TAG = "DialogImage";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vault, container, false);
        setHasOptionsMenu(true);

        // Bind the controllers to the elements in layout
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
        //mFileRecyclerView.setLayoutManager(new LinearLayoutManager(thi));

        // Put some sample files in the vault
        copySampleFiles();

        // Get a instance of the FileVault object
        mVault = FileVault.get(this.getContext());

        //  Set the preview mode, by default small preview
        mPreviewMode = PreviewMode.PREVIEW_SMALL;
        updateUI();

        // Setup the zoom-in and zoom-out button
        mZoomInButton = view.findViewById(R.id.button_zoom_in);
        mZoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseBiggerItemLayout(); // show  higher-fidelity preview image
            }
        });

        mZoomInButton = view.findViewById(R.id.button_zoom_out);
        mZoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseSmallerItemLayout();  // show lower-fidelity preview image
            }
        });
        return view;
    }

    /*
        Change the preview mode to on fidelity lower, that is, to show smaller preview image than current mode does.
        The lowest-fidelity is just showing the filename alone.
     */
    private void chooseSmallerItemLayout() {
        if (currentModeIndex > 0) {
            currentModeIndex--;
            mPreviewMode = modes.get(currentModeIndex);
        }
        updateUI();
    }

    /*
    Change the preview mode to on fidelity higher, that is, to show bigger preview image than current mode does
    */
    private void chooseBiggerItemLayout() {

        if (currentModeIndex < modes.size() - 1) {
            currentModeIndex++;
            mPreviewMode = modes.get(currentModeIndex);
        }
        updateUI();
    }

    /*
        This method copy the sample files from Asset to the private storage.
     */
    private void copySampleFiles() {
        File file = new File(this.getContext().getFilesDir() + "/" + FileVault.sVaultDirectory);
        if (!file.exists()) {
            file.mkdir();
        }
        Utils.copyFilesFromAsset(this.getContext(), "sample_files", FileVault.sVaultDirectory);
    }

    /**
     * Update the recylerview according to the preview mode
     */
    private void updateUI() {
        List<ProtectedFile> files = mVault.getFiles();
        switch (mPreviewMode) {
            case PreviewMode.FILE_DETAIL:
                showFileDetail(files);
                break;
            case PreviewMode.PREVIEW_SMALL:
                showSmallPreview(files);
                break;
            case PreviewMode.PREVIEW_MEDIUM:
                showMediumPreview(files);
                break;
            case PreviewMode.PREVIEW_BIG:
                showBigPreview(files);
                break;
        }
        currentModeIndex = modes.indexOf(mPreviewMode);

        /* original code
        List<ProtectedFile> files = mVault.getFiles();
        mAdapter = new FileAdapterFileDetail(files);
        mFileRecyclerView.setAdapter(mAdapter);
        */
    }

    /**
     * Show big-size preview image in the recycler view.
     * Each row has two images.
     *
     * @param files
     */
    private void showBigPreview(List<ProtectedFile> files) {
        mAdapter = new FileAdapterPreviewBig(files);
        mFileRecyclerView.setAdapter(mAdapter);
        columnsInGrid = 2;
        mFileRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), columnsInGrid));
    }

    /**
     * Show the medium-fidelity preview in the recylerview, in which the medium-size image of the files are shown ,
     * Each row has three images shown.
     *
     * @param files
     */
    private void showMediumPreview(List<ProtectedFile> files) {
        mAdapter = new FileAdapterPreviewMedium(files);
        mFileRecyclerView.setAdapter(mAdapter);
        columnsInGrid = 3;
        mFileRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), columnsInGrid));

    }

    /**
     * Show in the recylerview the small preview of the files, in which a small-size image of the file and its file name is shown.
     * In this mode, each row shows four elements (previews of files).
     *
     * @param files
     */
    private void showSmallPreview(List<ProtectedFile> files) {
        columnsInGrid = 4;
        mAdapter = new FileAdapterPreviewSmall(files);
        mFileRecyclerView.setAdapter(mAdapter);

        columnsInGrid = 4;
        mFileRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), columnsInGrid));
    }

    /**
     * The preview mode shows only the filenames of the file, without preview images.
     * In this mode, each row has only one element, showing information of one file.
     *
     * @param files
     */
    private void showFileDetail(List<ProtectedFile> files) {
        mAdapter = new FileAdapterFileDetail(files);
        mFileRecyclerView.setAdapter(mAdapter);

        columnsInGrid = 1;
        mFileRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), columnsInGrid));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.export_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;
    }

    public void viewImage(ProtectedFile file) {
        Intent intent = ImageViewerActivity.newIntent(getActivity(), file.getFilename(), file.getFilepath());
        startActivity(intent);
    }
//////////// Utils for RecyclerView ///////////////////
/**
 **   The controller of small preview, showing a small-size preview image of files, along with their file names
 */
public class FileHolderPreviewSmall extends RecyclerView.ViewHolder {
    public TextView mFileTextView;
    public ImageView mImageView;
    public FileHolderPreviewSmall(@NonNull View itemView) {
        super(itemView);
        mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_filename_text_view);
        mImageView = (ImageView) itemView.findViewById(R.id.file_preview_small_image_view);
    }

}

    public class FileAdapterPreviewSmall extends  RecyclerView.Adapter<FileHolderPreviewSmall> {
        private List<ProtectedFile> mFiles;

        public FileAdapterPreviewSmall(@NonNull List<ProtectedFile> files) {
            mFiles = files;
        }

        @Override
        public FileHolderPreviewSmall onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.item_file_preview_small, parent, false);

            return new FileHolderPreviewSmall(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolderPreviewSmall fileHolder, int position) {
            final ProtectedFile file = mFiles.get(position);
            fileHolder.mFileTextView.setText(file.getFilename());   // Bind the file name to the user interface
            fileHolder.mImageView.setImageBitmap(mVault.getPreview(file,60,60)); // Bind the preview image to the user interface
            fileHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage(file);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }

    /*
        The controller of low-fidelity preview, showing only the file names
     */
    private class FileHolderFileDetail extends RecyclerView.ViewHolder {
        public TextView mFileTextView;

        public FileHolderFileDetail(@NonNull View itemView) {
            super(itemView);
            mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_filename_name_text_view);

        }
    }

    private class FileAdapterFileDetail extends RecyclerView.Adapter<FileHolderFileDetail> {
        private List<ProtectedFile> mFiles;

        public FileAdapterFileDetail(List<ProtectedFile> files) {
            mFiles = files;
        }

        @NonNull
        @Override
        public FileHolderFileDetail onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.item_file_preview_filename, parent, false);

            return new FileHolderFileDetail(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolderFileDetail fileHolder, int position) {
            final ProtectedFile file = mFiles.get(position);
            //fileHolder.mFileTextView.setText(file.getFilename());
            fileHolder.mFileTextView.setText(file.getFilename()); // Bind the filename to the individual cell in the recycler view
            fileHolder.mFileTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage(file);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }


    /*
     *   The controller of the medium-fidelity view, showing a medium-size preview image of the files
     */
    private class FileHolderPreviewMedium extends RecyclerView.ViewHolder{
        //public TextView mFileTextView;
        public ImageView mImageView;
        public FileHolderPreviewMedium(@NonNull View itemView) {
            super(itemView);
            //mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_medium_imageView);
            mImageView = itemView.findViewById(R.id.file_preview_medium_imageView);

        }
    }
    private class FileAdapterPreviewMedium extends  RecyclerView.Adapter<FileHolderPreviewMedium>{
        private List<ProtectedFile> mFiles;

        public FileAdapterPreviewMedium(@NonNull List<ProtectedFile> files){
            mFiles = files;
        }

        @Override
        public FileHolderPreviewMedium onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.item_file_preview_medium,parent,false);

            return new FileHolderPreviewMedium(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolderPreviewMedium fileHolder, int position) {
            final ProtectedFile file = mFiles.get(position);
            //fileHolder.setText(file.getFilename());
            fileHolder.mImageView.setImageBitmap(mVault.getPreview(file,120,120)); // Bind the picture to the view
            fileHolder.mImageView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    viewImage(file);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }

    /*
     *   The controller of the high-fidelity view, showing a big preview image of the files
     */
    private class FileHolderPreviewBig extends RecyclerView.ViewHolder{
        //public TextView mFileTextView;
        public ImageView mImageView;
        public FileHolderPreviewBig(@NonNull View itemView) {
            super(itemView);
            //mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_medium_imageView);
            mImageView = itemView.findViewById(R.id.file_preview_big_image_view);

        }
    }
    private class FileAdapterPreviewBig extends  RecyclerView.Adapter<FileHolderPreviewBig>{
        private List<ProtectedFile> mFiles;

        public FileAdapterPreviewBig(@NonNull List<ProtectedFile> files){
            mFiles = files;
        }

        @Override
        public FileHolderPreviewBig onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.item_file_preview_big,parent,false);

            return new FileHolderPreviewBig(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolderPreviewBig fileHolder, int position) {
            final ProtectedFile file = mFiles.get(position);
            //fileHolder.setText(file.getFilename());
            fileHolder.mImageView.setImageBitmap(mVault.getPreview(file,190,190)); // Bind the picture to the view
            fileHolder.mImageView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    viewImage(file);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }
}
