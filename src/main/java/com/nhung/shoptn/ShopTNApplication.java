package com.nhung.shoptn;

import com.nhung.shoptn.model.Role;
import com.nhung.shoptn.model.User;
import com.nhung.shoptn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@SpringBootApplication
public class ShopTNApplication implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    public static void main(String[] args) {
        SpringApplication.run(ShopTNApplication.class, args);
    }

    public void run(String... args){
        User adminAccount = userRepository.findByRole(Role.ADMIN);
        if(null == adminAccount){
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setPhone("0123456789");
            user.setName("admin");
            user.setRole(Role.ADMIN);
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setIsEnable(1);
            userRepository.save(user);
        }

    }

}
