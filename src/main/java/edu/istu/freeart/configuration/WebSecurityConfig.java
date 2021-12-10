package edu.istu.freeart.configuration;

import edu.istu.freeart.filter.JwtRequestFilter;
import edu.istu.freeart.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final String[] ALLOWED_GUEST_POST = new String[] { "/api/login", "/api/register" };

    private final String[] FORBIDDEN_USER_POST = new String[] { "/api/tags", "/api/images/validate" };

    private final String[] FORBIDDEN_USER_DELETE = new String[] { "/api/tags", "/api/images/*" };

    private final String[] FORBIDDEN_GUEST_GET =
            new String[] { "/api/users/auctions", "/api/users/*/moderation", "/api/users/following/" };

    private final String[] FORBIDDEN_USER_GET = new String[] { "/api/images/moderation/**",
            "/api/users/rating",
            "api/auctions/transaction",
            "api/images/history" };

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));

        httpSecurity.cors().configurationSource(request -> corsConfiguration.applyPermitDefaultValues());

        httpSecurity.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, FORBIDDEN_GUEST_GET)
                .hasAuthority("USER")
                .antMatchers(HttpMethod.GET, FORBIDDEN_USER_GET)
                .hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/**")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/uploads/**")
                .permitAll()
                .antMatchers(HttpMethod.POST, ALLOWED_GUEST_POST)
                .permitAll()
                .antMatchers(HttpMethod.POST, FORBIDDEN_USER_POST)
                .hasAuthority("ADMIN")
                .antMatchers(HttpMethod.DELETE, FORBIDDEN_USER_DELETE)
                .hasAuthority("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
