package com.katcom.androidFileVault.fileRecyclerView;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ItemViewHolder<T> extends RecyclerView.ViewHolder{
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindViewData(T data, Context context);
}
