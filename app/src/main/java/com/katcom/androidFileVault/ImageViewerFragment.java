package com.katcom.androidFileVault;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageViewerFragment extends Fragment {
    private static final String ARG_FILE = "file";
    private static final String TAG = "ImageViewer";

    FileManager vault = FileManager.get(getContext());
    private ImageView mImageView;
    private ProtectedFile mFile;
    private int WRITE_REQUEST_CODE = 0;
    private Bitmap picture;
    private ProgressBar mProgressBar;
    private Executor executor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        setHasOptionsMenu(true);

        mFile = (ProtectedFile) getArguments().getParcelable(ARG_FILE);

        String imgPath = mFile.getFilepath();
        String imgName = mFile.getFilename();

        mImageView = view.findViewById(R.id.fragment_image_viwer_image_view); // Bind the controller to the image view in the layout
        mImageView.setVisibility(View.GONE);   // hide the image until it is loaded

        mProgressBar = view.findViewById(R.id.image_viewer_progressBar);
        mProgressBar.setVisibility(View.VISIBLE); // Show the progress bar while loading

        //mImageView.setImageBitmap(FileManager.get(getContext()).getPreview(mFile,700,700));
        executor = Executors.newFixedThreadPool(2);
        new LoadingImageTask().executeOnExecutor(executor,mFile);
        getActivity().setTitle(imgName);
        return view;
    }

    public static ImageViewerFragment newInstance(ProtectedFile file) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_FILE, file);

        ImageViewerFragment fragment = new ImageViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_image_viewer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_image_viewer_export:
                export();
                break;
            case R.id.menu_image_viewer_share:
                share();
                break;
        }
        return true;
    }

    private void export() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TITLE, mFile.getFilename());
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }
    private void share() {
        new ShareImageTask().executeOnExecutor(executor,mFile);

    }
    private void shareImage(Uri fileUri) {
        Intent shareIntent = createShareImageIntent(fileUri);

        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }
    private Intent createShareImageIntent(Uri uriToImage){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/jpeg");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return shareIntent;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                Log.v(TAG, "Uri: " + uri.toString());
                export(uri);
            }
        }
    }

    private void export(Uri uri) {
        try {
            vault.exportFile(mFile,getContext().getContentResolver().openOutputStream(uri));
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableEntryException  | IOException e) {
            Log.e(TAG,e.toString());
        }

    }

    private class LoadingImageTask extends AsyncTask<ProtectedFile,Void,Void> {
        File file;
        InputStream in;

        @Override
        protected Void doInBackground(ProtectedFile... files) {
            picture = new PreviewManager(getContext()).getPreview(mFile,700,700);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateUI();
        }
    }

    private class ShareImageTask extends AsyncTask<ProtectedFile,Void,Void> {
        File file;
        InputStream in;
        Uri fileUri;
        @Override
        protected Void doInBackground(ProtectedFile... files) {
            file = vault.createSharedFile(mFile);
            try {
                OutputStream out = new FileOutputStream(file);

                vault.exportFile(mFile,out);;
            } catch (IOException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
                Log.v(TAG,e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fileUri = FileProvider.getUriForFile(
                    getContext(),
                    BuildConfig.APPLICATION_ID+".fileprovider",
                    file);
            shareImage(fileUri);
            Log.v(TAG,"Shared File Created");
        }
    }


    private void updateUI() {
        mProgressBar.setVisibility(View.GONE);
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageBitmap(picture);
    }
}
