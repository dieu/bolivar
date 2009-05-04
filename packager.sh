cd tc-log-parser-example &&
mvn clean package
mvn package -P Worker
cd ..
rm -rf archive
mkdir archive
cp tc-log-parser-example/target/scheduler-unit.jar archive
cp tc-log-parser-example/target/node-unit.jar archive
cp misc/*.sh archive
cp tc-log-parser-example/tc-config.xml archive
cp misc/dso-boot.jar archive


