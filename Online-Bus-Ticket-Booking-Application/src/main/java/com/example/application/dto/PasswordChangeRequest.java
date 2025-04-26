package com.example.application.dto;

import jakarta.validation.constraints.NotBlank;

public class PasswordChangeRequest {
    @NotBlank(message = "Email should not be blank")
    private String usernameOrEmail;
    @NotBlank(message = "oldPassword should not be blank")
    private String oldPassword;
    @NotBlank(message = "newPassword should not be blank")
    private String newPassword;
    @NotBlank(message = "confirmPassword should not be blank")
    private String confirmPassword;

    public PasswordChangeRequest() {
    }

    public PasswordChangeRequest(String usernameOrEmail, String oldPassword, String newPassword, String confirmPassword) {
        this.usernameOrEmail = usernameOrEmail;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public @NotBlank(message = "Email should not be blank") String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(@NotBlank(message = "Email should not be blank") String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public @NotBlank(message = "oldPassword should not be blank") String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(@NotBlank(message = "oldPassword should not be blank") String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public @NotBlank(message = "newPassword should not be blank") String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(@NotBlank(message = "newPassword should not be blank") String newPassword) {
        this.newPassword = newPassword;
    }

    public @NotBlank(message = "confirmPassword should not be blank") String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(@NotBlank(message = "confirmPassword should not be blank") String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
