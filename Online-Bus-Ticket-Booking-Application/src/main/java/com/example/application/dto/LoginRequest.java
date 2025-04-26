package com.example.application.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username or Email is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public @NotBlank(message = "Username or Email is required") String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(@NotBlank(message = "Username or Email is required") String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public @NotBlank(message = "Password is required") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") String password) {
        this.password = password;
    }
}
