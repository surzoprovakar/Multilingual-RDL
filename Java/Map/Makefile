build:
	protoc --java_out=. ./SyncMsg/msg.proto
	javac -cp .:jar/junit-platform-console-standalone-1.9.2.jar:jar/protobuf-java-3.21.12.jar map/Msg.java Server.java Client.java FileReader.java MapCRDT.java MapCRDTTest.java

test:
	java -jar jar/junit-platform-console-standalone-1.9.2.jar --class-path .:jar/protobuf-java-3.21.12.jar --scan-class-path

clean:
	rm -rf *.class map/