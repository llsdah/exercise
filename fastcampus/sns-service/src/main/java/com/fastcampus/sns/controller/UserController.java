package com.fastcampus.sns.controller;


import com.fastcampus.sns.controller.request.UserJoinRequest;
import com.fastcampus.sns.controller.request.UserLoginRequest;
import com.fastcampus.sns.controller.response.AlarmResponse;
import com.fastcampus.sns.controller.response.Response;
import com.fastcampus.sns.controller.response.UserJoinResponse;
import com.fastcampus.sns.controller.response.UserLoginResponse;
import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SNSApplicationException;
import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.service.UserService;
import com.fastcampus.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.swing.undo.AbstractUndoableEdit;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
    @PostConstruct
    public void init() {
        log.info("userService가 null인지 확인: {}", userService == null);
    }

     */

    @ResponseBody
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request){

        // join
        User user = userService.join(request.getName(),request.getPassword());

        UserJoinResponse response = UserJoinResponse.fromUser(user);

        // 성공인지 실패인지에 경우
        log.debug("response {},",response.toString());
        return Response.success(response);
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        log.debug("token {}",token);
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        log.debug("Alarm Controller");
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class)
                .orElseThrow(() -> new SNSApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "CLASS CASTING ERROR"));

        Page<Alarm> alarmPage = userService.alarmList(user.getId(), pageable);
        return Response.success(
                alarmPage.map(AlarmResponse::fromAlarm)
        );
    }

    @GetMapping("/alarm_old")
    public Response<Page<AlarmResponse>> alarm_old(Pageable pageable, Authentication authentication) {

        // TODO 실제 코드상 - context 부분과 가지고 올수 있는 사유를 정확히 이해해야합니다.
        // User user = (User) authentication.getPrincipal();

        Page<Alarm> alarmPage = userService.alarmList_old(authentication.getName(), pageable);
        return Response.success(
                alarmPage.map(AlarmResponse::fromAlarm)
        );
    }

}
