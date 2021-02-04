package com.katcom.androidFileVault.fileRecyclerView.previewHolders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.katcom.androidFileVault.ProtectedFile;
import com.katcom.androidFileVault.R;
import com.katcom.androidFileVault.SecureFileOpener;
import com.katcom.androidFileVault.fileRecyclerView.ItemViewHolder;

public class DetailPreviewHolder extends ItemViewHolder<ProtectedFile> {
    private  TextView mFileTextView;
    public DetailPreviewHolder(@NonNull View itemView) {
        super(itemView);
        mFileTextView = (TextView) itemView.findViewById(R.id.file_preview_filename_name_text_view);

    }

    @Override
    public void bindViewData(final ProtectedFile file, final Context context) {
        mFileTextView.setText(file.getFilename()); // Bind the filename to the individual cell in the recycler view
        mFileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecureFileOpener opener = SecureFileOpener.getInstance(context);
                opener.openBasedOnFileType(file);
            }
        });

    }
}
