package com.springboot.config;

import com.springboot.auth.filter.JwtAuthenticationFilter;
import com.springboot.auth.filter.JwtVerificationFilter;
import com.springboot.auth.handler.MemberAuthenticationFailureHandler;
import com.springboot.auth.handler.MemberAuthenticationSuccessHandler;
import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.utils.JwtAuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {
    private final JwtTokenizer jwtTokenizer;
    private final JwtAuthorityUtils jwtAuthorityUtils;

    public SecurityConfiguration(JwtTokenizer jwtTokenizer, JwtAuthorityUtils jwtAuthorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.jwtAuthorityUtils = jwtAuthorityUtils;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                    // h2 웹 콘솔 화면 자체가 내부적으로 <frame> 을 사용하고 있음. 이를 정상적으로 수행하도록 함.
                    // 동일 출처로부터 들어오는 요청만 페이지 렌더링을 허용.
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        // User 가 회원가입을 해서 로그인을 하면.
                        // boards post, patch, 한 건의 get, Delete
                        // Get 의 경우에 모든 글은 다른 회원도 볼 수 있음. 근데,
                        // 비밀글 상태인 질문은 질문을 등록한 회원(고객)과 관리자만 조회할 수 있다.
                        // 비밀글이면 로그인 한 상태에서 MemberId 가 같은지 다른지 조건을 달아주어야 하나.
                        // 회원으로 등록한 회원만 해당 게시판 기능 이용.
                        .antMatchers(HttpMethod.POST, "/*/boards").hasRole("USER")
                        // 질문을 등록한 회원만 수정.
                        .antMatchers(HttpMethod.PATCH, "/*/boards/*").hasRole("USER")
                        .antMatchers(HttpMethod.GET, "/*/boards").hasAnyRole("USER", "ADMIN")
                        // 1건의 특정 질문은 질문을 등록한 회원과 관리지가 조회할 수 있음.
                        .antMatchers(HttpMethod.GET, "/*/boards/*").hasAnyRole("USER", "ADMIN")
                        // 회원만 삭제 가능. 질문을 등록한 회원만 삭제.
                        .antMatchers(HttpMethod.DELETE, "/*/boards/*").hasRole("USER")
                        // 답변 관리자만 등록 가능. 한 건만 등록 가능.
                        .antMatchers(HttpMethod.POST, "/*/boards/*/comments").hasRole("ADMIN")
                        // 답변을 등록한 관리자만 수정 가능.
                        .antMatchers(HttpMethod.PATCH, "/*/boards/*/comments/*").hasRole("ADMIN")
                        // 답변을 등록한 관리자만 삭제 가능.
                        .antMatchers(HttpMethod.DELETE, "/*/boards/*/comments/*").hasRole("ADMIN")

                        .anyRequest().permitAll());
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure (HttpSecurity builder) {
            AuthenticationManager authenticationManager =
                    builder.getSharedObject(AuthenticationManager.class);
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);
            jwtAuthenticationFilter.setFilterProcessesUrl("/v2/auth/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, jwtAuthorityUtils);

            builder.addFilter(jwtAuthenticationFilter)
                    // Authentication 다음에 Verification 필터를 실행해라
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }

}
