package com.griddynamics.terracotta;


public class Node {
    
     public static void main(String[] args) throws Exception {
        Example example = new Example();
        Thread.sleep(20000L);  
        example.startWorker();
    }
}
