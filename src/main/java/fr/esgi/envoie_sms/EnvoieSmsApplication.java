package fr.esgi.envoie_sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "fr.esgi.envoie_sms.repository")
@EntityScan(basePackages = "fr.esgi.envoie_sms.model")
public class EnvoieSmsApplication {
	public static void main(String[] args) {
		SpringApplication.run(EnvoieSmsApplication.class, args);
	}
}