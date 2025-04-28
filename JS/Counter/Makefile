build:
	node protoGen.js
	protoc --js_out=import_style=commonjs,binary:. SyncMsg/msg.proto Plugin/logger.proto
	protoc --go_out=. --go_opt=paths=source_relative Plugin/logger.proto
	protoc --java_out=. ./Plugin/logger.proto

plugin:
	# node logger.js
	# export GOPATH=$HOME/go
	# export PATH=$PATH:$GOPATH/bin
	# go mod init counter
	# go mod tidy
	# go run logger.go
	javac -cp .:Plugin/jar/protobuf-java-3.21.12.jar logger/Logger.java JavaLogger.java
	java -cp .:Plugin/jar/protobuf-java-3.21.12.jar JavaLogger

all:	build plugin
test:
	npm test

clean:
	@rm -rf SyncMsg/msg_pb.js Plugin/logger.pb.go Plugin/*.proto Plugin/logger_pb.js
	@rm -rf go.mod go.sum *.log *.class logger/