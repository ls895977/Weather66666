package com.naran.ui.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by darhandarhad on 2017/11/23.
 */

public class StringUtil {
    public static boolean isEmpty(String str) {

        if (null == str || "".equals(str) || "null".equals(str)) {
            return true;
        }
        return false;
    }

    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
          inputFile.close();
         return Base64.encodeToString(buffer, Base64.DEFAULT);
    }
    public static String voic2Base64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }
}
