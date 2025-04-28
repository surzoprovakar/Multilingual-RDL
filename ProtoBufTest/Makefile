go:
	@export GOPATH=$HOME/go
	@export PATH=$PATH:$GOPATH/bin
	protoc --go_out=. --go_opt=paths=source_relative proto/example.proto
	@go mod init example
	@go mod tidy
	go run goproto.go

js:
	protoc --js_out=import_style=commonjs,binary:. proto/example.proto
	node jsproto.js

java:
	protoc --java_out=. ./proto/example.proto
	javac -cp .:protobuf-java-3.21.12.jar example/Example.java javaproto.java
	java -cp .:protobuf-java-3.21.12.jar javaproto

clean:
	@rm -rf go.mod go.sum proto/example.pb.go proto/example_pb.js example/ javaproto.class example_*