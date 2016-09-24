/**
 * Created by jiaweizhang on 9/24/2016.
 */
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        port(8080);
        get("/test", (req, res) -> {
            byte[] encoded = Files.readAllBytes(Paths.get(new File("test.txt").getAbsolutePath()));
            return new String(encoded, Charset.defaultCharset());
        });

        post("/test", (req, res) -> {
            return "banana received";/*
            String body = req.body();
            StringBuilder sb = new StringBuilder(body);
            File f = new File("test.txt");
            try(FileWriter writer = new FileWriter(f.getAbsoluteFile())){
                writer.append(sb);
            }
            return "writtn";*/
        });
    }

}
