package com.sales.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF(因为使用JWT)
            .csrf().disable()
            // 禁用session(使用JWT无状态认证)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置请求授权
            .authorizeRequests()
            // 放行登录接口
            .antMatchers("/api/sales/login", "/api/admin/login").permitAll()
            // 放行健康检查
            .antMatchers("/actuator/health").permitAll()
            // 其他请求需要认证
            .anyRequest().authenticated()
            .and()
            // 禁用默认登录页面
            .formLogin().disable()
            .httpBasic().disable();
        
        return http.build();
    }
}
