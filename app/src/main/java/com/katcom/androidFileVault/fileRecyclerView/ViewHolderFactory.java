package com.katcom.androidFileVault.fileRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katcom.androidFileVault.PreviewMode;
import com.katcom.androidFileVault.R;
import com.katcom.androidFileVault.fileRecyclerView.previewHolders.BigPreviewHolder;
import com.katcom.androidFileVault.fileRecyclerView.previewHolders.DetailPreviewHolder;
import com.katcom.androidFileVault.fileRecyclerView.previewHolders.MediumPreviewHolder;
import com.katcom.androidFileVault.fileRecyclerView.previewHolders.SmallPreviewHolder;

public class ViewHolderFactory {
    public static final int BIG_PREVIEW_LAYOUT = R.layout.item_file_preview_big;
    public static final int MEDIUM_PREVIEW_LAYOUT = R.layout.item_file_preview_medium;
    public static final int SMALL_PREVIEW_LAYOUT = R.layout.item_file_preview_small;
    public static final int DETAIL_PREVIEW_LAYOUT = R.layout.item_file_preview_filename;

     public ItemViewHolder createViewHolder(String viewType, ViewGroup parent, Context context){
         View view;
         switch(viewType){
            case PreviewMode.PREVIEW_BIG:
                 view =  LayoutInflater.from(context).inflate(BIG_PREVIEW_LAYOUT,parent,false);
                return new BigPreviewHolder(view);
            case PreviewMode.PREVIEW_MEDIUM:
                 view =  LayoutInflater.from(context).inflate(MEDIUM_PREVIEW_LAYOUT,parent,false);
                return new MediumPreviewHolder(view);
            case PreviewMode.PREVIEW_SMALL:
                view =  LayoutInflater.from(context).inflate(SMALL_PREVIEW_LAYOUT,parent,false);
                return new SmallPreviewHolder(view);
            case PreviewMode.FILE_DETAIL:
                view =  LayoutInflater.from(context).inflate(DETAIL_PREVIEW_LAYOUT,parent,false);
                return new DetailPreviewHolder(view);
            default:
                return null;
        }
     }

}
