build:
	go run protoGen.go
	export GOPATH=$HOME/go
	export PATH=$PATH:$GOPATH/bin
	protoc --go_out=. --go_opt=paths=source_relative SyncMsg/msg.proto Plugin/logger.proto
	protoc --js_out=import_style=commonjs,binary:. Plugin/logger.proto
	protoc --java_out=. ./Plugin/logger.proto
	go mod init counter
	go mod tidy
	go build server.go client.go counter.go file_reader.go pluginManager.go

plugin:
	# go run logger.go
	# to enable Rust logger, use Makefile inside Rust_logger/
	# node logger.js
	javac -cp .:Plugin/jar/protobuf-java-3.21.12.jar logger/Logger.java JavaLogger.java
	java -cp .:Plugin/jar/protobuf-java-3.21.12.jar JavaLogger

all:	build plugin
test:	build
	go test -v counter.go counter_test.go

clean:	
	@rm -rf server go.mod go.sum SyncMsg/msg.pb.go *.log *.class
	@rm -rf Plugin/logger.pb.go Plugin/*.proto Plugin/logger_pb.js logger/