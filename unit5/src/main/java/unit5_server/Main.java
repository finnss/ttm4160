package main.java.unit5_server;

import main.java.unit5_server.server.ServerStateMachine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        //ServerStateMachine stateMachine = new ServerStateMachine();
        //stateMachine.run();
        SpringApplication.run(Main.class, args);
    }
}
