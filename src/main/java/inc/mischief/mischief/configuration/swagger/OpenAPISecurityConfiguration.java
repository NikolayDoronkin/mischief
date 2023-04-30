package inc.mischief.mischief.configuration.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
		info =@Info(
				title = "User API",
				version = "${api.version}",
				contact = @Contact(
						name = "Baeldung", email = "user-apis@baeldung.com", url = "https://www.baeldung.com"
				),
				license = @License(
						name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
				),
				termsOfService = "${tos.uri}",
				description = "${api.description}"
		),
		servers = @Server(
				url = "${api.server.url}",
				description = "Production"
		)
)
public class OpenAPISecurityConfiguration {
}
