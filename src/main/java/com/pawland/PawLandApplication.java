package com.pawland;

import com.pawland.global.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({AppConfig.class})
public class PawLandApplication {

	public static void main(String[] args) {
		SpringApplication.run(PawLandApplication.class, args);
	}

}
