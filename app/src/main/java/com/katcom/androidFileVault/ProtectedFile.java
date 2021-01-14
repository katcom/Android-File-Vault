package com.katcom.androidFileVault;

import java.util.UUID;

public class ProtectedFile {
    private String filename;
    private String filepath;

    public UUID getId() {
        return id;
    }

    private UUID id;
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

    private String type;

    public ProtectedFile(String filename,String filepath,UUID id){
        this.filename = filename;
        this.filepath = filepath;
        this.id = id;
    }

}
