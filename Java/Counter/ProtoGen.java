import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class ProtoGen {

    public static void main(String[] args) {
        try {
            String data = new String(Files.readAllBytes(Paths.get("Plugin/logger.json")));
            JSONObject config = new JSONObject(data);
            generateProtobuf(config);
        } catch (IOException e) {
            System.out.println("Error reading logger.json: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error parsing logger.json: " + e.getMessage());
        }
    }

    private static void generateProtobuf(JSONObject config) {
        StringBuilder protoBuilder = new StringBuilder();

        protoBuilder.append("syntax = \"").append(config.getString("syntax")).append("\";\n\n");
        protoBuilder.append("package ").append(config.getString("package")).append(";\n");
        protoBuilder.append("option go_package = \"").append(config.getString("go_package")).append("\";\n\n");

        JSONObject message = config.getJSONObject("message");
        protoBuilder.append("message ").append(message.getString("name")).append(" {\n");

        JSONObject fields = message.getJSONObject("fields");
        int fieldNumber = 1;
        for (String fieldName : fields.keySet()) {
            String fieldType = fields.getString(fieldName);
            protoBuilder.append("    ").append(fieldType).append(" ").append(fieldName)
                    .append(" = ").append(fieldNumber).append(";\n");
            fieldNumber++;
        }
        protoBuilder.append("}\n");

        String protoFilename = "Plugin/logger.proto";
        try {
            Files.write(Paths.get(protoFilename), protoBuilder.toString().getBytes());
            System.out.println("Generated protobuf file: " + protoFilename);
        } catch (IOException e) {
            System.out.println("Error writing protobuf file: " + e.getMessage());
        }
    }
}
