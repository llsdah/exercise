package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SNSApplicationException;
import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.repository.AlarmEntityRepositoty;
import com.fastcampus.sns.repository.UserCacheRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.util.JwtTokenUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
//public class UserService extends UserDetailsService {
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final AlarmEntityRepositoty alarmEntityRepositoty;
    private final BCryptPasswordEncoder encoder;
    private final UserCacheRepository userCacheRepository;

    @Value("${jwt.secret.key}")
    private String key;

    @Value("${jwt.token.expired.time}")
    private long expiredTime;


    // join 수행시 exception 발생경우 롤백이 자동됩니다.
    @Transactional
    public User join(String userName, String password){
        log.debug("UserService Start");
        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SNSApplicationException(ErrorCode.DUPLICATED_USER_NAME,String.format("%s is duplicated", userName));
        });

        // 회원가입 진행 = user 등록;
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName,encoder.encode(password)));

        log.debug("UserService End");
        //throw new RuntimeException();
        //throw new SNSApplicationException(ErrorCode.TEMP, "transaction test");
        return User.fromEntity(userEntity);
    }

    // TODO : implement, JWT 사용
    public String login(String userName, String password){

        // 아래와 다르게 redis 사용하는 것으로 확인

        User user = loadUserByUserName(userName);
        userCacheRepository.setUser(user);
        if (!encoder.matches(password, user.getPassword())) {
            throw new SNSApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        /**
        // 회원가입 여부 체크 
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(
                () -> new SNSApplicationException(ErrorCode.USER_NOT_FOUNDES,String.format("%s not founded",userName)));

        // 비밀번호 체크
        //if (!userEntity.getPassword().equals(password)) {
        if (!encoder.matches(password, userEntity.getPassword())) {
            throw new SNSApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        */
        // 토큰 생성 반환
        String token = JwtTokenUtils.generateToken(userName, key, expiredTime);

        log.debug("UserService End token : {}, key : {}, expiredTime : {}",token, key, expiredTime);

        return token;
    }

    public User loadUserByUserName(String userName){
        log.debug("loadUserByUserName {} ",userName);
        // redis에서 데이터 조회
        return userCacheRepository.getUser(userName).orElseGet(() ->
            userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(
                () -> new SNSApplicationException(ErrorCode.USER_NOT_FOUNDES,"not found user")
        ));
    }

    // TODO : alarm
    public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
        log.debug("Alarm Controller");
        return alarmEntityRepositoty.findAllByUserId(userId, pageable).map(Alarm::fromEntity);
    }

    public Page<Alarm> alarmList_old(String userName, Pageable pageable) {

        // user를 찾아서 넣어야 되는데 실제 정의 되는 부분은 userID 값만 있으면된다.
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SNSApplicationException(ErrorCode.USER_NOT_FOUNDES, String.format("%s not founded",userName)));

        return alarmEntityRepositoty.findAllByUser(userEntity, pageable).map(Alarm::fromEntity);
    }
}
