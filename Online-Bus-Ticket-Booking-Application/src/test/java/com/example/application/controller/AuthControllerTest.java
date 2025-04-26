package com.example.application.controller;

import com.example.application.entity.Role;
import com.example.application.entity.User;
import com.example.application.repository.UserRepository;
import com.example.application.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/auth/registerForm"))
                .andExpect(model().attributeExists("registerRequest"))
                .andExpect(view().name("register"));
    }

    @Test
    public void testRegisterUser() throws Exception {

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newuser@gmail.com")).thenReturn(Optional.empty());

        User user=new User(1L,"newuser","newuser@gmail.com","12345678",Role.USER);

        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .param("username","newuser")
                        .param("email","newuser@gmail.com")
                        .param("password","12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/loginForm"));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {

        User user = new User(1L, "newuser", "newuser@gmail.com", "12345678", Role.USER);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newuser@gmail.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/register")
                        .param("username", "newuser")
                        .param("email", "newuser@gmail.com")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/registerForm"));
    }

    @Test
    public void testRegisterUser_UsernameAlreadyExists() throws Exception {

        User user = new User(1L, "newuser", "newuser@gmail.com", "12345678", Role.USER);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/register")
                        .param("username", "newuser")
                        .param("email", "newuser@gmail.com")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/registerForm"));
    }


    @Test
    public void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/auth/loginForm"))
                .andExpect(view().name("login"));
    }

    @Test
    public void testAuthenticateUser_Success_UserRole() throws Exception {
        User user = new User(1L, "testuser", "testuser@gmail.com", "encodedPassword", Role.USER);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);
        when(jwtUtil.generateToken(user)).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/authenticate")
                        .param("usernameOrEmail", "testuser")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(cookie().exists("jwtToken"))
                .andExpect(cookie().value("jwtToken", "mocked-jwt-token"));
    }

    @Test
    public void testAuthenticateUser_Success_AdminRole() throws Exception {
        User user = new User(1L, "admin", "admin@gmail.com", "encodedPassword", Role.ADMIN);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);
        when(jwtUtil.generateToken(user)).thenReturn("mocked-admin-jwt-token");

        mockMvc.perform(post("/auth/authenticate")
                        .param("usernameOrEmail", "admin")
                        .param("password", "adminpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(cookie().value("jwtToken", "mocked-admin-jwt-token"));
    }

    @Test
    public void testAuthenticateUser_Failure_InvalidCredentials() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/authenticate")
                        .param("usernameOrEmail", "wronguser")
                        .param("password", "wrongpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/loginForm"))
                .andExpect(flash().attributeExists("errorMessage"));
    }



}
