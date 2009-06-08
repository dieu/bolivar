package com.griddynamics.equestrian;

import com.griddynamics.equestrian.deploy.StartApplicationImpl;
import com.griddynamics.equestrian.helpers.impl.ParserHostEC2;
import com.griddynamics.equestrian.helpers.AmazonKeys;

/**
 * @author apanasenko aka dieu
 *         Date: 05.06.2009
 *         Time: 17:15:12
 */
public class Start {
    public static void main(String[] arg) throws Exception {
        ParserHostEC2 ec2 = new ParserHostEC2();
        ec2.setAws(new AmazonKeys());
        int min = 1;
        int max = ec2.getCountNode();
        int step = 1;
        boolean deploy = false;
        for(int i = 0; i < arg.length; i++) {
            if(arg[i].equals("-min")) {
                min = Integer.parseInt(arg[++i]);
            }
            if(arg[i].equals("-max")) {
                int j = Integer.parseInt(arg[++i]);
                if(max > j) {
                    max = j;
                }
            }
            if(arg[i].equals("-step")) {
                step = Integer.parseInt(arg[++i]);
            }
            if(arg[i].equals("-deploy")) {
                deploy = true;
            }
        }
        StartApplicationImpl application = new StartApplicationImpl();
        application.setParserHost(ec2);
        if(deploy) {
            application.deploy();
        }
        for(int i = min; i <= max; i += step) {
            application.start(i);
            application.verify();
        }
        System.exit(0);
    }
}
