package com.AccountService.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    @NotEmpty (message = "name must not be empty")
    private String name;
    @NotEmpty (message = "lastname must not be empty")
    private String lastname;
    @NotEmpty (message = "email must not be empty")
    @Pattern(regexp = "[a-zA-Z0-9.-]+@acme.com",message = "Email should be @acme.com")
    private String email;
    @NotEmpty (message = "password must not be empty")
    @Size(min = 12, message = "Password must be at least 12 characters long")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public UserDTO(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }
}
