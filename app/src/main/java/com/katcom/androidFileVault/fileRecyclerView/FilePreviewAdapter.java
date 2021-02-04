package com.katcom.androidFileVault.fileRecyclerView;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.katcom.androidFileVault.ProtectedFile;

import java.util.List;

public class FilePreviewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private ViewHolderFactory mHolderFactory;
    private List<ProtectedFile> mFiles;
    private String mPreviewMode;
    private Context mContext;
    public FilePreviewAdapter(List<ProtectedFile> files, String previewMode, Context context){
        mFiles = files;
        mPreviewMode = previewMode;
        mHolderFactory = new ViewHolderFactory();
        mContext = context;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mHolderFactory.createViewHolder(mPreviewMode,parent,mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindViewData(mFiles.get(position),mContext);
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }
}
