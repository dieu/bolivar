package com.griddynamics.terracotta;


public class AllInOneJvm {

    public static void main(String[] args) throws Exception {
        Example example = new Example();
        example.startWorkers(new String[]{"node1","node2"});
        example.lunchJob(115L,new String[]{"node1","node2"});
    }
}
