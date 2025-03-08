package com.fastcampus.sns.controller;


import com.fastcampus.sns.controller.request.UserJoinRequest;
import com.fastcampus.sns.controller.request.UserLoginRequest;
import com.fastcampus.sns.controller.response.Response;
import com.fastcampus.sns.controller.response.UserJoinResponse;
import com.fastcampus.sns.controller.response.UserLoginResponse;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        User user = userService.join(request.getUserName(),request.getPassword());

        UserJoinResponse response = UserJoinResponse.fromUser(user);

        // 성공인지 실패인지에 경우
        log.debug("response {},",response.toString());
        return Response.success(response);
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getUserName(), request.getPassword());
        log.debug("token {}",token);
        return Response.success(new UserLoginResponse(token));
    }

}
