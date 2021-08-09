package tw.com.fcb.mimosa.examples.jpa;

import static org.mapstruct.CollectionMappingStrategy.TARGET_IMMUTABLE;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/** @author Jason Wu */
@Mapper(collectionMappingStrategy = TARGET_IMMUTABLE, nullValueMappingStrategy = RETURN_DEFAULT, builder = @Builder)
public interface UserMapper {

  UserEntity toEntity(UserRequest userRequest);

  UserDto fromEntity(UserEntity entity);

  UserEntity copy(ModifiedUser source, @MappingTarget UserEntity target);

}
