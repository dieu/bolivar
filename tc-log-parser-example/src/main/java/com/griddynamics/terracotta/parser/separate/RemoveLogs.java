package com.griddynamics.terracotta.parser.separate;

import com.griddynamics.terracotta.util.FileUtil;
import commonj.work.Work;

import java.io.File;

/**
 * @author agorbunov @ 08.05.2009 16:22:35
 */
public class RemoveLogs implements Work {    
    private Tracker phase = new Tracker();
    private File dir;

    public static Work from(String dir) {
        return new Trackable(RemoveLogs.class, dir);
    }

    /* Instead of this constructor, call RemoveLogs.from(dir).
     * The constructor is private, but marked as public to suit Terracotta. */
    @Deprecated
    public RemoveLogs(String dir) {
        this.dir = new File(dir);
    }

    public void run() {
        phase.phase(Phase.REMOVING);
        FileUtil.deleteDirContent(dir);
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {
        // Do nothing.
    }
}
