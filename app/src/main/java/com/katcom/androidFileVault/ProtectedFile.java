package com.katcom.androidFileVault;
/**
 * This class represents an encrypted file saved in the vault.
 * The information about the file includes its name, path, type, an unique id and a preview picture saved as a bitmap
 */

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.UUID;

public class ProtectedFile implements Parcelable {
    private String filename;
    private String filepath;
    private String type;
    private UUID id;

    private Bitmap preview;
    public ProtectedFile(String filename,String filepath,UUID id){
        this.filename = filename;
        this.filepath = filepath;
        this.id = id;
    }



    ////////////////////////// Getter and Setter/////////////////////////////////////
    public UUID getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public void setPreview(Bitmap preview) {
        this.preview=preview;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filename);
        dest.writeString(filepath);
        dest.writeString(type);
        //dest.writeParcelable(preview, flags);

    }

    protected ProtectedFile(Parcel in) {
        filename = in.readString();
        filepath = in.readString();
        type = in.readString();
        //preview = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<ProtectedFile> CREATOR = new Creator<ProtectedFile>() {
        @Override
        public ProtectedFile createFromParcel(Parcel in) {
            return new ProtectedFile(in);
        }

        @Override
        public ProtectedFile[] newArray(int size) {
            return new ProtectedFile[size];
        }
    };
}
