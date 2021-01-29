package com.katcom.androidFileVault;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageViewerFragment extends Fragment {
    private static final String ARG_FILE_PATH = "filepath";
    private static final String ARG_FILE_NAME = "filename";

    private ImageView mImageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_image_viewer,container,false);
        String imgPath = getImagePath();
        String imgName = getImageName();
        mImageView = view.findViewById(R.id.fragment_image_viwer_image_view); // Bind the controller to the image view in the layout
        mImageView.setImageBitmap(Utils.getScaledBitmap(getContext(),imgPath,700,700));
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        // Create the AlertDialog object and return it
        return view;
    }

    private String getImagePath() {
        return (String) getArguments().getSerializable(ARG_FILE_PATH);
    }

    private String getImageName(){
        return (String) getArguments().getSerializable(ARG_FILE_NAME);
    }


    public static ImageViewerFragment newInstance(String filename,String filepath){
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE_PATH,filepath);
        args.putSerializable(ARG_FILE_NAME,filename);

        ImageViewerFragment fragment = new ImageViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
