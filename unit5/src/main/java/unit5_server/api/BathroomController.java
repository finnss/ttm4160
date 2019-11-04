package main.java.unit5_server.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BathroomController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/bathroomTest")
    public Bathroom bathroomTest(@RequestParam(value="name", defaultValue="World") String name) {
        return new Bathroom("someName", 3, (HashMap) Map.of("someLat", "someLong"));
    }
}