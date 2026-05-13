package de.teamplaner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TeamPlanerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamPlanerApplication.class, args);
    }
}
