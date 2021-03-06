package com.griddynamics.equestrian.helpers.impl;

import com.griddynamics.equestrian.helpers.ParserHost;
import com.griddynamics.equestrian.helpers.ApplicationPath;
import com.griddynamics.equestrian.helpers.AmazonKeys;
import com.xerox.amazonws.ec2.*;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

/**
 * @author: apanasenko aka dieu
 * Date: 27.05.2009
 * Time: 14:18:50
 */
public class ParserHostEC2 implements ParserHost{
    private List<String> workersIp;
    Map<String, String> nodes;
    private String serverIp;
    private String schedulerIp;
    private String pathApp;
    private int n;
    private AmazonKeys aws;

    public ParserHostEC2() {
        pathApp = ApplicationPath.APPLICATION_PATH;
        workersIp = new ArrayList<String>();
        nodes = new HashMap<String, String>();
        serverIp = "";
        schedulerIp = "";
        n = 0;
    }

    public void setAws(AmazonKeys aws) {
        this.aws = aws;
    }

    public int parse(int n) throws Exception {
        this.n = n;
        nodes.clear();
        Jec2 ec2 = new Jec2(aws.getAWSAccessKeyId(), aws.getSecretAccessKey());
        List<String> params = new ArrayList<String>();
        List<ReservationDescription> instances = ec2.describeInstances(params);
        for (ReservationDescription res : instances) {
            if (res.getInstances() != null) {
                for (ReservationDescription.Instance inst : res.getInstances()) {
                    if(inst.isRunning() && (aws.getUserId().equals("") || inst.getKeyName().equals(aws.getUserId()))) {
                        if(inst.getImageId().equals(aws.getWorkerImageId()) && workersIp.size() < n) {
                            workersIp.add(inst.getDnsName());
                        }
                        if(inst.getImageId().equals(aws.getServerImageId())) {
                            serverIp = inst.getDnsName();

                        }
                        if(inst.getImageId().equals(aws.getSchedulerImageId())) {
                            schedulerIp = inst.getDnsName();
                        }
                    }
                }
            }
        }
        writeCapFile();
        this.n = workersIp.size();
        return this.n;
    }

    public int parse() throws Exception {
        this.n = Integer.MAX_VALUE;
        Jec2 ec2 = new Jec2(aws.getAWSAccessKeyId(), aws.getSecretAccessKey());
        List<String> params = new ArrayList<String>();
        List<ReservationDescription> instances = ec2.describeInstances(params);
        for (ReservationDescription res : instances) {
            if (res.getInstances() != null) {
                for (ReservationDescription.Instance inst : res.getInstances()) {
                    if(inst.isRunning() && (aws.getUserId().equals("") || inst.getKeyName().equals(aws.getUserId()))) {
                        if(inst.getImageId().equals(aws.getWorkerImageId()) && workersIp.size() < n) {
                            workersIp.add(inst.getDnsName());
                        }
                        if(inst.getImageId().equals(aws.getServerImageId())) {
                            serverIp = inst.getDnsName();
                        }
                        if(inst.getImageId().equals(aws.getSchedulerImageId())) {
                            schedulerIp = inst.getDnsName();
                        }
                    }
                }
            }
        }
        writeCapFile();
        return workersIp.size();
    }

    public int getCountNode() throws Exception {
        clear();
        this.n = Integer.MAX_VALUE;
        nodes = new HashMap<String, String>();
        Jec2 ec2 = new Jec2(aws.getAWSAccessKeyId(), aws.getSecretAccessKey());
        List<String> params = new ArrayList<String>();
        List<ReservationDescription> instances = ec2.describeInstances(params);
        for (ReservationDescription res : instances) {
            if (res.getInstances() != null) {
                for (ReservationDescription.Instance inst : res.getInstances()) {
                    if(inst.isRunning() && (aws.getUserId().equals("") || inst.getKeyName().equals(aws.getUserId()))) {
                        if(inst.getImageId().equals(aws.getWorkerImageId()) && workersIp.size() < n) {
                            workersIp.add(inst.getPrivateDnsName());
                            nodes.put(inst.getPrivateDnsName().split("[.]")[0], "idle");
                        }
                        if(inst.getImageId().equals(aws.getServerImageId())) {
                            serverIp = inst.getPrivateDnsName();
                            nodes.put(inst.getPrivateDnsName().split("[.]")[0], "running");
                        }
                        if(inst.getImageId().equals(aws.getSchedulerImageId())) {
                            schedulerIp = inst.getPrivateDnsName();
                            nodes.put(inst.getPrivateDnsName().split("[.]")[0], "running");
                        }
                    }
                }
            }
        }
        int count = workersIp.size();
        workersIp = new ArrayList<String>();
        n = 0;
        return count;
    }

    public void clear() {
        workersIp = new ArrayList<String>();
        serverIp = "";
        schedulerIp = "";
        n = 0;
    }

    public Map<String,String> getNodeIp() {
        return nodes;
    }

    public String getNodeInfo() {
        return  nodes.toString().replace("{", "").replace("}", "");
    }

    public int getSchedulerSize() {
        if(!schedulerIp.equals("")) {
            return 1;
        }
        return 0;
    }

    public int getServerSize() {
        if(!serverIp.equals("")) {
            return 1;
        }
        return 0;
    }

