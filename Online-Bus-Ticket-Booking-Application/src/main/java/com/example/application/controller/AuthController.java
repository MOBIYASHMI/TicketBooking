package com.example.application.controller;

import com.example.application.dto.LoginRequest;
import com.example.application.dto.PasswordChangeRequest;
import com.example.application.dto.RegisterRequest;
import com.example.application.entity.Role;
import com.example.application.entity.User;
import com.example.application.repository.UserRepository;
import com.example.application.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/registerForm")
    public String showRegistrationForm(Model model){
        model.addAttribute("registerRequest",new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest")RegisterRequest registerRequest, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            return "register";
        }

        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            result.rejectValue("username","error.user","Username is already taken");
            return "redirect:/auth/registerForm";
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            result.rejectValue("email","error.user","Email already exists");
            return "redirect:/auth/registerForm";
        }

        User user=new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage","Registration successful! Please login");
        return "redirect:/auth/loginForm";
    }

    @GetMapping("/loginForm")
    public String showLoginForm(){
        return "login";
    }

    @PostMapping("/authenticate")
    public String authenticateUser(@Valid LoginRequest loginRequest, HttpServletResponse response,RedirectAttributes redirectAttributes) { // Added HttpServletResponse
        System.out.println("Login attempt for: " + loginRequest.getUsernameOrEmail());
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            System.out.println("Authentication successful for: " + user.getUsername());
            String jwt = jwtUtil.generateToken(user);
            System.out.println("jwt token: "+jwt);

            Cookie jwtCookie = new Cookie("jwtToken", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(86400); // Cookie expiration (e.g., 1 day)
            jwtCookie.setPath("/"); // Cookie available for all paths
            response.addCookie(jwtCookie);

            if(user.getRole() == Role.ADMIN){
                return "redirect:/admin/dashboard";
            }else{
                return "redirect:/dashboard";
            }
        }catch (AuthenticationException e){
            System.out.println("Authentication failed for: " + loginRequest.getUsernameOrEmail() + ". Reason: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password"); // Add error message to the model
            return "redirect:/auth/loginForm";
        }
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(@RequestParam(value = "success", required = false) String success,
                                         Model model) {
        if (success != null) {
            model.addAttribute("successMessage", "Password changed successfully!");
        }
        return "password";  // This will load the same change-password.html
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute PasswordChangeRequest passwordChangeRequest,
                                 Model model) {
        Optional<User> optionalUser = userRepository.findByEmail(passwordChangeRequest.getUsernameOrEmail());

        if (!optionalUser.isPresent()) {
            model.addAttribute("errorMessage", "User not found with given email.");
            return "password";
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            model.addAttribute("errorMessage", "Old password is incorrect.");
            return "password";
        }

        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
            model.addAttribute("errorMessage", "New Password and Confirm Password do not match.");
            return "password";
        }

        // Everything is fine. Encrypt new password and update.
        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        userRepository.save(user);

        model.addAttribute("successMessage", "Password changed successfully!");
        return "redirect:/auth/change-password?success";
    }

}
