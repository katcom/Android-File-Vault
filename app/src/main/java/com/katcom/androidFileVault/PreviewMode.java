package com.katcom.androidFileVault;

import java.util.ArrayList;
import java.util.List;

public class PreviewMode {
    final public static String FILE_DETAIL = "File Detail";
    final public static String PREVIEW_SMALL = "Preview Small";
    final public static String PREVIEW_MEDIUM = "Preview Medium";
    final public static String PREVIEW_BIG = "Preview Big";
    private static List<String> modes;

    private PreviewMode(){}

    static {
         modes = new ArrayList<>();
         modes.add(FILE_DETAIL);
         modes.add(PREVIEW_SMALL);
    }

    public static List<String> getModeList(){
        return modes;
    }
    public static int getModeCount(){
        return modes.size();
    }

}
