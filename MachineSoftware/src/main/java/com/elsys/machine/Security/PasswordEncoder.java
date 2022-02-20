package com.elsys.machine.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
