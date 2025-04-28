import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

class AmbientData {
    private String timestamp;
    private double temperature;
    private double humidity;

    public AmbientData(String timestamp, double temperature, double humidity) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }
}

public class ReplicaJava {
    private static final String JDBC_URL = "jdbc:sqlite:ambientData.db";
    private static final String JDBC_USER = "";
    private static final String JDBC_PASSWORD = "";

    public static void main(String[] args) {
        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Set<AmbientData> ambientDataSet = new HashSet<>();

        // Adding some sample data
        ambientDataSet.add(new AmbientData("2024-08-20T10:00:00Z", 30.0, 75.0));
        ambientDataSet.add(new AmbientData("2024-08-20T11:00:00Z", 22.0, 45.0));
        ambientDataSet.add(new AmbientData("2024-08-20T12:00:00Z", 15.0, 85.0));
        ambientDataSet.add(new AmbientData("2024-08-20T13:00:00Z", 10.0, 50.0));
        ambientDataSet.add(new AmbientData("2024-08-20T14:00:00Z", 35.0, 30.0));

        // Create database and table
        CreateDatabase();

        // Persist data to the database
        SaveAmbientData(ambientDataSet);
    }

    private static void CreateDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS AmbientData ("
                                + "timestamp VARCHAR(255) PRIMARY KEY, "
                                + "temperature DOUBLE, "
                                + "humidity DOUBLE);";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {

            preparedStatement.execute();
            System.out.println("Database and table created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void SaveAmbientData(Set<AmbientData> ambientDataSet) {
        String insertSQL = "INSERT INTO AmbientData (timestamp, temperature, humidity) VALUES (?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            for (AmbientData data : ambientDataSet) {
                preparedStatement.setString(1, data.getTimestamp());
                preparedStatement.setDouble(2, data.getTemperature());
                preparedStatement.setDouble(3, data.getHumidity());
                preparedStatement.executeUpdate();
            }

            System.out.println("Data saved to the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
