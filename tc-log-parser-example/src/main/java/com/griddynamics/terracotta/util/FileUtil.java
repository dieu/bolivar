package com.griddynamics.terracotta.util;

import java.io.File;

/**
 * @author agorbunov @ 08.05.2009 15:22:29
 */
public class FileUtil {

    public static void verifyDirExists(String d) {
        verifyDirExists(new File(d));
    }

    public static void verifyDirExists(File d) {
        verifyFileExists(d);
        verifyIsDir(d);
    }

    private static void verifyFileExists(File f) {
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
        if (d.exists()) {
            verifyIsDir(d);
            return;
        }
        boolean created = d.mkdirs();
        if (!created)
            throw new IllegalStateException("Could not create directory " + d);
    }

    public static void deleteDirContent(File d) {
        if (!d.exists())
            return;
        if (!d.isDirectory())
            return;
        File[] children = d.listFiles();
        for (File c : children) {
            if (c.isDirectory())
                deleteDirContent(c);
            else
                deleteFile(c);
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
