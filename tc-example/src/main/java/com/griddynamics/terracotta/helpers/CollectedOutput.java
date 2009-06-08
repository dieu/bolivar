package com.griddynamics.terracotta.helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * @author: apanasenko aka dieu
 * Date: 03.06.2009
 * Time: 14:19:13
 */
public class CollectedOutput extends Thread {
    private BufferedReader process;
    private StringBuffer output;

    public CollectedOutput(InputStream process) {
        this.process = new BufferedReader(new InputStreamReader(process));
        this.output = new StringBuffer();
    }

    public void startCollecting() {
        super.start();
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

    public boolean isEmpty() {
        return output.length() == 0;
    }

    public String toString() {
        return output.toString();
    }
}
