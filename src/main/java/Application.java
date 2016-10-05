/*
 * Created by jiaweizhang on 10/5/2016.
 * https://github.com/tipsy/spark-file-upload/blob/master/src/main/java/UploadExample.java
 */


import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        port(8080);

        File uploadDir = new File("upload");
        uploadDir.mkdir(); // create the upload directory if it doesn't exist

        staticFiles.externalLocation("upload");

        get("/", (req, res) ->
                "<form method='post' enctype='multipart/form-data'>" // note the enctype
                        + "    <input type='file' name='uploaded_file' accept='.txt'>" // make sure to call getPart using the same "name" in the post
                        + "    <button>Upload file</button>"
                        + "</form>"
        );

        post("/write/:fileName", (req, res) -> {
            System.out.println("\nReceived request");
            long receivedRequest = System.currentTimeMillis();
            String fileName = req.params(":fileName");

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
                File f = new File(uploadDir.getAbsolutePath() + "/" + fileName + ".txt");
                long startWriting = System.currentTimeMillis();
                Files.copy(input, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
                long finishedWriting = System.currentTimeMillis();
                System.out.println("Writing time: " + (finishedWriting - startWriting));
            } catch (Exception e) {
                e.printStackTrace();
            }

            long returningRequest = System.currentTimeMillis();
            System.out.println("Receive to Return: " + (returningRequest - receivedRequest));
            return "Success";
        });
    }
}
