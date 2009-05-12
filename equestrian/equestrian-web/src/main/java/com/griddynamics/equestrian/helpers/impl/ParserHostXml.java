package com.griddynamics.equestrian.helpers.impl;

import com.griddynamics.equestrian.helpers.ParserHost;
import com.griddynamics.equestrian.helpers.ApplicationPath;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * @author: apanasenko aka dieu
 * Date: 08.05.2009
 * Time: 14:10:04
 */
public class ParserHostXml extends DefaultHandler implements ParserHost{
    private List<String> workersIp = new ArrayList<String>();
    private String serverIp = "";
    private String schedulerIp = "";
    private int n;

    public static void main(String[] arg) throws IOException, SAXException, ParserConfigurationException {
        new ParserHostXml().parse(1);
    }

    public ParserHostXml() {
    }

    public void parse(int n) throws SAXException, ParserConfigurationException, IOException {
        this.n = n;
        File hostLog = new File(ApplicationPath.HOST_LOG_PATH);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        sp.parse(hostLog, this);
        writeCapFile();
    }

    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes attrs) {
        if(rawName.equals("worker")) {
            if (attrs != null) {
                int len = attrs.getLength();
                for (int i = 0; i < len && workersIp.size() < n; i++) {
                    if(attrs.getQName(i).equals("ip")) {
                        workersIp.add(attrs.getValue(i));
                    }
                }
            }
        } else if(rawName.equals("server")) {
            if (attrs != null && serverIp.equals("")) {
                int len = attrs.getLength();
                for (int i = 0; i < len; i++) {
                    if(attrs.getQName(i).equals("ip")) {
                        serverIp = attrs.getValue(i);
                    }
                }
            }
        } else if(rawName.equals("scheduler")) {
            if (attrs != null && schedulerIp.equals("")) {
                int len = attrs.getLength();
                for (int i = 0; i < len; i++) {
                    if(attrs.getQName(i).equals("ip")) {
                        schedulerIp = attrs.getValue(i);
                    }
                }
            }
        }
    }

    private void writeCapFile() throws IOException {
        if(!serverIp.equals("") && !schedulerIp.equals("") && workersIp.size() != 0) {
            StringBuilder contentFile = new StringBuilder("");
            contentFile.append("SERVER_ADDR = \"" + serverIp + "\" \n");
            contentFile.append("JAVA_HOME = \"/usr/java/default\" \n\n");
            contentFile.append("role :workers, ");
            for(String ip: workersIp) {
                contentFile.append("\"" + ip + "\",");
            }
            contentFile.deleteCharAt(contentFile.length() - 1);
            contentFile.append(" \n");
            contentFile.append("role :scheduler, \"" + schedulerIp + "\" \n");
            contentFile.append("role :server, SERVER_ADDR\n" +
                    "\n" +
                    "set :user, 'bolivar_scheduler'\n" +
                    "set :password, '123456'\n" +
                    "\n" +
                    "EXAMPLE_DIR = \"tc-log-parser-example\"\n" +
                    "TARGET_DIR = \"bolivar\"\n" +
                    "NODE = \"node-unit.jar\"\n" +
                    "SCHEDULER = \"scheduler-unit.jar\"\n" +
                    "JARS = [NODE, SCHEDULER]\n" +
                    "DSO_BOOT = \"dso-boot.linux.java-6.12.jar\"\n" +
                    "TC_DIR = \"terracotta-2.7.2\"\n" +
                    "CONFIG = \"tc-config.xml\"\n" +
                    "MISC = \"misc\"\n" +
                    "\n" +
                    "def scp(src, dst)\n" +
                    "  body = File.open(src, \"rb\") { |f| f.read }\n" +
                    "  run \"mkdir -p #{TARGET_DIR}\"\n" +
                    "  put(body, File.join(TARGET_DIR, dst))\n" +
                    "end\n" +
                    "\n" +
                    "def target(dir)\n" +
                    "  File.join(TARGET_DIR, dir)\n" +
                    "end\n" +
                    "\n" +
                    "desc \"Build the project from sources\"\n" +
                    "task :build do\n" +
                    "  system(\"cd tc-log-parser-example && mvn clean package && mvn package -P Worker\")\n" +
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
                    "\n" +
                    "  # upload and unpack terracotta\n" +
                    "  tc_tar = TC_DIR + \".tar.gz\"\n" +
                    "  scp(File.join(MISC, tc_tar), tc_tar)\n" +
                    "  run \"cd #{TARGET_DIR} && tar xzf #{tc_tar}\"\n" +
                    "end\n" +
                    "\n" +
                    "task :run_workers, :roles => :workers do\n" +
                    "  run \"java -Xbootclasspath/p:#{TARGET_DIR}/#{DSO_BOOT} -Dtc.install-root=#{File.join(TARGET_DIR, TC_DIR)}  -Dtc.server=#{SERVER_ADDR} -Dtc.config=#{TARGET_DIR}/tc-config.xml -jar #{TARGET_DIR}/#{NODE} > /dev/null 2>&1 &\"\n" +
                    "end\n" +
                    "\n" +
                    "task :run_scheduler, :roles => :scheduler do\n" +
                    "  run \"java -Xbootclasspath/p:#{TARGET_DIR}/#{DSO_BOOT} -Dtc.install-root=#{File.join(TARGET_DIR, TC_DIR)}  -Dtc.server=#{SERVER_ADDR} -Dtc.config=#{TARGET_DIR}/tc-config.xml -DlocalDir=/var/www/html/logs -DhttpUrl=http://#{SERVER_ADDR}/logs/ -jar #{TARGET_DIR}/#{SCHEDULER} 2>&1 &\"\n" +
                    "end\n" +
                    "\n" +
                    "task :run_server, :roles => :server do\n" +
                    "  run \"export JAVA_HOME=#{JAVA_HOME} && #{File.join(TARGET_DIR, TC_DIR, \"bin\", \"start-tc-server.sh\")} > /dev/null 2>&1 &\"\n" +
                    "end\n" +
                    "\n" +
                    "task :kill do\n" +
                    "  run \"killall -9 java\"\n" +
                    "end\n" +
                    "\n" +
                    "task :clean do\n" +
                    "  run \"rm -rf #{TARGET_DIR}\"\n" +
                    "end");

            FileWriter capFile = new FileWriter(ApplicationPath.APPLICATION_PATH + "Capfile");
            capFile.write(contentFile.toString());
            capFile.close();
        }
    }
}
