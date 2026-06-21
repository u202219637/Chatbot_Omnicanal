//package pe.edu.upc.shadowchat;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class ShadowchatApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(ShadowchatApplication.class, args);
//    }
//
//}


package pe.edu.upc.shadowchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShadowchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShadowchatApplication.class, args);
    }

}