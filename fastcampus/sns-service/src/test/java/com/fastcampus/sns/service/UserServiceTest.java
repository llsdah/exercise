package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SNSApplicationException;
import com.fastcampus.sns.fixture.UserEntityFixture;
import com.fastcampus.sns.repository.UserEntityRepository;
import com.fastcampus.sns.model.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserEntityRepository userEntityRepository;

    @MockitoBean
    private BCryptPasswordEncoder encoder;

    @Test
    void 회원가입이_정상적으로_동작하는경우() {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt password");
        when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(userName,password,1));

        Assertions.assertDoesNotThrow(() -> userService.join(userName,password));

    }

    @Test
    void 회원가입이_username으로_회원가입한_유저가_있는경우() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName,password,1);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        SNSApplicationException e = Assertions.assertThrows(SNSApplicationException.class, () -> userService.join(userName,password));

        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());

    }

    @Test
    void 로그인이_정상적으로_동작하는경우() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName,password,1);

        // mocking
        // when은 조건들 이다. 즉 아래 두 문장에 대사 if문이 있다면 그러할 경우라 가정한다.
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture)); // DB값이 해당 내용일 경우
        when(encoder.matches(password, fixture.getPassword())).thenReturn(true); //encoder.matches(password, fixture.getPassword()) 조건이 무조건 true 일 경우

        Assertions.assertDoesNotThrow(() -> userService.login(userName,password));

    }


    @Test
    void 로그인시_username으로_회원가입한_유저가_없는경우() {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        SNSApplicationException e = Assertions.assertThrows(SNSApplicationException.class, () -> userService.login(userName,password));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUNDES, e.getErrorCode());

    }
    
    @Test
    void 로그인시_password가_틀린경우() {
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";

        UserEntity fixture = UserEntityFixture.get(userName,password,1);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        SNSApplicationException e = Assertions.assertThrows(SNSApplicationException.class, () -> userService.login(userName,wrongPassword));

        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());


    }


}
