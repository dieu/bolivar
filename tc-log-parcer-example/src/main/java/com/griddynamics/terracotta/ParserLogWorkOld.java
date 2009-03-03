package com.griddynamics.terracotta;

import commonj.work.Work;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.net.URL;


public class ParserLogWorkOld implements Work {
    final private String pathToFile;
    final private Aggregator aggregator;

    public ParserLogWorkOld(String pathToFile, Aggregator aggregator) {
        this.pathToFile = pathToFile;
        this.aggregator = aggregator;
    }

    public String getPathToFile() {
        return pathToFile;
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
            char[] newArray = new char[20000];
            char[] oldArray = new char[20000];
            int newI = inputStreamReader.read(newArray);
            int oldI = 0;
            int oldOffset = 0;
            int newOffset = 0;
            int chunckCount = -1;
            String ip = "";
            boolean notSkip = true;
            while (newI >= 0) {
                for (int j = 0; j < newI; j++) {
                    if (newArray[j] == '\n' || newArray[j] == ' ' || newArray[j] == '\t') {
                        if (notSkip) {
                            if (newOffset > 0) {
                                chunckCount++;
                                if(chunckCount % 7 == 1){
                                    ip = new String(Arrays.copyOfRange(newArray,newOffset,j));
                                }
                                if (chunckCount % 7 == 4) {
                                    long n = 0L;
                                    for(int k = newOffset; k < j; k++ ){
                                        n *= 10;
                                        n += newArray[k] - '0';
                                    }
                                    aggregator.addStatitics(ip,n);
                                }
                            } else {
                                chunckCount++;
                                if(chunckCount % 7 == 1){
                                    ip = new String(Arrays.copyOfRange(oldArray,oldOffset,oldI));
                                    ip += new String(Arrays.copyOfRange(newArray,newOffset,j));
                                }
                                if (chunckCount % 7 == 4) {
                                    long n = 0L;
                                     for(int k = oldOffset; k < oldI; k++ ){
                                        n *= 10;
                                        n += oldArray[k] - '0';
                                    }
                                    for(int k = newOffset; k < j; k++ ){
                                        n *= 10;
                                        n += newArray[k] - '0';
                                    }
                                    aggregator.addStatitics(ip,n);
                                }
                            }
                        }
                        newOffset = j + 1;
                        notSkip = false;
                    } else {
                        notSkip = true;
                    }
                }
                oldArray = newArray;
                newArray = new char[2000];
                oldOffset = newOffset;
                oldI = newI;
                newI = inputStreamReader.read(newArray);
                newOffset = 0;
            }
            chunckCount++;
            //if (chunckCount % 8 == 4) sum += Long.parseLong(new String(Arrays.copyOfRange(newArray, oldOffset, oldI)));

            //System.out.println("------------------" + sum + "-----------------");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
