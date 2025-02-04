package raisetech.Re.Student.Management.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("raisetech.Re.Student.Management.repository")
@ComponentScan(basePackages = "raisetech.Re.Student.Management")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
