package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entity.LikeEntity;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {
    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    List<LikeEntity> findAllByPost(PostEntity post); // 갯수만 가지고 올 수 있도록 수정

    // select count(*) from "like" where post_fid = 2
    //@Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post =:post")
    //Integer countByPost(@Param("post")PostEntity post);

    // FIXME 위의 갯수 조회 하는 것 , JPA 제공
    long countByPost(PostEntity post);

    // FIXME JPA 영속성 관리 ( lifecycle 관리 )
    // 데이터 조회 후 삭제 합니다. 즉, 비효율적 입니다. =. JPA 메소드 비 추춘
    @Transactional // 전체 삭제를 위함.
    @Modifying // UPDATE 때문에 달아 줌
    @Query("UPDATE LikeEntity entity SET deletedAt = CURRENT_TIMESTAMP where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity post);

}