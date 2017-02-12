package com.seki.saezurishiki.file;

import android.content.Context;

import org.jetbrains.annotations.Contract;

import java.io.File;

public class CachManager {

    public static void deleteCache(Context context) {
        final File cacheFile = context.getCacheDir();

        if (cacheFile != null && cacheFile.isDirectory()) {
            deleteFile(cacheFile);
        }
    }


    @Contract("null -> fail")
    private static boolean deleteFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is null!");
        }

        if (!file.isDirectory()) {
            return file.delete();
        }

        File[] files = file.listFiles();
        for (File f : files) {
            deleteFile(f);
        }

        return file.delete();
    }
}
