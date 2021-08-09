package tw.com.fcb.mimosa.ext.openapi;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Maps;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@ConfigurationProperties(prefix = "mimosa.openapi")
public class MimosaOpenApiProperties {

  String title;
  String description;
  String termsOfService;
  Contact contact;
  License license;
  String version;

  @Singular
  Map<String, Object> extensions = Maps.newHashMap();
}
