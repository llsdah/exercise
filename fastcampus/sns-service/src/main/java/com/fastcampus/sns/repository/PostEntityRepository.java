package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {

    //PK는 무조건 index가 걸려있다. 성능상 큰 상관이 없다. 아닌경우 인덱스가 필요하다.
    Page<PostEntity> findAllByUser(UserEntity userEntity, Pageable pageable);
}
