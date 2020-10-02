
cat ~/.bash_profile



export CONFLUENT_HOME=/opt/kafka/confluent-5.5.1
export PATH="${CONFLUENT_HOME}/bin:$PATH"

cd $CONFLUENT_HOME/bin
confluent local start
confluent local status

kafka-topics --create --topic hello-producer --bootstrap-server localhost:9092
kafka-topics --create --topic employees --bootstrap-server 192.168.99.1:9092


kafka-console-producer --topic hello-producer --bootstrap-server 192.168.99.1:9092

kafka-console-consumer --topic hello-producer --from-beginning --bootstrap-server 192.168.99.1:9092