package com.main.controller;

import com.main.entity.User;
import com.main.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    @Operation(summary = "This operation is used to create user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creation of user"),
            @ApiResponse(responseCode = "209", description = "User is already exist"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})

    @PostMapping(value = "/create-user", produces = {"application/json"},
            consumes = {"application/json"})
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User result = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "This operation is used to find all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Find all users"),
            @ApiResponse(responseCode = "204", description = "No user found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})
    @GetMapping(value = "/users", produces = {"application/json"})
    public ResponseEntity<List<User>> findAllUsers() {
        List<User> list = userService.getAllUsers();
        if(!list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(list);
        }
        return ResponseEntity.noContent().build();
    }
}
