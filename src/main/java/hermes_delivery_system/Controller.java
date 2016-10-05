package hermes_delivery_system;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by jiaweizhang on 10/4/16.
 */

@RestController
public class Controller {

    @RequestMapping(value = "/write/{fileName}",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity write(@PathVariable(value = "fileName") String fileName,
                                @RequestBody final String body) {
        System.out.println("\nRequest received");
        long received = System.currentTimeMillis();
        File f = new File(fileName + ".txt");

        Thread t = new Thread(() -> {
            try {
                FileWriter writer = new FileWriter(f.getAbsoluteFile());
                long beginTime = System.currentTimeMillis();
                writer.append(body);
                long endTime = System.currentTimeMillis();
                System.out.println("Writing: " + (endTime - beginTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Async write is complete");
        });
        t.setDaemon(true);
        t.start();

        long returned = System.currentTimeMillis();
        System.out.println("Total  : " + (returned - received));
        System.out.println("Returning\n");
        return ResponseEntity.ok(fileName + " received");
    }
}
