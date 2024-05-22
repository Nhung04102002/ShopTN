package com.nhung.shoptn.config;

import com.nhung.shoptn.model.Role;
import com.nhung.shoptn.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        User user = (User) authentication.getPrincipal();
        System.out.println(user.getEmail());
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        authorities.forEach(auth -> System.out.println(auth.getAuthority()));
        String redirectURL = request.getContextPath();
        if(user.getRole().equals(Role.ADMIN)){
            redirectURL += "/admin";
        }
        else if(user.getRole().equals(Role.USER)){
            redirectURL += "/user";
        }
//        super.onAuthenticationSuccess(request,response,authentication);
        response.sendRedirect(redirectURL);

    }
}
