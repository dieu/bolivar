package com.griddynamics.terracotta.util;

import java.io.File;

/**
 * @author agorbunov @ 08.05.2009 15:22:29
 */
public class FileUtil {

    public static void verifyDirExists(String dir) {
        verifyDirExists(new File(dir));
    }

    public static void verifyDirExists(File dir) {
        vefiryExists(dir);
        verifyIsDirectory(dir);
    }

    private static void vefiryExists(File f) {
        if (!f.exists())
            throw new RuntimeException("File " + f.getPath() + " doesn't exist");
    }

    private static void verifyIsDirectory(File f) {
        if (!f.isDirectory())
            throw new RuntimeException(f.getPath() + " is not directory");
    }

    public static void createDirIfNotExists(String dir) {
        createDirectoryIfNotExists(new File(dir));
    }

    public static void createDirectoryIfNotExists(File dir) {
        if (dir.exists())
            return;
        boolean created = dir.mkdirs();
        if (!created)
            throw new IllegalStateException("Could not create directory " + dir);
    }

    public static void deleteDirectoryContent(File dir) {
        if (!dir.exists())
            return;
        if (!dir.isDirectory())
            return;
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory())
                deleteDirectoryContent(f);
            else
                deleteFile(f);
        }
    }

    public static void deleteFile(File f) {
        if (!f.exists())
            return;
        boolean deleted = f.delete();
        if (!deleted)
            throw new IllegalStateException("Could not delete file " + f);
    }
}