    private void writeCapFile() throws IOException {
        if(!serverIp.equals("") && !schedulerIp.equals("") && workersIp.size() != 0) {
            StringBuilder contentFile = new StringBuilder("");
            contentFile.append("SERVER_ADDR = \"").append(serverIp).append("\" \n");
            contentFile.append("COUNT_WORKERS = \"").append(workersIp.size()).append("\" \n");
            contentFile.append("JAVA_HOME = \"/usr/lib/jvm/java-6-sun\" \n\n");
            contentFile.append("role :workers, ");
            for(String ip: workersIp) {
                contentFile.append("\"");
                contentFile.append(ip);
                contentFile.append("\",");
            }
            contentFile.deleteCharAt(contentFile.length() - 1);
            contentFile.append(" \n");
            contentFile.append("role :scheduler, \"").append(schedulerIp).append("\" \n");
            contentFile.append("role :server, SERVER_ADDR\n" +
                    "\n" +
                    "set :user, 'agorbunov'\n" +
                    "set :password, '123456'\n" +
                    "\n" +
                    "EXAMPLE_DIR = \"parser-with-blocking-queue\"\n" +
                    "TARGET_DIR = \"bolivar\"\n" +
                    "NODE = \"node-unit.jar\"\n" +
                    "SCHEDULER = \"scheduler-unit.jar\"\n" +
                    "JARS = [NODE, SCHEDULER]\n" +
                    "DSO_BOOT = \"dso-boot.linux.java-6.10.jar\"\n" +
                    "TC_DIR = \"terracotta-3.0.0\"\n" +
                    "CONFIG = \"tc-config.xml\"\n" +
                    "MISC = \"misc\"\n" +
                    "DOWNLOADED_LOGS = \"downloaded-logs\"\n" +
                    "\n" +
                    "def scp(src, dst)\n" +
                    "  body = File.open(src, \"rb\") { |f| f.read }\n" +
                    "  run \"mkdir -p #{TARGET_DIR}\"\n" +
                    "  put(body, File.join(TARGET_DIR, dst))\n" +
                    "end\n" +                                   "\n" +
                    "def target(dir)\n" +
                    "  File.join(TARGET_DIR, dir)\n" +
                    "end\n" +
                    "\n" +
                    "desc \"Build the project from sources\"\n" +
                    "task :build do\n" +
                    "  system(\"cd parser-with-blocking-queue && mvn clean package && mvn package -P Worker\")\n" +
                    "end\n" +
                    "\n" +
                    "desc \"Uploads tc-config.xml to all nodes\"\n" +
                    "task :upload_config do\n" +
                    "  scp(File.join(EXAMPLE_DIR, CONFIG), CONFIG)\n" +
                    "end\n" +
                    "\n" +
                    "task :upload_jars do\n" +
                    "  JARS.each { |jar| scp(File.join(EXAMPLE_DIR, \"target\", jar), jar) }\n" +
                    "  scp(File.join(MISC, DSO_BOOT), DSO_BOOT)\n" +
                    "end\n" +
                    "\n" +
                    "task :upload_tc do\n" +
                    "  tc_tar = TC_DIR + \".tar.gz\"\n" +
                    "  scp(File.join(MISC, tc_tar), tc_tar)\n" +
                    "  run \"cd #{TARGET_DIR} && tar xzf #{tc_tar}\"\n" +
                    "end\n" +
                    "\n" +
                    "task :upload_all do\n" +
                    "  # upload config\n" +
                    "  scp(File.join(EXAMPLE_DIR, CONFIG), CONFIG)\n" +
                    "\n" +
                    "  # upload jars\n" +
                    "  JARS.each { |jar| scp(File.join(EXAMPLE_DIR, \"target\", jar), jar) }\n" +
                    "  scp(File.join(MISC, DSO_BOOT), DSO_BOOT)\n" +
                    "end\n" +
                    "\n" +
                    "task :run_workers, :roles => :workers do\n" +
                    "  run \"rm -f #{DOWNLOADED_LOGS}/*\"\n" +
                    "  run \"java -Xms512m -Xmx512m -Xbootclasspath/p:#{TARGET_DIR}/#{DSO_BOOT} -Dtc.install-root=#{File.join(TARGET_DIR, TC_DIR)} -Dtc.server=#{SERVER_ADDR} -Dtc.config=#{TARGET_DIR}/tc-config.xml -DtypeOfWork=parsing -jar #{TARGET_DIR}/#{NODE} 2>&1\"\n" +
                    "end" +
                    "\n" +
                    "task :run_scheduler, :roles => :scheduler do\n" +
                    "  workers = find_servers(:roles => :workers)\n" +
                    "  run \"java -Xbootclasspath/p:#{TARGET_DIR}/#{DSO_BOOT} -Dtc.install-root=#{File.join(TARGET_DIR, TC_DIR)} -Dtc.server=#{SERVER_ADDR} -Dtc.config=#{TARGET_DIR}/tc-config.xml -DlocalDir=/var/www/html/logs -DhttpUrl=http://#{SERVER_ADDR}/html/logs/ -DdownloadedDir=#{DOWNLOADED_LOGS} -DcountWorkers=#{workers.length} -jar #{TARGET_DIR}/#{SCHEDULER} 2>&1\"\n" +
                    "end" +
                    "\n" +
                    "task :run_server, :roles => :server do\n" +
                    "  run \"export JAVA_HOME=#{JAVA_HOME} && #{File.join(TARGET_DIR, TC_DIR, \"bin\", \"start-tc-server.sh\")} \"\n" +//> /dev/null 2>&1 &\"\n" +
                    "end\n" +
                    "\n" +
                    "task :kill do\n" +
                    "  run \"killall -9 java\"\n" +
                    "end\n" +
                    "\n" +
                    "task :clean do\n" +
                    "  run \"rm -rf #{TARGET_DIR}\"\n" +
                    "end");

            FileWriter capFile = new FileWriter(this.pathApp + "Capfile");
            capFile.write(contentFile.toString());
            capFile.close();
        }
    }
}
