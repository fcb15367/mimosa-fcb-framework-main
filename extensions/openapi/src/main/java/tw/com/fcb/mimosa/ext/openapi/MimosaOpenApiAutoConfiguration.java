package tw.com.fcb.mimosa.ext.openapi;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import javax.annotation.PostConstruct;

import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.converters.models.MonetaryAmount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(MimosaOpenApiProperties.class)
@Slf4j
public class MimosaOpenApiAutoConfiguration {

  private final Environment environment;
  private final MimosaOpenApiProperties properties;

  @PostConstruct
  public void init() {
    SpringDocUtils.getConfig()
        .replaceWithSchema(
            MonetaryAmount.class,
            new ObjectSchema()
                .addProperties("amount", new NumberSchema())
                .example(0)
                .addProperties("currency", new StringSchema().example("TWD")));
  }

  // HTTP Bearer, see:
  // https://swagger.io/docs/specification/authentication/bearer-authentication/
  @Bean
  @ConditionalOnProperty("mimosa.security.enabled")
  public OpenAPI securedOpenAPI() {
    return new OpenAPI()
        .components(
            new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme().type(HTTP).scheme("bearer")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .info(info());
  }

  @Bean
  @ConditionalOnMissingBean
  public OpenAPI openAPI() {
    return new OpenAPI().components(new Components()).info(info());
  }

  private Info info() {
    return new Info()
        .description(properties.description)
        .termsOfService(properties.termsOfService)
        .contact(properties.contact)
        .license(properties.license)
        .version(properties.version)
        .title(
            ofNullable(properties.title)
                .filter(not(StringUtils::isEmpty))
                .orElseGet(() -> environment.getProperty("spring.application.name") + " API"));
  }

  @Configuration
  @ConditionalOnProperty(value = "springdoc.swagger-ui.enabled", matchIfMissing = true)
  static class SwaggerUiConfiguration implements WebMvcConfigurer {

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerPath;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
      registry.addRedirectViewController("/", swaggerPath);
    }
  }
}
