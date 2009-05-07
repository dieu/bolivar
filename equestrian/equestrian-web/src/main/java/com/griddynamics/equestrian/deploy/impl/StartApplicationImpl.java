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
    private String runServerCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_server";
    private String runWorkersCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_workers";
    private String runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH + "cap.cmd run_scheduler";
    private String outServer = "";
    private String outWorkers = "";
    private String outScheduler = "";
    private boolean isRunServer = false;
    private boolean isRunWorkers = false;
    private boolean isRunScheduler = false;
    private Process server;
    private Process workers;
    private Process scheduler;
    private Pattern pattern = Pattern.compile("[a-zA-Z]+");


    public static void main(String[] arg) {
        StartApplicationImpl s = new StartApplicationImpl();
        s.start();
        s.verify();
        s.verify();
    }

    public void deploy() {
        //To change body of implemented methods use File | Settings | File Templates.
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
//            scheduler = Runtime.getRuntime().exec(runSchedulerCommand, null,
//                    new File(ApplicationPath.APPLICATION_PATH));
//            isRunScheduler = true;
//            Thread.sleep(1000L);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Application verify() {
        Application application = new Application();
        outServer += getData(server, 1);
        outWorkers += getData(workers, 2);
        outScheduler += getData(scheduler, 3);
        if(!isRunServer) {
            application.setServerStatus(false);
        }
        if(!isRunWorkers) {
            application.setWorkerStatus(false);
        }
        if(!isRunScheduler) {
            application.setScheluderStatus(false);
        }
        Matcher m = pattern.matcher(outServer);
        if(m.matches()) {
            application.setApplicationStatus(m.toMatchResult().group());
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
                        break;
                    case 2:
                        isRunWorkers = false;
                        break;
                    case 3:
                        isRunScheduler = false;
                        break;
                    default:
                        break;
                }
            }
        } else {
            switch (id) {
                case 1 :
                    isRunServer = false;
                    break;
                case 2:
                    isRunWorkers = false;
                    break;
                case 3:
                    isRunScheduler = false;
                    break;
                default:
                    break;
            }
        }
        return out.toString();
    }
}
