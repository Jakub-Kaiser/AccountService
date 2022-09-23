package com.AccountService.controller;


import com.AccountService.DTO.UserDTO;
import com.AccountService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Validated
public class AccountServiceController {

    UserService userService;
    @Autowired
    public AccountServiceController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserDTO user) {
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.OK);
//        return EntityModel.of(userService.saveUser(user),
//                linkTo(methodOn(AccountServiceController.class).register(user)).withSelfRel());
    }

    @GetMapping("/auth")
    public String getAuth() {
        return "Access granted";
    }


}
