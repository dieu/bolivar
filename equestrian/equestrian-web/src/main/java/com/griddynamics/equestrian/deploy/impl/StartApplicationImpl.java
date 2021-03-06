package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StartApplication;
import com.griddynamics.equestrian.helpers.ApplicationPath;
import com.griddynamics.equestrian.helpers.ParserHost;
import com.griddynamics.equestrian.model.Application;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.util.*;
import java.text.MessageFormat;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 19:46:27
 */
public class StartApplicationImpl implements StartApplication<Application> {
    private String uploadCommand;
    private String runServerCommand;
    private String runWorkersCommand;
    private String runSchedulerCommand;
    private String runKillCommand;
    private String separator;
    private String outScheduler = "";
    private String outWorkers = "";
    private String infoNodes = "";
    private int nWorkers = 0;
    private int minute = 0;
    private boolean isRunScheduler = false;
    private boolean isRunKill = false;
    private Process server;
    private Process workers;
    private Process scheduler;
    private Process kill;
    private String regAll = "[a-zA-Z\\d\\s\\S]*";
    private String regTime = "\\s*<to>\\d+</to>\\s*";
    private Pattern patTime = Pattern.compile(regAll + regTime + regAll);
    private Pattern error = Pattern.compile(regAll + "WARN - Can't connect to server" + regAll);
    private ParserHost parserHost;
    private Application application;
    private Date date = null;
    private File dir;
    private FileWriter schedulerLogs;
    private FileWriter workersLogs;
    private Map<String, String> nodes;
    private ReadSchedulerOut readSchedulerOut = new ReadSchedulerOut();
    private ReadWorkersOut readWorkersOut = new ReadWorkersOut();

    public StartApplicationImpl() {
        separator = System.getProperty("file.separator");
        if(separator.equals("\\")) {
            uploadCommand  = ApplicationPath.CAPISTRANO_PATH + "cap.cmd upload_all";
            runServerCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_server";
            runWorkersCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_workers";
            runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_scheduler";
            runKillCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd kill";
            dir = new File(ApplicationPath.APPLICATION_PATH);
        } else {
            uploadCommand  = ApplicationPath.CAPISTRANO_PATH + "cap upload_all";
            runServerCommand = ApplicationPath.CAPISTRANO_PATH + "cap run_server";
            runWorkersCommand = ApplicationPath.CAPISTRANO_PATH + "cap run_workers";
            runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH + "cap run_scheduler";
            runKillCommand = ApplicationPath.CAPISTRANO_PATH + "cap kill";
            dir = new File(ApplicationPath.APPLICATION_PATH);
        }
    }

