package com.wakereality.fictionengines;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Stephen A. Gutknecht on 8/22/17.
 */


/*
As explained here, NDK code and Linux shell apps code can't seem to access the assets folder directly
   https://stackoverflow.com/questions/22903540/android-copy-files-from-assets-to-data-data-folder
 */
public class CopyAssetHelper {

    public static boolean copyAssetFile(AssetManager assetManager,
                                     String fromAssetFilePath, File toFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetFilePath);
            toFile.createNewFile();
            out = new FileOutputStream(toFile.getPath());
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
            return true;
        } catch(Exception e) {
            Log.e("FictionEngines", "Exception copying file from Android assets " + fromAssetFilePath, e);
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        // Often hardware caches in 4K pages
        final byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
