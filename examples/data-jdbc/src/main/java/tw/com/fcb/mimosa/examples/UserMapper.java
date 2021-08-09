package tw.com.fcb.mimosa.examples;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.CollectionMappingStrategy.TARGET_IMMUTABLE;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

/** @author Jason Wu */
@Mapper(collectionMappingStrategy = TARGET_IMMUTABLE, nullValueMappingStrategy = RETURN_DEFAULT, builder = @Builder)
public interface UserMapper {

  UserEntity toEntity(UserRequest userRequest);

  UserDto fromEntity(UserEntity entity);

  UserEntity copy(ModifiedUser source, @MappingTarget UserEntity target);

}
