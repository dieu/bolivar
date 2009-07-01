package com.griddynamics.terracotta.scheduler;

import com.griddynamics.terracotta.helpers.Aggregator;
import com.griddynamics.terracotta.helpers.CountdownLatch;
import com.griddynamics.terracotta.helpers.Pipe;
import com.griddynamics.terracotta.helpers.TaskDownloading;
import com.griddynamics.terracotta.helpers.util.FileUtil;
import com.griddynamics.terracotta.helpers.WaitPipe;
import org.apache.log4j.Logger;
import org.terracotta.modules.concurrent.collections.ConcurrentStringMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author apanasenko aka dieu
 * Date: 04.06.2009
 * Time: 13:20:21
 */
public class Scheduler {
    private long endParsing;
    private long returning;
    public static BlockingQueue<TaskDownloading> queue = new LinkedBlockingQueue<TaskDownloading>();
    public static ConcurrentStringMap<Pipe<ConcurrentStringMap<Long>>> pipes = new ConcurrentStringMap<Pipe<ConcurrentStringMap<Long>>>();
    public static CountdownLatch cdl = new CountdownLatch();
    public static String workerDir;
    private String masterDir;
    private String masterUrl;
    private int workerCount;
    private Aggregator aggregator;
    private static Logger logger = Logger.getLogger(Scheduler.class);

    public Scheduler(String workerCount, String masterDir, String masterUrl, String workerDir) {
        this.workerCount = new Integer(workerCount);
        this.masterDir = masterDir;
        Scheduler.workerDir = workerDir;
        this.masterUrl = masterUrl;
        this.aggregator = new Aggregator();
        Scheduler.queue = new LinkedBlockingQueue<TaskDownloading>();
        Scheduler.pipes = new ConcurrentStringMap<Pipe<ConcurrentStringMap<Long>>>();
        Scheduler.cdl = new CountdownLatch();
    }

    public void run() throws InterruptedException {
        dowload();
        parse();
        report();
    }

    private void dowload() throws InterruptedException {
        logger.info("Downloading...");
        Long startedDownloading = System.currentTimeMillis();
        String[] logs = logs();
        cdl.reset(logs.length);
        for(String log: logs) {
            TaskDownloading task = new TaskDownloading(fileToUrl(log));
            queue.put(task);
        }
        cdl.await();
        logger.info("Downloaded in " + (System.currentTimeMillis() - startedDownloading));
    }

    private void parse() throws InterruptedException {
        logger.info("Parsing...");
        long startedParsing = System.currentTimeMillis();
        for(Pipe<ConcurrentStringMap<Long>> pipe: pipes.values()) {
            synchronized (pipe) {
                pipe.notify();
            }
        }
        List<Thread> threads = new ArrayList<Thread>();
        for(Pipe<ConcurrentStringMap<Long>> pipe: pipes.values()) {
            Thread thread = new Thread(new WaitPipe(pipe));
            thread.start();
            threads.add(thread);
        }
        boolean flagWait = true;
        while(flagWait){
            for(Iterator<Thread> iterator = threads.iterator(); iterator.hasNext();) {
                Thread thread = iterator.next();
                if(!thread.isAlive()) {
                    iterator.remove();
                    logger.info("Size: " + threads.size());
                }
            }
            flagWait = !threads.isEmpty();
        }
        returning = System.currentTimeMillis();
        for(String hostWorker: pipes.keySet()) {
            Pipe<ConcurrentStringMap<Long>> pipe = pipes.get(hostWorker);
            aggregator.add(hostWorker, pipe.getPipe(), pipe.getTime());
        }
        returning = System.currentTimeMillis() - returning;
        endParsing = System.currentTimeMillis() - startedParsing;
        logger.info("Parsed in " + endParsing);
    }

    private String[] logs() {
        File logs = new File(masterDir);
        FileUtil.verifyDirExists(logs);
        return logs.list();
    }

    private String fileToUrl(String file) {
        int index = file.lastIndexOf("/") < 0 ? file.lastIndexOf("\\") : file.lastIndexOf("/");
        return masterUrl + file.substring(index < 0 ? 0 : index);
    }

    private void report() {
        String ip = aggregator.ipWithMaxTraffic();
        logger.info("Ip <ip>" + ip + "</ip> has maximum traffic: <traf>" + aggregator.getTraffic() + "</traf> ");
        logger.info("Total time <to>" + endParsing + "</to>");
        logger.info("Parsing <op>" + aggregator.getAvgTimeParsing() + "</op>");
        logger.info("Returning <or>" + returning + "</or>");
    }
}
    