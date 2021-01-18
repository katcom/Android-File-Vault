package com.katcom.androidFileVault;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describe different modes of preview. All modes are pre-defined
 * It shouldn't be instantiated.
 * The static variables describe the supported modes.
 * Other class can get the list of all supported mode by calling getModeList() method.
 */
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
         modes.add(PREVIEW_MEDIUM);
         modes.add(PREVIEW_BIG);
    }

    /**
     *
     * @return The list of all modes.
     */
    public static List<String> getModeList(){
        return modes;
    }

    /**
     *
     * @return The number of the supported modes
     */
    public static int getModeCount(){
        return modes.size();
    }

}
