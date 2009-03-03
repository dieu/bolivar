package com.griddynamics.terracotta;

import commonj.work.Work;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.net.URL;


public class ParserLogWork implements Work {
    final private String pathToFile;
    private Long result;

    public ParserLogWork(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public synchronized Long getResult() {
        return result;
    }

    public synchronized void setResult(Long result) {
        this.result = result;
    }

    public boolean isDaemon() {
        return false;
    }

    public void release() {

    }

    public void run() {
        try {
            InputStreamReader inputStreamReader = null;
            if(pathToFile.startsWith("http")) {
                inputStreamReader = new InputStreamReader(new URL(pathToFile).openStream());
            }else{
                inputStreamReader = new FileReader(pathToFile);
            }
            char[] newArray = new char[2000];
            char[] oldArray = new char[2000];
            int newI = inputStreamReader.read(newArray);
            int oldI = 0;
            int oldOffset = 0;
            int newOffset = 0;
            int chunckCount = -1;
            long sum = 0L;
            boolean notSkip = true;
            while (newI >= 0) {
                for (int j = 0; j < newI; j++) {
                    if (newArray[j] == '\n' || newArray[j] == ' ') {
                        if (notSkip) {
                            if (newOffset > 0) {
                                chunckCount++;
                                if (chunckCount % 7 == 4) {
                                    sum += Long.parseLong(new String(Arrays.copyOfRange(newArray, newOffset, j)));
                                }
                            } else {
                                String s1 = new String(Arrays.copyOfRange(newArray, newOffset, j));
                                String s2 = new String(Arrays.copyOfRange(oldArray, oldOffset, oldI));
                                chunckCount++;
                                if (chunckCount % 7 == 4) {
                                    sum += Long.parseLong(s2 + s1);
                                }
                            }
                        }
                        newOffset = j + 1;
                        notSkip = false;
                    } else {
                        notSkip = true;
                    }
                }
                oldArray = Arrays.copyOf(newArray, newI);
                oldOffset = newOffset;
                oldI = newI;
                newI = inputStreamReader.read(newArray);
                newOffset = 0;
            }
            chunckCount++;
            if (chunckCount % 8 == 4) sum += Long.parseLong(new String(Arrays.copyOfRange(newArray, oldOffset, oldI)));
            setResult(sum);
            System.out.println("------------------" + sum + "-----------------");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
