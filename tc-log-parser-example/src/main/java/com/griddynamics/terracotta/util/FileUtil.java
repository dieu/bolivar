package com.griddynamics.terracotta.util;

import java.io.File;

/**
 * @author agorbunov @ 08.05.2009 15:22:29
 */
public class FileUtil {

    public static void verifyDirExists(String a) {
        verifyDirExists(new File(a));
    }

    public static void verifyDirExists(File a) {
        vefiryExists(a);
        verifyIsDir(a);
    }

    private static void vefiryExists(File a) {
        if (!a.exists())
            throw new RuntimeException("File " + a.getPath() + " doesn't exist");
    }

    private static void verifyIsDir(File a) {
        if (!a.isDirectory())
            throw new RuntimeException(a.getPath() + " is not directory");
    }

    public static void createDirIfNotExists(String a) {
        createDirIfNotExists(new File(a));
    }

    public static void createDirIfNotExists(File a) {
        if (a.exists()) {
            verifyIsDir(a);
            return;
        }
        boolean created = a.mkdirs();
        if (!created)
            throw new IllegalStateException("Could not create directory " + a);
    }

    public static void deleteDirContent(File a) {
        if (!a.exists())
            return;
        if (!a.isDirectory())
            return;
        File[] children = a.listFiles();
        for (File c : children) {
            if (c.isDirectory())
                deleteDirContent(c);
            else
                deleteFile(c);
        }
    }

    public static void deleteFile(File a) {
        if (!a.exists())
            return;
        boolean deleted = a.delete();
        if (!deleted)
            throw new IllegalStateException("Could not delete file " + a);
    }
}
