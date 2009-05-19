package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StartApplication;
import com.griddynamics.equestrian.helpers.ApplicationPath;
import com.griddynamics.equestrian.helpers.ParserHost;
import com.griddynamics.equestrian.helpers.impl.ParserHostXml;
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
    private String uploadCommand = ApplicationPath.CAPISTRANO_PATH + "cap upload_all";
    private String runServerCommand = ApplicationPath.CAPISTRANO_PATH + "cap run_server";
    private String runWorkersCommand = ApplicationPath.CAPISTRANO_PATH + "cap run_workers";
    private String runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH + "cap run_scheduler";
    private String runKillCommand = ApplicationPath.CAPISTRANO_PATH + "cap kill";
//    private String uploadCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd upload_all";
//    private String runServerCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_server";
//        private String runWorkersCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_workers";
//        private String runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_scheduler";
//        private String runKillCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd kill";
    private String outScheduler = "";
    private int nWorkers = 0;
    private boolean isRunScheduler = false;
    private boolean isRunUpload = false;
    private Process server;
    private Process workers;
    private Process scheduler;
    private Process upload;
    private String regAll = "[a-zA-Z\\d\\s\\S]*";
    private String regTime = "\\s*<nodeTime>\\d+</nodeTime>\\s*";
    private Pattern patTime = Pattern.compile(regAll + regTime + regAll);
    private ParserHost parserHost;
    private Application application;
    private Date date;
    private Map<String, Boolean> nodes;

    public void setParserHost(ParserHost parserHost) {
        this.parserHost = parserHost;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void deploy(int n) {
        try {
            Runtime.getRuntime().exec(runKillCommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
            Thread.sleep(1000L);
            parserHost = new ParserHostXml();
            nWorkers = parserHost.parse(n);
            nodes =  parserHost.getNodeIp();
            upload = Runtime.getRuntime().exec(uploadCommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
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
            date = Calendar.getInstance().getTime();
            Runtime.getRuntime().exec(runKillCommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
            Thread.sleep(1000L);
            server = Runtime.getRuntime().exec(runServerCommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
            Thread.sleep(1000L);
            workers = Runtime.getRuntime().exec(runWorkersCommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
            Thread.sleep(1000L);
            scheduler = Runtime.getRuntime().exec(runSchedulerCommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
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
                Runtime.getRuntime().exec(runKillCommand, null,
                        new File(ApplicationPath.APPLICATION_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(patTime.matcher(outScheduler).matches()) {
                String[] split = outScheduler.split(" ");
                for(String word: split) {
                    if(word.startsWith("<nodeTime>")) {
                        application.setTime(word.replace("<nodeTime>","")
                                .replace("</nodeTime>","").replace("\r","").replace("\n", ""));
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
            application.setSchedulerStatus(false);
        } else {
            application.setApplicationStatus("Wait...");
        }
        return application;
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
}
