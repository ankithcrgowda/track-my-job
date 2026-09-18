package com.trackmyjob.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration.
 *
 * @OpenAPIDefinition — defines the top-level API metadata:
 *   info    = title, description, version, contact
 *   servers = where the API is hosted (local, prod)
 *   security = applies JWT auth globally to all endpoints
 *
 * @SecurityScheme — defines HOW authentication works.
 *   We tell Swagger: "there's a Bearer token scheme,
 *   passed in the Authorization HTTP header."
 *   This is what makes the 🔒 Authorize button appear in Swagger UI.
 */

@Configuration
@OpenAPIDefinition (
		info = @Info(
				title = "TrackMyJob API",
				description = """
				A production-grade REST API for tracking job applications.
				## Authentication
					1. Register via `POST /api/auth/register`
					2. Login via `POST /api/auth/login` \s
					3. Copy the `token` from the response
					4. Click the **Authorize** button above and enter: `Bearer <token>`
					5. All protected endpoints will now work
				""",
				version = "1.0.0",
				contact = @Contact(
						name = "Ankith C R",
						email = "ankithcr13@gmail.com"
				)
		),
		servers = {
				@Server(url = "http://localhost:8080", description = "Local Development")
		},
		security = @SecurityRequirement(name = "bearerAuth")
		// Applies JWT auth requirement to ALL endpoints globally.
		// Individual public endpoints (register/login) override this.
)

@SecurityScheme(
		name = "bearerAuth",  // must match the name in @SecurityRequirement
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT",   // purely informational label in the UI
		in = SecuritySchemeIn.HEADER,
		description = "JWT token. Get it from /api/auth/login or /api/auth/register"
)
public class SwaggerConfig {

//	Methods are not needed, All the configurations are done in above Annotations

}