    public void setParserHost(ParserHost parserHost) {
        this.parserHost = parserHost;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void deploy(int n) {
        try {
            parserHost.parse();
            killApplication();
            parserHost.clear();
            nWorkers = parserHost.parse(n);
            parserHost.getCountNode();
            nodes = parserHost.getNodeIp();
            if(date == null) {
                date = Calendar.getInstance().getTime();
                minute = Calendar.getInstance().get(Calendar.MINUTE);
            }
            init(nWorkers, minute);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            Runtime.getRuntime().exec(runKillCommand, null, dir);
            Thread.sleep(1000L);
            server = Runtime.getRuntime().exec(runServerCommand, null, dir);
            Thread.sleep(5000L);
            workers = Runtime.getRuntime().exec(runWorkersCommand, null, dir);
            Thread.sleep(10000L);
            scheduler = Runtime.getRuntime().exec(runSchedulerCommand, null, dir);
            isRunScheduler = true;
            Thread.sleep(1000L);
            readSchedulerOut = new ReadSchedulerOut();
            readWorkersOut = new ReadWorkersOut();
            readSchedulerOut.start();
            readWorkersOut.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Application verify() {
        application = new Application();
        application.setWorkers(String.valueOf(nWorkers));
        application.setDate(date);
        application.setNodeIp(infoNodes);
        application.setSchedulerStatus(isRunScheduler);
        if(!isRunScheduler) {
            if(patTime.matcher(outScheduler).matches()) {
                String[] split = outScheduler.split(" ");
                for(String word: split) {
                    if(word.startsWith("<op>")) {
                        String time = word.replace("<op>","")
                                .replace("</op>","").replace("\r","").replace("\n", "");
                        time = String.format(Locale.US, "%.2f", (Long.parseLong(time) / 1000.0f));
                        application.setParsing(time);
                    }
                    if(word.startsWith("<or>")) {
                        application.setReturning(word.replace("<or>","")
                                .replace("</or>","").replace("\r","").replace("\n", ""));
                    }
                    if(word.startsWith("<to>")) {
                        String time = word.replace("<to>","")
                                .replace("</to>","").replace("\r","").replace("\n", "");
                        time = String.format(Locale.US, "%.2f", (Long.parseLong(time) / 1000.0f));
                        application.setTime(time);
                    }
                    if(word.startsWith("<ip>")) {
                        application.setIp(word.replace("<ip>","")
                                .replace("</ip>","").replace("\r","").replace("\n", ""));
                    }
                    if(word.startsWith("<traf>")) {
                        String traff = word.replace("<traf>","")
                                .replace("</traf>","").replace("\r","").replace("\n", "");
                        traff = String.format(Locale.US, "%.2f", (Long.parseLong(traff) / (1024.0f * 1024.0f)));
                        application.setTraf(traff);
                    }
                }
            }
            infoNodes = nodes.toString().replace("{", "").replace("}", "");
            stop();
            application.setNodeIp(infoNodes);
            application.setSchedulerStatus(false);
        }
        return application;
    }

    public boolean isRun() {
        return isRunScheduler;
    }

    public void stop() {
        closeLogs();
        clear();
        if(server != null) {
            server.destroy();
        }
        if(workers != null) {
            workers.destroy();
        }
        if(scheduler != null) {
            scheduler.destroy();
        }
        try {
            readWorkersOut.interrupt();
            readSchedulerOut.interrupt();
            killApplication();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(int n, int minute) {
        if(separator.equals("\\")) {

        } else {
            Calendar calendar = Calendar.getInstance();
            String dir = ApplicationPath.APPLICATION_PATH + "remote-logs/"
                    + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE) + "/"
                    + calendar.get(Calendar.HOUR_OF_DAY) + "-" + minute + "_" + n + "/";
            String files = dir + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.MILLISECOND) + "_";
            try {
                schedulerLogs = new FileWriter(files + "scheduler.txt");
                workersLogs = new FileWriter(files + "workers.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new File(dir).mkdirs();
        }

    }

    private void closeLogs() {
        if(separator.equals("/") && schedulerLogs != null && workersLogs != null) {
            try {
                schedulerLogs.close();
                workersLogs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clear() {
        isRunScheduler = false;
        outWorkers = "";
        outScheduler = "";
        infoNodes = "";
        date = null;
        minute = 0;
    }

    private void killApplication() throws IOException {
        isRunKill = true;
        kill = Runtime.getRuntime().exec(runKillCommand, null, dir);
        while (isRunKill) {
            getData(kill, 4);
        }
        kill.destroy();
    }

    private String getData(Process process, int id) {
        StringBuilder out = new StringBuilder("");
        if(process != null) {
            int j = 0;
            byte outCommandLine[] = new byte[4096];
            try {
                j = process.getErrorStream().read(outCommandLine);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(j > 0) {
                byte[] data = new byte[j];
                for(int i = 0, q = 0; i < 4096 && q < j; i++) {
                    if(outCommandLine[i] != 0) {
                        data[q++] = outCommandLine[i];
                    }
                }
                out.append(new String(data)).append("\n");
            } else {
                switch (id) {
                    case 1:
                        isRunScheduler = false;
                        server.destroy();
                        workers.destroy();
                        scheduler.destroy();
                        break;
                    case 2:
                        break;
                    case 3:
                        workers.destroy();
                        break;
                    case 4:
                        kill.destroy();
                        isRunKill = false;
                        break;
                    default: break;
                }
            }
        } else {
            switch (id) {
                case 1:
                    isRunScheduler = false;
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    isRunKill = false;
                    break;
                default: break;
            }
        }
        return out.toString();
    }

    private class ReadSchedulerOut extends Thread {
        public void run() {
            while (isRunScheduler) {
                String temp = getData(scheduler, 1);
                outScheduler += temp;
                try {
                    if(separator.equals("/") && schedulerLogs != null && workersLogs != null) {
                        schedulerLogs.write(temp);
                    }
                    sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ReadWorkersOut extends Thread {
        public void run() {
            while (isRunScheduler) {
                String temp = getData(workers, 3);
                if(error.matcher(temp).matches()) {
                    if(server != null) {
                        server.destroy();
                    }
                    try {
                        server = Runtime.getRuntime().exec(runServerCommand, null, dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                outWorkers += temp;
                String[] split = temp.split(" ");
                for(String word: split) {
                    if(word.startsWith("<rem>")) {
                        String tag = word.replace("<rem>","")
                                .replace("</rem>","").replace("\r","").replace("\n", "");
                        if(nodes.containsKey(tag)) {
                            nodes.put(tag, "removing");
                        }
                    }
                    if(word.startsWith("<dow>")) {
                        String tag = word.replace("<dow>","")
                                .replace("</dow>","").replace("\r","").replace("\n", "");
                        if(nodes.containsKey(tag)) {
                            nodes.put(tag, "dowloading");
                        }
                    }
                    if(word.startsWith("<par>")) {
                        String tag = word.replace("<par>","")
                                .replace("</par>","").replace("\r","").replace("\n", "");
                        if(nodes.containsKey(tag)) {
                            nodes.put(tag, "parsing");
                        }
                    }
                    if(word.startsWith("<ret>")) {
                        String tag = word.replace("<ret>","")
                                .replace("</ret>","").replace("\r","").replace("\n", "");
                        if(nodes.containsKey(tag)) {
                            nodes.put(tag, "returning");
                        }
                    }
                    if(word.startsWith("<fin>")) {
                        String tag = word.replace("<fin>","")
                                .replace("</fin>","").replace("\r","").replace("\n", "");
                        if(nodes.containsKey(tag)) {
                            nodes.put(tag, "finished");
                        }
                    }
                    if(word.startsWith("<err>")) {
                        String tag = word.replace("<err>","")
                                .replace("</err>","").replace("\r","").replace("\n", "");
                        if(nodes.containsKey(tag)) {
                            nodes.put(tag, "error");
                        }
                    }
                }
                infoNodes = nodes.toString().replace("{", "").replace("}", "");
                try {
                    if(separator.equals("/") && schedulerLogs != null && workersLogs != null) {
                        workersLogs.write(temp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}