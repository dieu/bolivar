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
    private String files = "";
    private int nWorkers = 0;
    private int minute = 0;
    private boolean isRunScheduler = false;
    private boolean isRunUpload = false;
    private Process server;
    private Process workers;
    private Process scheduler;
    private Process upload;
    private String regAll = "[a-zA-Z\\d\\s\\S]*";
    private String regTime = "\\s*<to>\\d+</to>\\s*";
    private Pattern patTime = Pattern.compile(regAll + regTime + regAll);
    private ParserHost parserHost;
    private Application application;
    private Date date = null;
    private File dir;
    private Map<String, String> nodes;

    public StartApplicationImpl() {
        separator = System.getProperty("file.separator");
        if(separator.equals("\\")) {
            uploadCommand  = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd upload_all";
            runServerCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd run_server";
            runWorkersCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd run_workers";
            runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd run_scheduler";
            runKillCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd kill";
            dir = new File(ApplicationPath.APPLICATION_PATH_WIN);
        } else {
            uploadCommand  = ApplicationPath.CAPISTRANO_PATH_NIX + "cap upload_all";
            runServerCommand = ApplicationPath.CAPISTRANO_PATH_NIX + "cap run_server";
            runWorkersCommand = ApplicationPath.CAPISTRANO_PATH_NIX + "cap run_workers";
            runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH_NIX + "cap run_scheduler";
            runKillCommand = ApplicationPath.CAPISTRANO_PATH_NIX + "cap kill";
            dir = new File(ApplicationPath.APPLICATION_PATH_NIX);
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
            Runtime.getRuntime().exec(runKillCommand, null, dir);
            Thread.sleep(1000L);
            parserHost.clear();
            nWorkers = parserHost.parse(n);
            nodes =  parserHost.getNodeIp();
            date = Calendar.getInstance().getTime();
            minute = Calendar.getInstance().get(Calendar.MINUTE);
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
            if(date == null) {
                date = Calendar.getInstance().getTime();
                minute = Calendar.getInstance().get(Calendar.MINUTE);
            }
            init(nWorkers, minute);
            Runtime.getRuntime().exec(runKillCommand, null, dir);
            Thread.sleep(1000L);
            server = Runtime.getRuntime().exec(runServerCommand, null, dir);
            Thread.sleep(1000L);
            workers = Runtime.getRuntime().exec(runWorkersCommand, null, dir);
            Thread.sleep(1000L);
            scheduler = Runtime.getRuntime().exec(runSchedulerCommand, null, dir);
            isRunScheduler = true;
            Thread.sleep(1000L);
            ReadSchedulerOut readSchedulerOut = new ReadSchedulerOut();
            readSchedulerOut.start();
            ReadWorkersOut readWorkersOut = new ReadWorkersOut();
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
            try {
                Runtime.getRuntime().exec(runKillCommand, null, dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(patTime.matcher(outScheduler).matches()) {
                String[] split = outScheduler.split(" ");
                for(String word: split) {
                    if(word.startsWith("<op>")) {
                        application.setParsing(word.replace("<op>","")
                                .replace("</op>","").replace("\r","").replace("\n", ""));
                    }
                    if(word.startsWith("<or>")) {
                        application.setReturning(word.replace("<or>","")
                                .replace("</or>","").replace("\r","").replace("\n", ""));
                    }
                    if(word.startsWith("<to>")) {
                        application.setTime(word.replace("<to>","")
                                .replace("</to>","").replace("\r","").replace("\n", ""));
                    }
                    if(word.startsWith("<ip>")) {
                        application.setIp(word.replace("<ip>","")
                                .replace("</ip>","").replace("\r","").replace("\n", ""));
                    }
                    if(word.startsWith("<traf>")) {
                        application.setTraf(word.replace("<traf>","")
                                .replace("</traf>","").replace("\r","").replace("\n", ""));
                    }
                }
            }
            if(!outScheduler.equals("") && separator.equals("/")) {
                try {
                    FileWriter schedulerLogs = new FileWriter(files + "scheduler.txt");
                    schedulerLogs.write(outScheduler);
                    schedulerLogs.close();
                    FileWriter workersLogs = new FileWriter(files + "workers.txt");
                    workersLogs.write(outWorkers);
                    workersLogs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for(String key: nodes.keySet()) {
                nodes.put(key, "stop");
            }
            infoNodes = nodes.toString().replace("{", "").replace("}", "");
            outScheduler = "";
            outWorkers = "";
            infoNodes = "";
            date = null;
            minute = 0;
            application.setNodeIp(infoNodes);
            application.setSchedulerStatus(false);
        } else {
            application.setApplicationStatus("Wait...");
        }
        return application;
    }

    public boolean isRun() {
        return isRunScheduler;
    }

    public void stop() {
        if(separator.equals("/")) {
            try {
                FileWriter schedulerLogs = new FileWriter(files + "scheduler.txt");
                schedulerLogs.write(outScheduler);
                schedulerLogs.close();
                FileWriter workersLogs = new FileWriter(files + "workers.txt");
                workersLogs.write(outWorkers);
                workersLogs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isRunUpload = false;
        isRunScheduler = false;
        outWorkers = "";
        outScheduler = "";
        infoNodes = "";
        date = null;
        minute = 0;
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
            Runtime.getRuntime().exec(runKillCommand, null, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        isRunUpload = false;
                        upload.destroy();
                        break;
                    case 3:
                        workers.destroy();
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
                    isRunUpload = false;
                    break;
                case 3:
                    break;
                default: break;
            }
        }
        return out.toString();
    }

    private void init(int n, int minute) {
        if(separator.equals("\\")) {

        } else {
            Calendar calendar = Calendar.getInstance();
            String dir  = ApplicationPath.APPLICATION_PATH_NIX + "remote-logs/"
                    + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE) + "/"
                    + calendar.get(Calendar.HOUR_OF_DAY) + "-" + minute + "_" + n + "/";
            this.files = dir + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.MILLISECOND) + "_";
            new File(dir).mkdirs();
        }

    }

    private class ReadSchedulerOut extends Thread {
        public void run() {
            while (isRunScheduler) {
                outScheduler += getData(scheduler, 1);
                try {
                    sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ReadWorkersOut extends Thread {
        public void run() {
            while (isRunScheduler) {
                outWorkers += getData(workers, 3);
                String[] split = outWorkers.split(" ");
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
                    sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
