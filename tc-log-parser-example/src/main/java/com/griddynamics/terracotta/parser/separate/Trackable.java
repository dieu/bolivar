package com.griddynamics.terracotta.parser.separate;

import static com.griddynamics.terracotta.parser.separate.Tracker.Phase.ERROR;
import com.griddynamics.terracotta.parser.wrapper.Wrapper;
import static com.griddynamics.terracotta.util.ErrUtil.runtimeException;
import commonj.work.Work;

/**
 * Allows the user to track whether a work has failed or not.
 *
 * @author agorbunov @ 26.05.2009 20:49:59
 */
public class Trackable extends Wrapper {
    public Trackable(Class<? extends Work> work, Object... arguments) {
        super(work, arguments);
    }

    @Override
    protected void run(Work work) {
        try {
            work.run();
        } catch (Throwable e) {
            reportFailure();
            rethrow(e);
        }
    }

    protected void reportFailure() {
        // A local variable instead of a field to prevent UnlockedSharedObjectException.
        Tracker tracker = new Tracker();
        tracker.phase(ERROR);
    }

    private void rethrow(Throwable e) {
        throw runtimeException(e);
    }
}
