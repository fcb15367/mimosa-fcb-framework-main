package tw.com.fcb.mimosa.ext.ddd.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.ext.ddd.application.UseCaseConfiguration;

/**
 * Mimosa DDD ext 自動配置的進入點
 *
 * @author Matt Ho
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Import(UseCaseConfiguration.class)
@EnableConfigurationProperties(MimosaDddProperties.class)
public class MimosaDddAutoConfiguration {
}
