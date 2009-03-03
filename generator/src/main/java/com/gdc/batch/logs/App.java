package com.gdc.batch.logs;

import java.io.IOException;

public class App
{
    public static void main( String[] args ) throws InstantiationException, IllegalAccessException, IOException, InterruptedException {
        LogFilesGenerator generator = new LogFilesGenerator();
        generator.generateAsync();
//        generator.generateSync();
    }
}
