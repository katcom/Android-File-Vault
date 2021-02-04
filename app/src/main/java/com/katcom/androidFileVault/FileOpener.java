package com.katcom.androidFileVault;

public interface FileOpener {
    void openFile(ProtectedFile file);

    void openPdf(ProtectedFile file);

    void openDocx(ProtectedFile file);

    void openTxt(ProtectedFile file);

    void openVideo(ProtectedFile file);

    void openAudio(ProtectedFile file);

    void openPicture(ProtectedFile file);
    
}
