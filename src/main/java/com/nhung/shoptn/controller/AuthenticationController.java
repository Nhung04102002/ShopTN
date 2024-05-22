package com.nhung.shoptn.controller;

import com.nhung.shoptn.model.Role;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import com.nhung.shoptn.dto.JwtAuthenticationResponse;
import com.nhung.shoptn.dto.RefreshTokenRequest;
import com.nhung.shoptn.dto.SignUpRequest;
import com.nhung.shoptn.dto.SigninRequest;
import com.nhung.shoptn.model.User;
import com.nhung.shoptn.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping("/signup")
    public String formSignUp(Model model){
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "signup";
    }

    @GetMapping("/signIn")
    public String formSignIn(Model model){
        model.addAttribute("signInRequest", new SigninRequest());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return "signIn";
        }
        else{
            User user = (User) authentication.getPrincipal();
            if(user.getRole().equals(Role.ADMIN)){
                return "redirect:/admin";
            }
            return "redirect:/user";
        }
    };

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignUpRequest signUpRequest){
        authenticationService.signup(signUpRequest);
        return "redirect:/signIn";
    }

    @PostMapping("/signIn")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SigninRequest signinRequest){

        return ResponseEntity.ok( authenticationService.signIn(signinRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }
}
