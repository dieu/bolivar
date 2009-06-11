package com.griddynamics.terracotta.helpers;

import java.io.IOException;

/**
 * @author agorbunov @ 08.05.2009 16:30:17
 */
public class Wget {
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
        waitUntilCompletion();
        verifyCompletedSuccessfully();
    }

    private void verifyIsStarted() {
        if (wget == null)
            throw new IllegalStateException("Wget is not started");
    }

    private void waitUntilCompletion() {
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
        System.err.println(exitCode());
    }

    private String errorSummary() {
        return String.format(
                "Could not download %s to directory %s.",
                url, localDir);
    }

    private String exitCode() {
        return String.format(
                "Wget finished with exit code %d.",
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

