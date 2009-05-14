package com.griddynamics.terracotta.parser.separate;

import org.apache.log4j.Logger;

import java.io.*;

import com.griddynamics.terracotta.util.StrUtil;

/**
 * @author agorbunov @ 08.05.2009 16:30:17
 */
public class Wget {

    private static class CollectedOutput extends Thread {
        private BufferedReader process;
        private StringBuffer output;

        public CollectedOutput(InputStream process) {
            this.process = new BufferedReader(new InputStreamReader(process));
            this.output = new StringBuffer();
        }

        public void startCollecting() {
            start();
        }

        public void run() {
            try {
                readOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void readOutput() throws IOException {
            while (true) {
                String s = process.readLine();
                if (s == null)
                    break;
                output.append(s);
            }
        }

        public String toString() {
            return output.toString();
        }
    }

    private static final Logger logger = Logger.getLogger(Wget.class);
    private String url;
    private String localDir;
    private transient Process wget;
    private transient CollectedOutput stdout;
    private transient CollectedOutput stderr;

    public Wget(String url, String localDir) {
        this.url = url;
        this.localDir = localDir;
    }

    public void startDownloading() {
        try {
            invokeWget();
            collectStdoutAndStderr();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeWget() throws IOException {
        String[] command = commandForDownloading();
        logger.info("Invoking command " + StrUtil.arrayToString(command));
        wget = Runtime.getRuntime().exec(command);
    }

    private String[] commandForDownloading() {
        return new String[]{
                "wget", "--directory-prefix=" + localDir, url};
    }

    private void collectStdoutAndStderr() {
        stdout = new CollectedOutput(wget.getInputStream());
        stderr = new CollectedOutput(wget.getErrorStream());
        stdout.startCollecting();
        stderr.startCollecting();
    }

    public void waitUntilDownloadCompletes() {
        verifyIsStarted();
        waitUntilCompletes();
        verifyCompletedSuccessfully();
    }

    private void verifyIsStarted() {
        if (wget == null)
            throw new IllegalStateException("Wget is not started");
    }

    private void waitUntilCompletes() {
        try {
            wget.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyCompletedSuccessfully() {
        if (completedWithError()) {
            printErrorDetails();
            throwDownloadException();
        }
    }

    private boolean completedWithError() {
        return wget.exitValue() != 0;
    }

    private void printErrorDetails() {
        printErrorSummary();
        printOutputStream();
        printErrorStream();
    }

    private void printErrorSummary() {
        System.err.println(errorSummary());
    }

    private String errorSummary() {
        return String.format(
                "Could not download %s to directory %s.\n" +
                "Wget finished with exit code %d",
                url, localDir,
                wget.exitValue());
    }

    private void printOutputStream() {
        System.out.println(stdout.toString());
    }

    private void printErrorStream() {
        System.err.println(stderr.toString());
    }

    private void throwDownloadException() {
        throw new RuntimeException(errorSummary());
    }
}

