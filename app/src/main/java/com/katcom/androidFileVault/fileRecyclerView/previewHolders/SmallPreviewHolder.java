package com.katcom.androidFileVault.fileRecyclerView.previewHolders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.katcom.androidFileVault.FileVault;
import com.katcom.androidFileVault.ProtectedFile;
import com.katcom.androidFileVault.R;
import com.katcom.androidFileVault.SecureFileOpener;
import com.katcom.androidFileVault.fileRecyclerView.ItemViewHolder;

public class SmallPreviewHolder extends ItemViewHolder<ProtectedFile> {
    public TextView mFileTextView;
    public ImageView mImageView;

    public SmallPreviewHolder(@NonNull View itemView) {
        super(itemView);
        mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_filename_text_view);
        mImageView = (ImageView) itemView.findViewById(R.id.file_preview_small_image_view);
    }

    @Override
    public void bindViewData(final ProtectedFile file, final Context context) {
        mFileTextView.setText(file.getFilename());   // Bind the file name to the user interface
        mImageView.setImageBitmap(FileVault.get(context).getPreview(file,60,60)); // Bind the preview image to the user interface
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecureFileOpener opener = SecureFileOpener.getInstance(context);
                opener.openBasedOnFileType(file);
            }
        });
    }

}
