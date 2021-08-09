package tw.com.fcb.mimosa.ext.security.auth;

import static org.mapstruct.CollectionMappingStrategy.TARGET_IMMUTABLE;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;
import static tw.com.fcb.mimosa.ext.security.auth.ValidateResponse.SUCCESS_RTN_CODE;

import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import tw.com.fcb.mimosa.ext.security.SimpleUser;

/** @author Jason Wu */
@Mapper(collectionMappingStrategy = TARGET_IMMUTABLE, nullValueMappingStrategy = RETURN_DEFAULT, builder = @Builder)
public interface AuthMapper {

  SimpleUser toSimpleUser(ValidateResponse validateResponse);

  @AfterMapping
  default void afterMapping(ValidateResponse source, @MappingTarget SimpleUser.SimpleUserBuilder target) {
    target.username(source.getCustId());
    target.enabled(SUCCESS_RTN_CODE.equalsIgnoreCase(source.getRtnCode()));
  }
}
