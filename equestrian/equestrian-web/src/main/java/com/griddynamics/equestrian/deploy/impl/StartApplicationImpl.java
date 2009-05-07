package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StartApplication;
import com.griddynamics.equestrian.helpers.ApplicationPath;
import com.griddynamics.equestrian.model.Application;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 19:46:27
 */
public class StartApplicationImpl implements StartApplication<Application> {
    private String buildCaommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd build";
    private String uploadCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd upload_all";
    private String runServerCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_server";
    private String runWorkersCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_workers";
    private String runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_scheduler";
    private String runKillCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd kill";
//    private String outServer = "";
//    private String outWorkers = "";
    private String outScheduler = "";
    private boolean isRunServer = false;
    private boolean isRunWorkers = false;
    private boolean isRunScheduler = false;
    private boolean isRunBuild = false;
    private boolean isRunUpload = false;
    private Process server;
    private Process workers;
    private Process scheduler;
    private Process build;
    private Process upload;
    private String regAll = "[a-zA-Z\\d\\s\\S]*";
    private String regTime = "\\s*<nodeTime>\\d+</nodeTime>\\s*";
    private String regIp = "\\s*<ip>\\d\\.+</ip>\\s*";
    private String regTraff = "\\s*<traf>\\d+</traf>\\s*";
    private Pattern patTime = Pattern.compile(regAll + regTime + regAll);

    public static void main(String[] arg) {
        StartApplicationImpl s = new StartApplicationImpl();
        s.start();
        s.verify();
        s.verify();
    }

    public void deploy() {
        try {
            build = Runtime.getRuntime().exec(buildCaommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
            isRunBuild = true;
            while(isRunBuild) {
                getData(build, 4);
                Thread.sleep(60000L);
            }
            upload = Runtime.getRuntime().exec(uploadCommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
            isRunUpload = true;
            while(isRunUpload) {
                getData(upload, 5);
                Thread.sleep(60000L);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            server = Runtime.getRuntime().exec(runServerCommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
            isRunServer = true;
            Thread.sleep(1000L);
            workers = Runtime.getRuntime().exec(runWorkersCommand, null,
                    new File(ApplicationPath.APPLICATION_PATH));
            isRunWorkers = true;
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
        Application application = new Application();
        outScheduler += getData(scheduler, 3);
        if(!isRunScheduler) {
            try {
                Runtime.getRuntime().exec(runKillCommand, null,
                        new File(ApplicationPath.APPLICATION_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
            isRunServer = false;
            isRunWorkers = false;
            outScheduler = "";
            application.setWorkerStatus(false);
            application.setServerStatus(false);
            application.setScheluderStatus(false);
        }
        if(patTime.matcher(outScheduler).matches()) {
            String[] split = outScheduler.split(" ");
            StringBuilder status = new StringBuilder("");
            for(String word: split) {
                if(word.startsWith("<nodeTime>")) {
                    status.append(" Result time: ").append(word.replace("<nodeTime>","")
                                                              .replace("</nodeTime>",""));
                }
                if(word.startsWith("<ip>")) {
                    status.append(" Result ip: ").append(word.replace("<ip>","")
                                                            .replace("</ip>",""));
                }
                if(word.startsWith("<traf>")) {
                    status.append(" Result traffic: ").append(word.replace("<traf>","")
                                                                 .replace("</traf>",""));
                }
            }
            application.setApplicationStatus(status.toString().replace("\r","").replace("\n",""));
        } else {
            application.setApplicationStatus("Wait...");
        }
        if(application.getApplicationStatus().equals("")) {
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
                    case 1 :
                        isRunServer = false;
                        server.destroy();
                        break;
                    case 2:
                        isRunWorkers = false;
                        workers.destroy();
                        break;
                    case 3:
                        isRunScheduler = false;
                        scheduler.destroy();
                        break;
                    case 4:
                        isRunBuild = false;
                        build.destroy();
                        break;
                    case 5:
                        isRunUpload = false;
                        upload.destroy();
                        break;
                    default:
                        break;
                }
            }
        } else {
            switch (id) {
                case 1 :
                    isRunServer = false;
                    server.destroy();
                    break;
                case 2:
                    isRunWorkers = false;
                    workers.destroy();
                    break;
                case 3:
                    isRunScheduler = false;
                    scheduler.destroy();
                    break;
                case 4:
                    isRunBuild = false;
                    build.destroy();
                    break;
                case 5:
                    isRunUpload = false;
                    upload.destroy();
                    break;
                default:
                    break;
            }
        }
        return out.toString();
    }
}
