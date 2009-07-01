package com.griddynamics.terracotta.worker.factory;

import com.griddynamics.terracotta.helpers.CountdownLatch;
import com.griddynamics.terracotta.helpers.ParseLogs;
import com.griddynamics.terracotta.helpers.Pipe;
import com.griddynamics.terracotta.helpers.util.NetUtil;
import static com.griddynamics.terracotta.scheduler.Scheduler.pipes;
import static com.griddynamics.terracotta.scheduler.Scheduler.workerDir;
import org.apache.log4j.Logger;
import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;

import java.util.Map;

/**
 * @author apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 15:33:19
 */
public class Parsing implements Runnable {
    private static Logger logger = Logger.getLogger(Parsing.class);

    public void run() {
        runParsing();
    }

    private void runParsing() {
        try {
            Pipe<ConcurrentStringMap<Long>> pipe = new Pipe<ConcurrentStringMap<Long>>(new ConcurrentStringMap<Long>());
            pipes.putIfAbsent(NetUtil.ip(), pipe);
            synchronized (pipe) {
                pipe.wait();
            }
            logger.info("<par>" + NetUtil.host() + "</par>");
            logger.info("Parsing...");
            long startParsing = System.currentTimeMillis();
            ParseLogs parseLogs = new ParseLogs(workerDir);
            parseLogs.run();
            long endParsing = System.currentTimeMillis() - startParsing;
            synchronized (pipe) {
                pipes.get(NetUtil.ip()).setPipes(parseLogs.getTrafficMap());
                pipe.setTime(endParsing);
                pipe.notify();
            }
            logger.info("<fin>" + NetUtil.host() + "</fin>");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
