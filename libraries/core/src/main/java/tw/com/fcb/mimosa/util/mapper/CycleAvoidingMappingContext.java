package tw.com.fcb.mimosa.util.mapper;

import java.util.IdentityHashMap;
import java.util.Map;

import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

/**
 * 避免遞迴的關係造成無限迴圈 Mapping 的 mapstruct {@link org.mapstruct.Context}:
 *
 * <pre>
 * class Bar {
 *   Foo foo;
 * }
 *
 * class Foo {
 *   List<'Bar> bars;
 * }
 *
 * Foo foo =
 *    mapper.fromBar(bar, new CycleAvoidingMappingContext());
 * </pre>
 *
 * @author Matt Ho
 * @see <a
 *      href=
 *      "https://github.com/mapstruct/mapstruct-examples/tree/master/mapstruct-mapping-with-cycles">https://github.com/mapstruct/mapstruct-examples/tree/master/mapstruct-mapping-with-cycles</a>
 */
public class CycleAvoidingMappingContext {

  private Map<Object, Object> knownInstances = new IdentityHashMap<>();

  @BeforeMapping
  public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
    return (T) knownInstances.get(source);
  }

  @BeforeMapping
  public void storeMappedInstance(Object source, @MappingTarget Object target) {
    knownInstances.put(source, target);
  }
}
