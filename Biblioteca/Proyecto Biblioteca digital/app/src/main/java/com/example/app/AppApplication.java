package com.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.example.usuarios",
		"com.example.recursos",
		"com.example.ejemplares",
		"com.example.prestamos",
		"com.example.reservas"
})
@EnableJpaRepositories(basePackages = {
		"com.example.usuarios.repository",
		"com.example.recursos.repository",
		"com.example.ejemplares.repository",
		"com.example.prestamos.repository",
		"com.example.reservas.repository"
})
@EntityScan(basePackages = {
		"com.example.usuarios.entities",
		"com.example.recursos.entities",
		"com.example.ejemplares.entities",
		"com.example.prestamos.entities",
		"com.example.reservas.entities"
})
public class AppApplication {
	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}
}