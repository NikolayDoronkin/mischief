package inc.mischief.mischief;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "DIPLOM \"MISCHIEF\". By Nikolay Doronkin Mejia, BSUIR, 951005"))
public class MischiefApplication {

	public static void main(String[] args) {
		SpringApplication.run(MischiefApplication.class, args);
	}

}
