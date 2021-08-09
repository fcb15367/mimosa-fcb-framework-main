package tw.com.fcb.mimosa.examples;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/** @author Jason Wu */
@RequiredArgsConstructor
public class UserMyBatisRepositoryImpl implements UserMyBatisRepository {

  static final String NAMESPACE = UserEntity.class.getName() + "Mapper";

  final SqlSession sqlSession;

  @Override
  public List<UserEntity> findByCriteria(UserCriteria criteria, Pageable pageable) {
    var parameter = Map.of("criteria", criteria, "pageable", pageable);
    return sqlSession.selectList(NAMESPACE + ".findByCriteria", parameter);
  }

  @Override
  public long countByCriteria(UserCriteria criteria) {
    return sqlSession.selectOne(NAMESPACE + ".countByCriteria", criteria);
  }
}
