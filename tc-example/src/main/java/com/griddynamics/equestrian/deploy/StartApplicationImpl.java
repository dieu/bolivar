package com.griddynamics.equestrian.deploy;

import com.griddynamics.equestrian.helpers.ApplicationPath;
import com.griddynamics.equestrian.helpers.ParserHost;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 19:46:27
 */
public class StartApplicationImpl {
    private Logger logger  = Logger.getLogger(StartApplicationImpl.class);
    private String buildCommand;
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
    private Pattern error = Pattern.compile(regAll + "WARN - Can't connect to server" + regAll);
    private ParserHost parserHost;
    private Date date = null;
    private File dir;
    private FileWriter schedulerLogs;
    private FileWriter workersLogs;
    private Map<String, String> nodes;

    public StartApplicationImpl() {
        separator = System.getProperty("file.separator");
        if(separator.equals("\\")) {
            buildCommand  = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd build";
            uploadCommand  = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd upload_all";
            runServerCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd run_server";
            runWorkersCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd run_workers";
            runSchedulerCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd run_scheduler";
            runKillCommand = ApplicationPath.CAPISTRANO_PATH_WIN + "cap.cmd kill";
            dir = new File(ApplicationPath.APPLICATION_PATH_WIN);
        } else {
            buildCommand  = ApplicationPath.CAPISTRANO_PATH_WIN + "cap build";
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

    public void deploy() throws Exception {
        Runtime.getRuntime().exec(buildCommand, null, dir);
        Thread.sleep(10000L);
        parserHost.clear();
        nWorkers = parserHost.parse(Integer.MAX_VALUE);
        upload = Runtime.getRuntime().exec(uploadCommand, null, dir);
        isRunUpload = true;
        while(isRunUpload) {
            getData(upload, 2);
        }
    }

    public void start(int n) {
        try {
            deploy(n);
            if(date == null) {
                date = Calendar.getInstance().getTime();
                minute = Calendar.getInstance().get(Calendar.MINUTE);
            }
            Runtime.getRuntime().exec(runKillCommand, null, dir);
            Thread.sleep(1000L);
            server = Runtime.getRuntime().exec(runServerCommand, null, dir);
            Thread.sleep(5000L);
            workers = Runtime.getRuntime().exec(runWorkersCommand, null, dir);
            Thread.sleep(10000L);
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

    public void verify() {
        while(true) {
            if(!isRunScheduler) {
                try {
                    Runtime.getRuntime().exec(runKillCommand, null, dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logger.info(outScheduler);
                return;
            } else {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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

    private void deploy(int n) {
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

    private void init(int n, int minute) {
        if(separator.equals("\\")) {

        } else {
            Calendar calendar = Calendar.getInstance();
            String dir  = ApplicationPath.APPLICATION_PATH_NIX + "remote-logs/"
                    + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE) + "/"
                    + calendar.get(Calendar.HOUR_OF_DAY) + "-" + minute + "_" + n + "/";
            this.files = dir + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.MILLISECOND) + "_";
            try {
                schedulerLogs =  new FileWriter(files + "scheduler.txt");
                workersLogs = new FileWriter(files + "workers.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new File(dir).mkdirs();
        }

    }

    private class ReadSchedulerOut extends Thread {
        public void run() {
            while (isRunScheduler) {
                String temp = getData(scheduler, 1);
                outScheduler += temp;
                try {
                    if(temp != null) {
                        schedulerLogs.write(temp);
                        schedulerLogs.flush();
                    }
                    sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException ignored) {

                }
            }
        }
    }

    private class ReadWorkersOut extends Thread {
        public void run() {
            while (isRunScheduler) {
                String temp = getData(workers, 3);
                //WARN - Can't connect to server
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
                    if(temp != null) {
                        workersLogs.write(temp);
                        workersLogs.flush();
                    }
                    sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException ignored) {

                }
            }
        }
    }
}
