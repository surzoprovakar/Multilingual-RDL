build:
	javac -cp .:jar/json-20210307.jar ProtoGen.java
	java -cp .:jar/json-20210307.jar ProtoGen
	protoc --java_out=. ./SyncMsg/msg.proto ./Plugin/logger.proto
	protoc --go_out=. --go_opt=paths=source_relative Plugin/logger.proto
	protoc --js_out=import_style=commonjs,binary:. Plugin/logger.proto
	javac -cp .:jar/junit-platform-console-standalone-1.9.2.jar:jar/protobuf-java-3.21.12.jar counter/Msg.java Server.java Client.java FileReader.java Counter.java CounterTest.java logger/Logger.java PluginManager.java

plugin:
	# javac -cp .:jar/protobuf-java-3.21.12.jar logger/Logger.java JavaLogger.java
	# java -cp .:jar/protobuf-java-3.21.12.jar JavaLogger
	# export GOPATH=$HOME/go
	# export PATH=$PATH:$GOPATH/bin
	# go mod init counter
	# go mod tidy
	# go run logger.go
	node logger.js

all: build plugin
test:
	java -jar jar/junit-platform-console-standalone-1.9.2.jar --class-path .:jar/protobuf-java-3.21.12.jar --scan-class-path

clean:
	@rm -rf *.class counter/ .idea/ *.iml *.log go.mod go.sum
	@rm -rf Plugin/logger.pb.go Plugin/*.proto Plugin/logger_pb.js logger/