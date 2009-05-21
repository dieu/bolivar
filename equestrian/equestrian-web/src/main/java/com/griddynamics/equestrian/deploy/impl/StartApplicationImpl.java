package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StartApplication;
import com.griddynamics.equestrian.helpers.ApplicationPath;
import com.griddynamics.equestrian.helpers.ParserHost;
import com.griddynamics.equestrian.model.Application;

import java.io.File;
import java.io.IOException;
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
    private Map<String, Boolean> nodes;

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
            upload = Runtime.getRuntime().exec(uploadCommand, null, dir);
            isRunUpload = true;
            while(isRunUpload) {
                getData(upload, 2);
                Thread.sleep(1000L);
            }
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
            init(nWorkers, minute);
            server = Runtime.getRuntime().exec(runServerCommand, null, dir);
            Thread.sleep(1000L);
            init(nWorkers, minute);
            workers = Runtime.getRuntime().exec(runWorkersCommand, null, dir);
            Thread.sleep(1000L);
            init(nWorkers, minute);
            scheduler = Runtime.getRuntime().exec(runSchedulerCommand, null, dir);
            isRunScheduler = true;
            Thread.sleep(1000L);
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
        application.setNodeIp(nodes);
        application.setSchedulerStatus(isRunScheduler);
        outScheduler += getData(scheduler, 1);
        if(!isRunScheduler) {
            try {
                init(nWorkers, minute);
                Runtime.getRuntime().exec(runKillCommand, null, dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(patTime.matcher(outScheduler).matches()) {
                String[] split = outScheduler.split(" ");
                for(String word: split) {
                     if(word.startsWith("<re>")) {
                        application.setDowloanding(word.replace("<re>","")
                                .replace("</re>","").replace("\r","").replace("\n", ""));
                    }
                    if(word.startsWith("<do>")) {
                        application.setDowloanding(word.replace("<do>","")
                                .replace("</do>","").replace("\r","").replace("\n", ""));
                    }
                    if(word.startsWith("<pa>")) {
                        application.setParsing(word.replace("<pa>","")
                                .replace("</pa>","").replace("\r","").replace("\n", ""));
                    }
                    if(word.startsWith("<ag>")) {
                        application.setAggregating(word.replace("<ag>","")
                                .replace("</ag>","").replace("\r","").replace("\n", ""));
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
            outScheduler = "";
            date = null;
            minute = 0;
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
        isRunUpload = false;
        isRunScheduler = false;
        outScheduler = "";
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
                default: break;
            }
        }
        return out.toString();
    }

    private void init(int n, int minute) {
        if(separator.equals("\\")) {
            uploadCommand  = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd upload_all";
            runServerCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd run_server";
            runWorkersCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd run_workers";
            runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd run_scheduler";
            runKillCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd kill";
        } else {
            Calendar calendar = Calendar.getInstance();
            String commandLine = " 2>&1 | tee ";
            String dir  = "remote-logs/"
                    + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE) + "/"
                    + calendar.get(Calendar.HOUR_OF_DAY) + "-" + minute + "_" + n + "/";
            String files = calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.MILLISECOND) + "_";
            String createLogs = commandLine + dir + files;
            new File(ApplicationPath.APPLICATION_PATH_NIX + dir).mkdirs();
            uploadCommand  = ApplicationPath.CAPISTRANO_PATH_NIX + "cap upload_all" + createLogs + "upload.txt";
            runServerCommand = ApplicationPath.CAPISTRANO_PATH_NIX + "cap run_server" + createLogs + "run_server.txt";
            runWorkersCommand = ApplicationPath.CAPISTRANO_PATH_NIX + "cap run_workers" + createLogs + "run_workers.txt";
            runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH_NIX + "cap run_scheduler" + createLogs + "run_scheduler.txt";
            runKillCommand = ApplicationPath.CAPISTRANO_PATH_NIX + "cap kill" + createLogs + "cap kill.txt";
        }

    }
}
