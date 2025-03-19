package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entity.AlarmEntity;
import com.fastcampus.sns.model.entity.LikeEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlarmEntityRepositoty extends JpaRepository<AlarmEntity, Integer> {

    Page<AlarmEntity> findAllByUser(UserEntity userEntity, Pageable pageable);
}
