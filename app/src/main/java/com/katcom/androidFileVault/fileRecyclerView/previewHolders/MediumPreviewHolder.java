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

public class MediumPreviewHolder extends ItemViewHolder<ProtectedFile> {
    public ImageView mImageView;

    public MediumPreviewHolder(@NonNull View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.file_preview_medium_imageView);

    }

    @Override
    public void bindViewData(final ProtectedFile file, final Context context) {
        mImageView.setImageBitmap(FileManager.get(context).getPreview(file,120,120)); // Bind the picture to the view
        mImageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SecureFileOpener opener = SecureFileOpener.getInstance(context);
                opener.openBasedOnFileType(file);
            }
        });
    }
}
