package com.katcom.androidFileVault;

import java.util.UUID;

public class ProtectedFile {
    private String filename;
    private String filepath;
    private String type;
    private UUID id;

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



}
