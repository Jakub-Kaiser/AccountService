package com.AccountService.service;

import com.AccountService.DTO.UserDTO;
import com.AccountService.entity.UserEntity;
import com.AccountService.exception.UserExistsException;
import com.AccountService.repository.UserRepository;
import com.AccountService.security.BreachedPasswords;
import com.AccountService.security.PasswordDTO;
import com.AccountService.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    BreachedPasswords breachedPasswords;

    public UserDTO saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmailIgnoreCase(userDTO.getEmail())) {
            throw new UserExistsException("User exists");
        }
        throwIfPasswordBreached(userDTO.getPassword());
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

    public String changePassword(String newPassword) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (passwordEncoder.matches(newPassword, currentUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old and new password must be different");
        }
        throwIfPasswordBreached(newPassword);
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(currentUser.getUsername());
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
        return String.format("Password for email \"%s\" has been changed", currentUser.getUsername());
    }

    public void throwIfPasswordBreached(String password) {
        if (breachedPasswords.isPasswordBreached(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is insecure");
        }
    }


}
