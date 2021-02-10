package com.katcom.androidFileVault.fileRecyclerView.previewHolders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.katcom.androidFileVault.FileManager;
import com.katcom.androidFileVault.ProtectedFile;
import com.katcom.androidFileVault.R;
import com.katcom.androidFileVault.SecureFileOpener;
import com.katcom.androidFileVault.fileRecyclerView.ItemViewHolder;

public class BigPreviewHolder extends ItemViewHolder<ProtectedFile> {
    //public TextView mFileTextView;
    public ImageView mImageView;
    public BigPreviewHolder(@NonNull View itemView) {
        super(itemView);
        //mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_medium_imageView);
        mImageView = itemView.findViewById(R.id.file_preview_big_image_view);

    }

    @Override
    public void bindViewData(final ProtectedFile file, final Context context) {
        //fileHolder.setText(file.getFilename());

        mImageView.setImageBitmap(FileManager.get(context).getPreview(file,190,190)); // Bind the picture to the view
        mImageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SecureFileOpener opener = SecureFileOpener.getInstance(context);
                opener.openBasedOnFileType(file);
            }
        });
    }
}
