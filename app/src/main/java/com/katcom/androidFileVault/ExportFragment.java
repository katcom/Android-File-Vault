package com.katcom.androidFileVault;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExportFragment  extends Fragment {
    private RecyclerView currentLocationRecyclerView;
    private RecyclerView directoryRecyclerView;
    private RecyclerView.Adapter currentAdapter;
    private RecyclerView.Adapter directoryAdapter;
    private Button previousButton;
    private static String TAG = "ExportFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_export,container,false);
        currentLocationRecyclerView = v.findViewById(R.id.export_current_location_recycler_view);
        directoryRecyclerView = v.findViewById(R.id.export_directory_recycler_view);
        previousButton = v.findViewById(R.id.export_previous_button);

        updateUI();
        return v;
    }

    private void updateUI() {
        if(requestExternalStoragePermission(getContext())){
            List<File> files = getExternalFiles();
            for(File file : files){
                Log.v(TAG,"FILE:" + file.getName());
            }
        }else{
            Log.e(TAG,"no permission");
        }


    }

    private List<File> getExternalFiles() {
        List<File> files = new ArrayList<>();

        Cursor cursor =null;
        ContentResolver resolver;

            resolver =  getContext().getContentResolver();
            cursor = resolver.query(MediaStore.Files.getContentUri("external"), null, null, null, null);
            int columnIndexOrThrow_ID = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int columnIndexOrThrow_MIME_TYPE = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
            while(cursor.moveToNext()){
                String path = cursor.getString(columnIndexOrThrow_ID);
                File file = new File(path);
                files.add(file);
            }

            cursor.close();
        return files;
    }

    private class FileHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        public FileHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.item_export_folder);
        }
    }

    private class FileAdapter extends RecyclerView.Adapter<FileHolder>{
        private List<File> mFiles;
        public FileAdapter(List<File> files){
            mFiles = files;
        }

        @NonNull
        @Override
        public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_export_folder,parent, false);

            return new FileHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull FileHolder holder, int position) {
           File file = mFiles.get(position);
           holder.mTextView.setText(file.getName());
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }

    private boolean requestExternalStoragePermission(@NonNull Context context){
        if(!hasExternalStoragePermission()){
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 100);
        }

        Log.v(TAG,"IS EXTERNAL MANAGER");
        return true;

    }

    private boolean hasExternalStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }else{
            return false;
        }
    }
}
