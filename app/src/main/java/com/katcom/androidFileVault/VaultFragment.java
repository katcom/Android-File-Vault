package com.katcom.androidFileVault;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.katcom.androidFileVault.fileRecyclerView.FilePreviewAdapter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class VaultFragment extends Fragment {
    private static final int REQUEST_FILE_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private RecyclerView mFileRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ImageButton mZoomInButton;
    private ImageButton mZoomOutButton;
    private FileManager mVault;
    private int columnsInGrid = 1;
    private String mPreviewMode;
    private List<String> modes = PreviewMode.getModeList();
    private int currentModeIndex = 0;
    private final String DIALOG_IMAGE_TAG = "DialogImage";
    private final String TAG="VaultFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
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
                switch (id){
                    case R.id.action_import:
                        showChooseFileActivity();
                        break;
                    case R.id.action_help:
                        showHelpInfo();
                        break;
                    case R.id.action_about:
                        showEncryptTest();
                }
                return false;
            }
        });

        mFileRecyclerView = view.findViewById(R.id.vault_file_recycler_view);
        //mFileRecyclerView.setLayoutManager(new LinearLayoutManager(thi));


        // Get a instance of the FileVault object
        mVault = FileManager.get(this.getContext());

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

        mZoomOutButton = view.findViewById(R.id.button_zoom_out);
        mZoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseSmallerItemLayout();  // show lower-fidelity preview image
            }
        });


        return view;
    }

    private void showEncryptTest() {
        mVault.showEncryptTest();
    }

    private void showChooseFileActivity() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent,REQUEST_FILE_CODE);
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
        mAdapter = new FilePreviewAdapter(files,PreviewMode.PREVIEW_BIG,getContext());
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
        mAdapter = new FilePreviewAdapter(files,PreviewMode.PREVIEW_MEDIUM,getContext());
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
        mAdapter = new FilePreviewAdapter(files,PreviewMode.PREVIEW_SMALL,getContext());
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
        mAdapter = new FilePreviewAdapter(files,PreviewMode.FILE_DETAIL,getContext());
        mFileRecyclerView.setAdapter(mAdapter);

        columnsInGrid = 1;
        mFileRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), columnsInGrid));
    }

    public void takePhoto(){
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(imageTakeIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.export_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_export_selected_files:
                Intent i = new Intent(getActivity(),ExportActivity.class);
                startActivity(i);
                break;
            case R.id.menu_take_photo:
                takePhoto();
                break;

        }
        return true;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) return;

        if(requestCode == REQUEST_FILE_CODE && data != null){
            if(data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; i++) {
                    Uri SelectedFile = data.getClipData().getItemAt(i).getUri();
                    importAndEncryptFile(SelectedFile);
                }
            }else{
                Uri selectedFile = data.getData();
                importAndEncryptFile(selectedFile);
            }
        }
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap)extras.get("data");

            try {
                saveImage(image);
                updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImage(Bitmap image) throws IOException {
        mVault.importImage(image);

    }

    public void importAndEncryptFile(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor.moveToFirst()) {
            String filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            Log.v(TAG,filename);
            try {
                String filepath = getContext().getFilesDir() +"/" + mVault.getVaultDirectory() + "/" + filename;
                mVault.importFile(filename,contentResolver.openInputStream(uri),filepath);
                Toast.makeText(getContext(),"Select file: "+filename,Toast.LENGTH_LONG).show();

                updateUI();
            } catch (FileNotFoundException e) {
                Log.e(TAG,e.toString());
            }
        }
    }


    private String getVaultDirectory() {
        return getActivity().getFilesDir() +"/" +mVault.getVaultDirectory();
    }

    protected void showHelpInfo(){
        boolean has = mVault.hasPrivateKey();
    }
}
