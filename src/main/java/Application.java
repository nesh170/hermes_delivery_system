/**
 * Created by jiaweizhang on 9/24/2016.
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        port(8080);

        post("/write/:fileName", (req, res) -> {
            System.out.println("Request received: \t\t" + System.currentTimeMillis());
            String fileName = req.params(":fileName");
            File f = new File(fileName + ".txt");
            String body = req.body();
            //System.out.println(body.length());
            System.out.println("Loaded into mem: \t\t" + System.currentTimeMillis());

            Thread t = new Thread(() -> {
                try {
                    FileWriter writer = new FileWriter(f.getAbsoluteFile());
                    long beginTime = System.currentTimeMillis();
                    System.out.println("Starting write: \t\t" + beginTime);
                    writer.append(body);
                    long endTime = System.currentTimeMillis();
                    System.out.println("Ending write: \t\t\t" + endTime);
                    //System.out.println("Milliseconds writing: " + (endTime - beginTime));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Async write is complete");
            });
            t.setDaemon(true);
            t.start();
            System.out.println("Returning ack signal: \t" + System.currentTimeMillis());
            return fileName + " received";
        });


        get("/test", (req, res) -> {
            byte[] encoded = Files.readAllBytes(Paths.get(new File("test.txt").getAbsolutePath()));
            return new String(encoded, Charset.defaultCharset());
        });

        get("/dbup", (req, res) -> {
            String query = readQuery("setup.sql");
            Connection c = setupDatabase();
            Statement s = c.createStatement();
            s.execute(query);
            return "{ \"success\": true }";
        });

        post("/test", (req, res) -> {
            String body = req.body();

            StringBuilder sb = new StringBuilder(body);
            long unixTime = System.currentTimeMillis() / 1000L;

            File f = new File("sonar_" + unixTime + ".txt");
            try (FileWriter writer = new FileWriter(f.getAbsoluteFile())) {
                writer.append(sb);
                return "{ \"success\": true }";
            } catch (Exception e) {
                return "{ \"success\": false }";
            }
        });


    }

    private static String readQuery(String file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String str;
        StringBuilder sb = new StringBuilder();
        while ((str = in.readLine()) != null) {
            int index;
            // removes single-line comments
            if ((index = str.indexOf("--")) >= 0) {
                str = str.substring(0, index);
            }
            sb.append(str).append(" ");
        }
        in.close();

        return sb.toString();
    }

    public static Connection setupDatabase() {
        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return null;

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/testdb", "postgres",
                    "password");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
            return connection;
        } else {
            System.out.println("Failed to make connection!");
        }
        return null;
    }
}
