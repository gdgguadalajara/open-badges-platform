package com.gdgguadalajara;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Open Badges Platform API", version = "1.0.0", description = "API para la emisión y gestión de insignias digitales del GDG Guadalajara", contact = @Contact(name = "Soporte", email = "contacto@gdgguadalajara.com")))
public class BadgesApplication extends Application {

}
