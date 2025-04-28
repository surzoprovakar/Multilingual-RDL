build:
	go build ReplicaGo.go

run:
	sudo ./ReplicaGo

requirements:
	sudo apt-get install wiringpi
	go get github.com/stianeikeland/go-rpio
	go get github.com/d2r2/go-dht

js:
	node ReplicaJS.js

java-build:
	javac -cp .:sqlite-jdbc-3.41.2.2.jar ReplicaJava.java
java-run:
	java -cp .:sqlite-jdbc-3.41.2.2.jar ReplicaJava

clean:
	rm *.class *.db