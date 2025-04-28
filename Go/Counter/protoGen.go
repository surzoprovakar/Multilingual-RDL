package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"strings"
)

type ProtobufField struct {
	Name string `json:"name"`
	Type string `json:"type"`
}

type ProtobufMessage struct {
	Name   string            `json:"name"`
	Fields map[string]string `json:"fields"`
}

type PluginConfig struct {
	Syntax    string          `json:"syntax"`
	Package   string          `json:"package"`
	GoPackage string          `json:"go_package"`
	Message   ProtobufMessage `json:"message"`
}

func main() {

	fileData, err := ioutil.ReadFile("Plugin/logger.json")
	if err != nil {
		fmt.Printf("Error reading logger.json: %v\n", err)
		return
	}

	var config PluginConfig
	if err := json.Unmarshal(fileData, &config); err != nil {
		fmt.Printf("Error parsing logger.json: %v\n", err)
		return
	}

	generateProtobuf(config)
}

func generateProtobuf(config PluginConfig) {
	var protoBuilder strings.Builder

	protoBuilder.WriteString(fmt.Sprintf("syntax = \"%s\";\n\n", config.Syntax))
	protoBuilder.WriteString(fmt.Sprintf("package %s;\n", config.Package))
	protoBuilder.WriteString(fmt.Sprintf("option go_package = \"%s\";\n\n", config.GoPackage))

	protoBuilder.WriteString(fmt.Sprintf("message %s {\n", config.Message.Name))
	fieldNumber := 1
	for name, fieldType := range config.Message.Fields {
		protoBuilder.WriteString(fmt.Sprintf("    %s %s = %d;\n", fieldType, name, fieldNumber))
		fieldNumber++
	}
	protoBuilder.WriteString("}\n")

	protoFilename := "Plugin/logger.proto"
	err := ioutil.WriteFile(protoFilename, []byte(protoBuilder.String()), 0644)
	if err != nil {
		fmt.Printf("Error writing protobuf file: %v\n", err)
		return
	}

	fmt.Printf("Generated protobuf file: %s\n", protoFilename)
}
