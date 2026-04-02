package com.app.ecom_application.controllers;

import com.app.ecom_application.dtos.UserRequest;
import com.app.ecom_application.dtos.UserResponse;
import com.app.ecom_application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.ecom_application.models.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {


    @Autowired
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.fetchAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
     return userService.fetchUserById(id)
             .map(ResponseEntity::ok)
             .orElseGet(()->ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<String> createUser (@RequestBody UserRequest userRequest){
        userService.addUser(userRequest);
        return ResponseEntity.ok( "User added successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser (@PathVariable Long id, @RequestBody UserRequest updateUserRequest){
        boolean updated = userService.updateUser(id,updateUserRequest);
        if(updated) {
            return ResponseEntity.ok("User updated successfully");
        }
        return ResponseEntity.badRequest().build();
    }
}
