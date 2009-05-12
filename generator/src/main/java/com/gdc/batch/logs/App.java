package com.gdc.batch.logs;

import java.io.IOException;

public class App
{
    public static void main( String[] args ) throws InstantiationException, IllegalAccessException, IOException, InterruptedException {
        RandomBasedLogFilesGenerator generator = new RandomBasedLogFilesGenerator();
        generator.generateAsync();
//        generator.generateSync();
    }
}
