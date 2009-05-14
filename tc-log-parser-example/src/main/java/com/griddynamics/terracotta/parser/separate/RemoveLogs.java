package com.griddynamics.terracotta.parser.separate;

import commonj.work.Work;

import java.io.File;

import com.griddynamics.terracotta.util.FileUtil;

/**
 * @author agorbunov @ 08.05.2009 16:22:35
 */
public class RemoveLogs implements Work {    
    private File localDir;

    public static Work from(String localDir) {
        return new LocalWork(RemoveLogs.class, localDir);
    }

    /* Instead of this constructor, call RemoveLogs.from(localDir).
     * The constructor is private, but marked as public to suit Terracotta. */
    @Deprecated
    public RemoveLogs(String localDir) {
        this.localDir = new File(localDir);
    }

    public void run() {
        FileUtil.deleteDirectoryContent(localDir);
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}
