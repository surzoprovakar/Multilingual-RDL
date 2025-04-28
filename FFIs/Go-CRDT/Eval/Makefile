go:
	go run API.go

js-build:
	GOOS=js GOARCH=wasm go build -o go-api.wasm
	cp /usr/local/go/misc/wasm/wasm_exec.js .
js-run:
	node JSFFI.js

java-build:
	go build -o libgoApi.so -buildmode=c-shared API.go
	javac -cp .:jna-jpms-5.14.0.jar JavaFFI.java
java-run:
	java -cp .:jna-jpms-5.14.0.jar JavaFFI

clean:
	@rm -rf *.wasm wasm_exec.js *.h *.so *.class


# Eval-Command
# /usr/bin/time -v go run fileName 2>&1 | awk '/Elapsed \(wall clock\) time/ {print "Real time: " $8} /Maximum resident set size/ {print "Memory usage: " $6/1024 " MB"}'