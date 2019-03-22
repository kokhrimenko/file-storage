package kn.fs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	//change this to provide list of allowed URL's
        	.cors().disable()
        	.csrf().disable()
        	.authorizeRequests()
        		.antMatchers("/login", "/login/reset", "/register", "/help").permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/profile")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }
}
