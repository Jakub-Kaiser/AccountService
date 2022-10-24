package com.AccountService.service;

import com.AccountService.DTO.RoleUpdateDTO;
import com.AccountService.DTO.UserDTO;
import com.AccountService.entity.UserEntity;
import com.AccountService.exception.UserNotFoundException;
import com.AccountService.repository.UserRepository;
import com.AccountService.security.BreachedPasswords;
import com.AccountService.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    BreachedPasswords breachedPasswords;

    public UserDTO saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmailIgnoreCase(userDTO.getEmail())) {
            throw new UserNotFoundException("User exists");
        }
        throwIfPasswordBreached(userDTO.getPassword());
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setRole("ROLE_USER");
        UserEntity newUser = new UserEntity(
                userDTO.getName(),
                userDTO.getLastname(),
                userDTO.getEmail(),
                encryptedPassword,
                userDTO.getRole()
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

    public List<UserDTO> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            userDTOS.add(mapUserEntityToDto(userEntity));
        }
        return userDTOS;
    }

    private UserDTO mapUserEntityToDto(UserEntity userEntity) {
        return new UserDTO(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getLastname(),
                userEntity.getEmail(),
                userEntity.getRole());
    }

    public UserDTO getUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(username);
        if (userEntity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found");
        }
        return mapUserEntityToDto(userEntity);
    }

    public UserDTO updateUserRole(RoleUpdateDTO roleUpdate) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(roleUpdate.getUser());
        if (userEntity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found");
        }
        userEntity.setRole(roleUpdate.getRole());
        userRepository.save(userEntity);
        return mapUserEntityToDto(userEntity);
    }

    public String deleteUser(String username) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(username);
        if (userEntity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found");
        }
        userRepository.delete(userEntity);
        return "User deleted";
    }
}
