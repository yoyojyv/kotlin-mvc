package me.jerry.example.webmvc.config

import me.jerry.example.webmvc.constant.SecurityConstants.Companion.PASSWORD_PREFIX_NOOP
import me.jerry.example.webmvc.constant.SecurityConstants.Companion.PROP_USER_ACTUATOR
import me.jerry.example.webmvc.constant.SecurityConstants.Companion.PROP_USER_ADMIN
import me.jerry.example.webmvc.constant.SecurityConstants.Companion.ROLE_ACTUATOR
import me.jerry.example.webmvc.constant.SecurityConstants.Companion.ROLE_ADMIN
import me.jerry.example.webmvc.constant.SecurityConstants.Companion.ROLE_SYSTEM
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class SecurityConfig: WebSecurityConfigurerAdapter() {

    @Bean
    @ConfigurationProperties(prefix = PROP_USER_ADMIN)
    fun adminUser(): SecurityUserCredential {
        return SecurityUserCredential()
    }

    @Bean
    @ConfigurationProperties(prefix = PROP_USER_ACTUATOR)
    fun actuatorUser(): SecurityUserCredential {
        return SecurityUserCredential()
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        // @formatter:off
        auth.inMemoryAuthentication()
            .withUser(adminUser().username).password(PASSWORD_PREFIX_NOOP + adminUser().password)
            .roles(ROLE_ADMIN, ROLE_ACTUATOR, ROLE_SYSTEM)

            .and()

            .withUser(actuatorUser().username).password(PASSWORD_PREFIX_NOOP + actuatorUser().password)
            .roles(ROLE_ACTUATOR)
        // @formatter:on
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // @formatter:off
        http
            .httpBasic().and()
            .authorizeRequests()

            // actuator - health, info
            .requestMatchers(EndpointRequest.to(HealthEndpoint::class.java, InfoEndpoint::class.java)).permitAll()

            // actuator - others
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyRole(ROLE_ACTUATOR)

            // others - all open
            .anyRequest().permitAll()

        // csrf disabled
        http.csrf().disable()
        // @formatter:on
    }

    class SecurityUserCredential {
        lateinit var username: String
        lateinit var password: String
    }

}
