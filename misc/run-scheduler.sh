. env.sh
$JAVA_HOME/bin/java -Xbootclasspath/p:./dso-boot.jar -Dtc.install-root=$TC_HOME  -Dtc.server=$TC_SERVER_ADDR -Dtc.config=./tc-config.xml -DlocalDir=/var/www/html/logs -DhttpUrl=http://$TC_SERVER_ADDR/logs/ -jar sheduler-unit.jar
