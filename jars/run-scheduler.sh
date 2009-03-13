. env.sh
java -Xbootclasspath/p:./dso-boot.jar -Dtc.install-root=$TC_HOME  -Dtc.server=localhost -Dtc.config=./tc-config.xml -jar sheduler.jar -DlocalDir=/var/www/logs2 -DhttpUrl=http://192.168.18.59/logs2/
