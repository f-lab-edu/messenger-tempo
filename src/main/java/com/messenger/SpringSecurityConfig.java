package com.messenger;

import com.messenger.domain.Member;
import com.messenger.jwt.JwtAuthenticationFilter;
import com.messenger.jwt.JwtAuthorizationFilter;
import com.messenger.jwt.JwtProperties;
import com.messenger.repository.MemberRepository;
import com.messenger.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Slf4j
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public SpringSecurityConfig(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // basic authentication
        http.httpBasic().disable(); // basic authentication filter 비활성화
        // csrf
        http.csrf().disable();
        // remember-me
        http.rememberMe().disable();
        // stateless
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // jwt filter
        http.addFilterBefore(
                new JwtAuthenticationFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class
        ).addFilterBefore(
                new JwtAuthorizationFilter(memberRepository),
                BasicAuthenticationFilter.class
        );
        // authorization
        http.authorizeRequests()
//                // /와 /home은 모두에게 허용
                .antMatchers("/").permitAll()
//                // hello 페이지는 USER 롤을 가진 유저에게만 허용
//                .antMatchers("/note").hasRole("USER")
//                .antMatchers("/admin").hasRole("ADMIN")
//                .antMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE, "/notice").hasRole("ADMIN")
//                .anyRequest().authenticated();
                .anyRequest().permitAll();
        // login
        http.formLogin()
                .loginPage("/api/v1/members/login")
                .defaultSuccessUrl("/")
                .permitAll(); // 모두 허용
        // logout
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/members/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies(JwtProperties.COOKIE_NAME);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 정적 리소스 spring security 대상에서 제외
        // web.ignoring().antMatchers("/images/**", "/css/**"); // 아래 코드와 같은 코드입니다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * UserDetailsService 구현
     *
     * @return UserDetailsService
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return userId -> {
            log.debug("SpringSecurityConfig userDetailsService() userId={}", userId);
            Member member = memberService.findById(userId).orElse(null);
            if (member == null) {
                throw new UsernameNotFoundException(userId);
            }
            log.debug("member={}", member);
            return member;
        };
    }
}
