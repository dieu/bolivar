cd tc-log-parcer-example &&
mvn clean package
mvn package -P Worker
cd ..
rm -rf archive
mkdir archive
cp tc-log-parcer-example/target/scheduler-unit.jar archive
cp tc-log-parcer-example/target/node-unit.jar archive
cp jars/*.sh archive
cp tc-log-parcer-example/tc-config.xml archive
cp jars/dso-boot.jar archive


