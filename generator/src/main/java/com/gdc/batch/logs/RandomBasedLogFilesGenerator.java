package com.gdc.batch.logs;

import com.gdc.batch.logs.random.RandomValue;
import com.gdc.batch.logs.config.ConfigFormatParser;
import com.gdc.batch.logs.config.Config;

import java.util.*;
import java.util.concurrent.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

/**
 * Created by vkatson
 * Date: 27.02.2009
 * Time: 11:17:36
 */
public class RandomBasedLogFilesGenerator {

    private List<RandomValue> randomGeneratorsList;
    private int nThreads = Integer.valueOf(Config.getProperty("thread.number"));
    private String filenamePrefix = Config.getProperty("filename.prefix");

    public RandomBasedLogFilesGenerator() {
        ConfigFormatParser configParser = new ConfigFormatParser();
        try {
            randomGeneratorsList = configParser.parseConfig();
        } catch (InstantiationException e) {
            System.err.println("Invalid config!");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Invalid config!");
            e.printStackTrace();
        }
    }

    public void generateAsync() throws IllegalAccessException, InstantiationException, IOException, InterruptedException {
        prepareLogsDirectory();

        long fileNum = Long.valueOf(Config.getProperty("files.number"));
        System.out.println("Generation started.");
        long sTime = System.nanoTime();

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (long i = 0; i < fileNum; ++i) {
            String filename = filenamePrefix + System.nanoTime() / 1000;
            executorService.execute(new LogFileBuider(filename));
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        System.out.println("Generation finished in " + (System.nanoTime() - sTime) / 1000000 + " ms.");
    }

    public void generateSync() throws InterruptedException, IOException {
        prepareLogsDirectory();

        long fileNum = Long.valueOf(Config.getProperty("files.number"));
        System.out.println("Sync generation started.");
        long sTime = System.nanoTime();
        for (long i = 0; i < fileNum; ++i) {
            (new LogFileBuider(filenamePrefix + System.currentTimeMillis())).createFile();
        }

        System.out.println("Sync Generation finished in " + (System.nanoTime() - sTime) / 1000000 + " ms.");
    }

    private void prepareLogsDirectory() {
        File logsDir = new File(Config.getProperty("files.location"));
        if (logsDir.isDirectory()) {
            if (Config.getProperty("do.clean.before.launch").equals("1")) {
                System.out.println("Cleaning logs directory.");
                deleteFilesInDirectory(logsDir);
            }
        } else {
            System.out.println("Creating logs directory.");
            logsDir.mkdir();
        }
    }

    private void deleteFilesInDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFilesInDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
    }

    private class LogFileBuider implements Runnable {

        private String filename;

        private LogFileBuider(String filename) {
            this.filename = filename;
        }

        private void createFile() throws IOException {
            System.out.println("Writing to file " + filename + "...");
            long recordsNum = Long.valueOf(Config.getProperty("records.number"));
            String filePath = Config.getProperty("files.location") + File.separator + filename;

            FileWriter fstream = new FileWriter(filePath, true);
            BufferedWriter out = new BufferedWriter(fstream);

            for (long i = 0; i < recordsNum; ++i) {
                writeString(out);
            }
            out.close();
        }

        private void writeString(BufferedWriter out) throws IOException {
            for (RandomValue rnd : randomGeneratorsList) {
                out.write(rnd.getRandomValue() + "\t");
            }
            out.write(System.getProperty("line.separator"));
        }

        public void run() {
            try {
                createFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
