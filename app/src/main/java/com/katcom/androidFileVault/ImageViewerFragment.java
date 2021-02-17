package com.katcom.androidFileVault;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

public class ImageViewerFragment extends Fragment {
    private static final String ARG_FILE = "file";
    private static final String TAG = "ImageViewer";

    private ImageView mImageView;
    private ProtectedFile mFile;
    private int WRITE_REQUEST_CODE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        setHasOptionsMenu(true);

        mFile = (ProtectedFile) getArguments().getParcelable(ARG_FILE);

        //String imgPath = getImagePath();
        //String imgName = getImageName();
        String imgPath = mFile.getFilepath();
        String imgName = mFile.getFilename();

        mImageView = view.findViewById(R.id.fragment_image_viwer_image_view); // Bind the controller to the image view in the layout

        Bitmap img = getImage(mFile);
        mImageView.setImageBitmap(FileManager.get(getContext()).getPreview(mFile,700,700));

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        // Create the AlertDialog object and return it
        return view;
    }

    private Bitmap getImage(ProtectedFile mFile) {
        return null;
    }

    /*/private String getImagePath() {
        return (String) getArguments().getSerializable(ARG_FILE_PATH);
    }*/

    /*
    private String getImageName(){
        return (String) getArguments().getSerializable(ARG_FILE_NAME);
    }
    */

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
        FileManager vault = FileManager.get(getContext());
        try {
            vault.exportFile(mFile,getContext().getContentResolver().openOutputStream(uri));
        } catch (FileNotFoundException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableEntryException e) {
            Log.e(TAG,e.toString());
        }

    }
}
