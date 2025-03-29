package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entity.AlarmEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AlarmEntityRepositoty extends JpaRepository<AlarmEntity, Integer> {

    Page<AlarmEntity> findAllByUser(UserEntity userEntity, Pageable pageable);

    /**
     * 해당 부분으로 수행하더라도 쿼리 두번식 수행 왜?
     * N+1 Query 수행 문제
     * 1. 잘못된 fetch FetchType.EAGER 전략 -> @ManyToOne(fetch = FetchType.LAZY) 로 하는 것이 궁극적인 해결은 아님
     * 2. AlarmEntity 상에 User를 사용하는 로직이 있기 떄문!
     *
     * => 쿼리를 직접적으로 작성하는 것이 궁극적인 해결입니다. 또는 필요없는 Entity는 제거하는 방식입니다.
     *
     */
    Page<AlarmEntity> findAllByUserId(Integer userId, Pageable pageable);
}
