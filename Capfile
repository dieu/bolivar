SERVER_ADDR = "ec2-67-202-3-233.compute-1.amazonaws.com"
JAVA_HOME = "/usr/lib/jvm/java-6-sun"

role :workers, "ec2-67-202-26-214.compute-1.amazonaws.com"
#"ec2-67-202-19-159.compute-1.amazonaws.com", "ec2-174-129-173-47.compute-1.amazonaws.com" 
role :scheduler, SERVER_ADDR
role :server, SERVER_ADDR

set :user, 'agorbunov'
set :password, '123456'

EXAMPLE_DIR = "tc-log-parser-example"
TARGET_DIR = "bolivar"
NODE = "node-unit.jar"
SCHEDULER = "scheduler-unit.jar"
JARS = [NODE, SCHEDULER]
DSO_BOOT = "dso-boot.linux.java-6.10.jar"
TC_DIR = "terracotta-3.0.0"
CONFIG = "tc-config.xml"
MISC = "misc"

def scp(src, dst)
  body = File.open(src, "rb") { |f| f.read }
  run "mkdir -p #{TARGET_DIR}"
  put(body, File.join(TARGET_DIR, dst))
end

def target(dir)
  File.join(TARGET_DIR, dir)
end

desc "Build the project from sources"
task :build do
  system("cd tc-log-parser-example && mvn clean package && mvn package -P Worker")
end

desc "Uploads tc-config.xml to all nodes"
task :upload_config do
  scp(File.join(EXAMPLE_DIR, CONFIG), CONFIG)
end

task :upload_jars do
  JARS.each { |jar| scp(File.join(EXAMPLE_DIR, "target", jar), jar) }
  scp(File.join(MISC, DSO_BOOT), DSO_BOOT)
end

task :upload_tc do
  tc_tar = TC_DIR + ".tar.gz"
  scp(File.join(MISC, tc_tar), tc_tar)
  run "cd #{TARGET_DIR} && tar xzf #{tc_tar}"
end

task :upload_all do
  # upload config
  scp(File.join(EXAMPLE_DIR, CONFIG), CONFIG)

  # upload jars
  JARS.each { |jar| scp(File.join(EXAMPLE_DIR, "target", jar), jar) }
  scp(File.join(MISC, DSO_BOOT), DSO_BOOT)

  # upload and unpack terracotta
  tc_tar = TC_DIR + ".tar.gz"
  scp(File.join(MISC, tc_tar), tc_tar)
  run "cd #{TARGET_DIR} && tar xzf #{tc_tar}"
end

task :run_workers, :roles => :workers do
  run "java -Xms512m -Xmx512m -Xbootclasspath/p:#{TARGET_DIR}/#{DSO_BOOT} -Dtc.install-root=#{File.join(TARGET_DIR, TC_DIR)} -Dtc.server=#{SERVER_ADDR} -Dtc.config=#{TARGET_DIR}/tc-config.xml -jar #{TARGET_DIR}/#{NODE} 2>&1"
end

task :run_scheduler, :roles => :scheduler do
  run "java -Xbootclasspath/p:#{TARGET_DIR}/#{DSO_BOOT} -Dtc.install-root=#{File.join(TARGET_DIR, TC_DIR)} -Dtc.server=#{SERVER_ADDR} -Dtc.config=#{TARGET_DIR}/tc-config.xml -DlocalDir=/var/www/html/logs -DhttpUrl=http://#{SERVER_ADDR}/html/logs/ -DdownloadedDir=downloaded-logs -jar #{TARGET_DIR}/#{SCHEDULER} 2>&1"
end

task :run_server, :roles => :server do
  run "export JAVA_HOME=#{JAVA_HOME} && #{File.join(TARGET_DIR, TC_DIR, "bin", "start-tc-server.sh")} > /dev/null 2>&1 &"
end

task :kill do
  run "killall -9 java"
end

task :clean do
  run "rm -rf #{TARGET_DIR}"
end
