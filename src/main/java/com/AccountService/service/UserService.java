package com.AccountService.service;

import com.AccountService.DTO.UserDTO;
import com.AccountService.entity.UserEntity;
import com.AccountService.exception.UserExistsException;
import com.AccountService.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

    public UserDTO saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmailIgnoreCase(userDTO.getEmail())) {
            throw new UserExistsException("User exists");
        }
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        UserEntity newUser = new UserEntity(
                userDTO.getName(),
                userDTO.getLastname(),
                userDTO.getEmail(),
                encryptedPassword
        );
        userRepository.save(newUser);
        userDTO.setId(newUser.getId());
        return userDTO;
    }

}
