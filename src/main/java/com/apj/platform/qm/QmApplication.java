package com.apj.platform.qm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@ComponentScan(basePackages = "com.apj.platform")
public class QmApplication {

	public static void main(String[] args) {
		SpringApplication.run(QmApplication.class, args);
	}

}
