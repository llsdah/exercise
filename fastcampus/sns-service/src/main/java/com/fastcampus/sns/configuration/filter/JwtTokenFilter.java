package com.fastcampus.sns.configuration.filter;

import com.fastcampus.sns.model.User;
import com.fastcampus.sns.service.UserService;
import com.fastcampus.sns.util.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter{

    private final String key;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request 들어 올때 처리가 가능하다. ( 인증 확인 등 )

        // 해당 프로젝트는 login시 헤더에 토큰 저장
        final String HEADER = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.debug("HEADER : {}",HEADER);
        if (HEADER == null || !HEADER.startsWith("Bearer ")) {
            log.error("Error occurs while getting header. header is null of invalid, request URL {} ", request.getRequestURL());
            filterChain.doFilter(request,response);
            return;
        }

        try {
            // TODO : TOKEN 유효성 체크
            final String TOKEN = HEADER.split( " ")[1].trim();

            if ( JwtTokenUtils.isExpired(TOKEN, key) ) {
                log.error("Key is expired");
                filterChain.doFilter(request,response);
                return;
            };

            // user 조회 필요
            String userName = JwtTokenUtils.getUserName(TOKEN,key);
            // user 유효설 체크
            User user = userService.loadUserByUserName(userName);

            // 유효하다면 다시 context에 송신
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    // TODO 유저, 비밀, 권한
                    user, null, user.getAuthorities()
            );

            // request 정보 추가
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (RuntimeException e) {
            log.error("security filer error : {} ", e.getMessage());
            filterChain.doFilter(request,response);
            return;
        }

        // 생성 후 실행하기
        filterChain.doFilter(request, response);

    }
}
