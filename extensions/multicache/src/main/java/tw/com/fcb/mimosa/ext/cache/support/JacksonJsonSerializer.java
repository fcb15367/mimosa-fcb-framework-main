package tw.com.fcb.mimosa.ext.cache.support;

import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import lombok.SneakyThrows;

/** @author Matt Ho */
public class JacksonJsonSerializer implements RedisSerializer<Object> {

  private final ObjectMapper mapper;

  public JacksonJsonSerializer() {
    this(new ObjectMapper());
  }

  public JacksonJsonSerializer(ObjectMapper mapper) {
    this.mapper = mapper;
    registerNullValueSerializer(mapper);
    mapper
        .findAndRegisterModules()
        .setVisibility(
            mapper
                .getVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE))
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.EVERYTHING,
            JsonTypeInfo.As.PROPERTY)
        .setTimeZone(TimeZone.getDefault());
  }

  public static void registerNullValueSerializer(ObjectMapper objectMapper) {
    GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, "@class");
  }

  @Override
  public byte[] serialize(Object o) throws SerializationException {
    return Optional.ofNullable(o)
        .map(
            source -> {
              try {
                return mapper.writeValueAsBytes(source);
              } catch (JsonProcessingException e) {
                throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
              }
            })
        .orElseGet(() -> new byte[0]);
  }

  @SneakyThrows
  @Override
  public Object deserialize(byte[] source) {
    if (Objects.isNull(source)) {
      return null;
    }
    return mapper.readValue(source, Object.class);
  }
}
