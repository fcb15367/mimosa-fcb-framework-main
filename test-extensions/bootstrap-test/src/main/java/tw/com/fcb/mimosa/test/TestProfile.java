package tw.com.fcb.mimosa.test;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.test.context.ActiveProfiles;

/** @author Matt Ho */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Inherited
@ActiveProfiles({ "test", "h2" })
public @interface TestProfile {
}
