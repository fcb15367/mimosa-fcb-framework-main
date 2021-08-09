package tw.com.fcb.mimosa.ext.data.jdbc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "mimosa.data.jdbc")
public class MimosaDataJdbcProperties {
}
