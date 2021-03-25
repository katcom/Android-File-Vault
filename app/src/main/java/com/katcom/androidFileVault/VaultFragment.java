package com.katcom.androidFileVault;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.katcom.androidFileVault.fileRecyclerView.FilePreviewAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private List<ProtectedFile> mFiles;
    private Uri tempPictureUir;
    private PreviewManager previewManager;

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
                        //showHelpInfo();
                        break;
                    case R.id.action_about:
                        //showEncryptTest();
                        break;
                    case R.id.action_exit:
                        endApplication();
                        break;
                }
                return false;
            }
        });

        mFileRecyclerView = view.findViewById(R.id.vault_file_recycler_view);
        //mFileRecyclerView.setLayoutManager(new LinearLayoutManager(thi));


        // Get a instance of the FileVault object
        mVault = FileManager.get(this.getContext());

        mFiles = mVault.getFiles();


        // Create a PreviewManager to load preview of files
        previewManager = new PreviewManager(getContext());

        //  Set the preview mode, by default small preview
        mPreviewMode = PreviewMode.PREVIEW_SMALL;

        // Start loading preview
        loadPreviewData();

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



        Log.i(TAG,"Create Vault View");
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
        Change the preview mode to smaller, that is, to show smaller preview image than current mode does.
        The lowest-fidelity is the one just showing the filename alone.
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

    /**
     * Update the recylerview according to the preview mode
     */
    private void updateUI() {

        switch (mPreviewMode) {
            case PreviewMode.FILE_DETAIL:
                showFileDetail(mFiles);
                break;
            case PreviewMode.PREVIEW_SMALL:
                showSmallPreview(mFiles);
                break;
            case PreviewMode.PREVIEW_MEDIUM:
                showMediumPreview(mFiles);
                break;
            case PreviewMode.PREVIEW_BIG:
                showBigPreview(mFiles);
                break;
        }
        currentModeIndex = modes.indexOf(mPreviewMode);
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

    /**
     * Show a new activity to allow user to take photo.
     * After the photo is taken, it would be stored in the vault
     */
    public void takePhoto() {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (imageTakeIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            /*File photoFile = null;
            try{
                photoFile = createImageFile();

            } catch (IOException e) {
                Log.e(TAG,e.toString());
            }*/

                ContentValues values = new ContentValues ();

                values.put (MediaStore.Images.Media.IS_PRIVATE, 1);
                values.put (MediaStore.Images.Media.TITLE,new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) );
                values.put (MediaStore.Images.Media.DESCRIPTION, "Secret File");

                Uri picUri = getActivity ().getContentResolver ().insert (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                saveTempPictureUri(picUri);

                imageTakeIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                //Uri photoURI = Uri.fromFile(photoFile);
                //imageTakeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void saveTempPictureUri(Uri uri) {
        this.tempPictureUir = uri;
    }

    /**
     * This methods loads preview image of each file on the background;
     */
    private void loadPreviewData(){

        Executor executor = Executors.newFixedThreadPool(30);

        for(ProtectedFile file:mFiles){
            Log.v(TAG,"LOADING PREVIEW"+file.getFilename());
            //Bitmap preview = previewManager.getPreview(file,120,120);
            //file.setPreview(preview);
            new FetchSinglePreviewImage().executeOnExecutor(executor,file);

            /*if(file.getPreview() != null){
                Log.v(TAG,"Done Loading preview on background for : "+file.getFilename());
            }else{
                Log.v(TAG,"Failed Loading preview on background for : "+file.getFilename());
            }*/
        };
    }

    /**
     * This method returns a Executor that provides 15 threads to do things on the background.
     * @return
     */
    private ExecutorService getCacheExecutor() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                120L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    /**
     * Create an menu on the vault fragment
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.export_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * The events that happens when an item on the menu is clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_export_selected_files:
                //exportMultipleFiles();
                break;
            case R.id.menu_take_photo:
                takePhoto();
                break;
        }
        return true;
    }

    /**
     * This method allows user to select multiple files
     */
    private void exportMultipleFiles() {
        Intent i = new Intent(getActivity(), ExportActivity.class);
        startActivity(i);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) return;

        if(requestCode == REQUEST_FILE_CODE && data != null){
            readSelectedFilesAndImport(data);
        }
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            fetchPictureAndImport(data);
        }
    }

    /**
     * Get the picture user has taken, and import it to the vault
     * @param data
     */
    private void fetchPictureAndImport(@Nullable Intent data) {
        Uri picUri;

        if (data == null) {
            picUri = getTempPicUri();
        } else
        {
            Bundle extras = data.getExtras();
            picUri = (Uri) extras.get (MediaStore.EXTRA_OUTPUT);
        }

        //Bundle extras = data.getExtras();
        //Bitmap image = (Bitmap)extras.get("data");
        //Uri picUri = (Uri) extras.get(MediaStore.EXTRA_OUTPUT);

        try {
            //saveImage(image);
            InputStream in = getActivity().getContentResolver().openInputStream(picUri);
            File photoFile = createImageFile();
            //mVault.importFile(photoFile,in);
            new ImportTask(photoFile,in).execute();

            //updateUI();
        } catch (IOException e) {
            Log.e(TAG,e.toString());
        }

        // Delete the photo from external storage
        int delete = getActivity().getContentResolver().delete(picUri,null,null);


    }

    private Uri getTempPicUri() {
        return this.tempPictureUir;
    }

    /**
     * Fetch the URIs that point to the files user selected and import them to the vault.
     * @param data
     */
    private void readSelectedFilesAndImport(@NonNull Intent data) {
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

    /**
     * Import a file from the give URI into the vault, and encrypt it.
     * @param uri
     */
    public void importAndEncryptFile(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor.moveToFirst()) {
            String filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            Log.v(TAG,filename);
            try {
                // Specify where the file should go in the vault`
                String targetPath = getContext().getFilesDir() +"/" + mVault.getVaultDirectory() + "/" + filename;

                File targetFile = new File(targetPath);
                // Import it to the vault at the specified location
                //mVault.importFile(targetFile,contentResolver.openInputStream(uri));
                Executor executor = Executors.newSingleThreadExecutor();
                new ImportTask(targetFile,contentResolver.openInputStream(uri)).executeOnExecutor(executor);
                // Show a message to inform user about the file
                Toast.makeText(getContext(),"Select file: "+filename,Toast.LENGTH_LONG).show();

                //get last file
                //ProtectedFile file = mFiles.get(mFiles.size()-1);

                //updateUI();
                //new FetchSinglePreviewImage().execute(file);
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

    // Send a broadcast to close all activities
    private void endApplication(){
        getContext().sendBroadcast(new Intent(CloseReceiver.CLOSE_INTENT));
    }

    private class FetchPreviewImage extends AsyncTask<Integer,Void,Void>{

        @Override
        protected Void doInBackground(Integer... integers) {
            int size = integers[0];
            for(ProtectedFile file:mFiles){
                Bitmap preview = mVault.getPreview(file,120,120);
                file.setPreview(preview);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateUI();
        }
    }

    private class FetchSinglePreviewImage extends AsyncTask<ProtectedFile,Void,ProtectedFile>{

        @Override
        protected ProtectedFile doInBackground(ProtectedFile... protectedFiles) {
            ProtectedFile file = protectedFiles[0];
            //Bitmap preview = mVault.getPreview(file,120,120);
            Log.v(TAG,"Loading preview on background for : "+file.getFilename());
            Bitmap preview = previewManager.getPreview(file,120,120);
            file.setPreview(preview);
            return file;
        }

        @Override
        protected void onPostExecute(ProtectedFile file) {
            if(file.getPreview() != null){
                Log.v(TAG,"Done Loading preview on background for : "+file.getFilename());
            }else{
                Log.v(TAG,"Failed Loading preview on background for : "+file.getFilename());
            }
            updateUI();
        }
    }

    private class ImportTask extends AsyncTask<Void,Void,Void>{
        File file;
        InputStream in;
        public ImportTask(File file,InputStream in){
            this.file = file;
            this.in = in;
        }
        @Override
        protected Void doInBackground(Void... mVoid) {
            try {
                mVault.importFile(file,in);
            } catch (FileNotFoundException | UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException e) {
                Log.e(TAG,e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateUI();
            ProtectedFile protectedFile = mFiles.get(mFiles.size()-1);
            //Bitmap preview = mVault.getPreview(file,120,120);

            new FetchSinglePreviewImage().execute(protectedFile);
        }
    }

    /**
     * This method creates a jpg file, with current datetime as its file name
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(getVaultDirectory());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }




}


