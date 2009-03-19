SERVER_ADDR = "192.168.18.59"
JAVA_HOME = "/usr/lib/jvm/java-6-sun"

role :workers, "192.168.18.45", "192.168.18.46"
role :scheduler, "192.168.18.59"
role :server, SERVER_ADDR

set :user, 'guest'
set :password, 'guest'

EXAMPLE_DIR = "tc-log-parser-example"
TARGET_DIR = "bolivar"
NODE = "node-unit.jar"
SCHEDULER = "scheduler-unit.jar"
JARS = [NODE, SCHEDULER]
DSO_BOOT = "dso-boot.jar"
TC_DIR = "terracotta-2.7.2"
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
  run "java -Xbootclasspath/p:#{TARGET_DIR}/dso-boot.jar -Dtc.install-root=#{File.join(TARGET_DIR, TC_DIR)}  -Dtc.server=#{SERVER_ADDR} -Dtc.config=#{TARGET_DIR}/tc-config.xml -jar #{TARGET_DIR}/#{NODE} > /dev/null 2>&1 &"
end

task :run_scheduler, :roles => :scheduler do
  run "java -Xbootclasspath/p:#{TARGET_DIR}/dso-boot.jar -Dtc.install-root=#{File.join(TARGET_DIR, TC_DIR)}  -Dtc.server=#{SERVER_ADDR} -Dtc.config=#{TARGET_DIR}/tc-config.xml -DlocalDir=/var/www/html/logs -DhttpUrl=http://#{SERVER_ADDR}/logs/ -jar #{TARGET_DIR}/#{SCHEDULER} &"
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
