package com.AccountService.security;

import javax.validation.constraints.Size;

public class PasswordDTO {

    @Size(min = 6, message = "Password must be at least 6 characters long")
    String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
