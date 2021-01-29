package com.katcom.androidFileVault;

public interface SecureFileOpener {
    void openFile(String filename);

    void openPdf(String filename);

    void openDocx(String filename);

    void openTxt(String filename);

    void openVideo(String filename);

    void openAudio(String filename);

    void openPicture(String filename);

}
