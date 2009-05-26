package com.griddynamics.terracotta.parser.separate;

import static com.griddynamics.terracotta.parser.separate.Tracker.Phase;
import com.griddynamics.terracotta.parser.wrapper.Wrapper;
import static com.griddynamics.terracotta.util.ErrUtil.runtimeException;
import commonj.work.Work;

/**
 * In case of an error, notifies the user that the work has failed.
 *
 * @author agorbunov @ 26.05.2009 20:49:59
 */
public class Trackable extends Wrapper {
    private Tracker tracker = new Tracker();

    public Trackable(Class<? extends Work> work, Object... arguments) {
        super(work, arguments);
    }

    @Override
    protected void run(Work work) {
        try {
            work.run();
        } catch (Throwable e) {
            reportFailure(e);
            rethrow(e);
        }
    }

    protected void reportFailure(Throwable e) {
        tracker.entered(Phase.ERROR);
    }

    private void rethrow(Throwable e) {
        throw runtimeException(e);
    }
}
