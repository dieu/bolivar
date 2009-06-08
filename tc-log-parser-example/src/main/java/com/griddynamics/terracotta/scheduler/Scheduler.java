package com.griddynamics.terracotta.scheduler;

import com.griddynamics.terracotta.parser.Aggregator;
import com.griddynamics.terracotta.parser.separate.AveragePerformance;
import com.griddynamics.terracotta.parser.separate.DownloadLog;
import com.griddynamics.terracotta.parser.separate.ParseLogs;
import com.griddynamics.terracotta.parser.separate.RemoveLogs;
import static com.griddynamics.terracotta.scheduler.Phase.*;
import static com.griddynamics.terracotta.scheduler.Workers.ForEachLog;
import static com.griddynamics.terracotta.scheduler.Workers.ForEachWorker;
import static com.griddynamics.terracotta.util.StrUtil.encloseWithTag;
import commonj.work.Work;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertNotNull;

/**
 * @author agorbunov @ 07.05.2009 18:12:31
 */
public class Scheduler {
    private static Logger logger = Logger.getLogger(Scheduler.class);
    private TimeMeter timeMeter = new TimeMeter();
    private String masterUrl;
    private String workerDir;
    private Workers workers;
    private Aggregator aggregator;

    public Scheduler(String masterDir, String masterUrl, String workerDir, String countWorkers) {
        this.workers = new Workers(masterDir, timeMeter, Integer.parseInt(countWorkers));
        this.masterUrl = masterUrl;
        this.workerDir = workerDir;
        assertNotNull(masterDir);
        assertNotNull(masterUrl);
        assertNotNull(workerDir);
        assertNotNull(countWorkers);
    }

    public void findMaxTrafficWithSeveralWorkers() {
        connectToWorkers();
        findMaxTraffic();
        reportParsingPerformance();
    }

    private void connectToWorkers() {
        workers.init();
    }

    private void findMaxTraffic() {
        findTrafficOfEachIp();
        findMostConsumingIp();
    }

    private void findTrafficOfEachIp() {
        fetch();
        parse();
    }

    private void fetch() {
        remove();
        download();
    }

    private void remove() {
        // TODO Schedule the task according to the number of workers, rather than the number of logs
        workers.perform(new ForEachLog() {
            public Work work(String log) {
                return RemoveLogs.from(workerDir);
            }
            public Phase phase() {
                return REMOVING;
            }
        });
    }

    private void download() {
        workers.perform(new ForEachLog() {
            public Work work(String log) {
                return DownloadLog.fromTo(url(log), workerDir);
            }
            public Phase phase() {
                return DOWNLOADING;
            }
        });
    }

    private String url(String log) {
        return masterUrl + log;
    }

    private void parse() {
        aggregator = new Aggregator();
        // TODO Schedule the task according to the number of workers, rather than the number of logs
        workers.perform(new ForEachWorker() {
            public Work work(/*String log*/) {
                return ParseLogs.fromTo(workerDir, aggregator);
            }
            public Phase phase() {
                return PARSING;
            }
        });
    }

    private void findMostConsumingIp() {
        timeMeter.start(AGGREGATING);
        String ip = aggregator.ipWithMaxTraffic();
        logger.info("Ip <ip>" + ip + "</ip> has maximum traffic: <traf>" + aggregator.traffic(ip) + "</traf> ");
        timeMeter.stop();
    }

    private void reportParsingPerformance() {
        AveragePerformance ap = aggregator.averagePerformance();
        logger.info("Parsing performance:");
        logger.info("Parsed: " + encloseWithTag(ap.parsed(), "op"));
        logger.info("Parsed min: " + ap.parsedMin());
        logger.info("Parsed max: " + ap.parsedMax());
        logger.info("Parsed one: " + ap.parsedOne());
        logger.info("Returned: " + encloseWithTag(ap.returned(), "or"));
        logger.info("Returned min: " + ap.returnedMin());
        logger.info("Returned max: " + ap.returnedMax());
        logger.info("Logs: " + ap.logs());
    }
}
