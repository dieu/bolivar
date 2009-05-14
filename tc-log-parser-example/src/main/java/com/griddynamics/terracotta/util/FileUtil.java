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
        verifyIsDir(dir);
    }

    private static void vefiryExists(File f) {
        if (!f.exists())
            throw new RuntimeException("File " + f.getPath() + " doesn't exist");
    }

    private static void verifyIsDir(File f) {
        if (!f.isDirectory())
            throw new RuntimeException(f.getPath() + " is not directory");
    }

    public static void createDirIfNotExists(String d) {
        createDirIfNotExists(new File(d));
    }

    public static void createDirIfNotExists(File d) {
        if (d.exists())
            return;
        boolean created = d.mkdirs();
        if (!created)
            throw new IllegalStateException("Could not create directory " + d);
    }

    public static void deleteDirContent(File dir) {
        if (!dir.exists())
            return;
        if (!dir.isDirectory())
            return;
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory())
                deleteDirContent(f);
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
