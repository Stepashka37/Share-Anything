package ru.practicum.shareit.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Users", description = "Requests for users")
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Get user {}");
        return userClient.getUserById(userId);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @PostMapping
    @Operation(summary = "Create new user")
    public ResponseEntity<Object> createUser(@Validated(UserDto.New.class) @RequestBody UserDto userDto) {
        log.info("Create new user");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update user")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @Validated(UserDto.Update.class) @RequestBody UserDto userDto) {
        log.info("Update user {}");
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user by id")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.info("Delete user {}");
        return userClient.deleteUserById(userId);
    }

    @DeleteMapping
    @Operation(summary = "Delete all users")
    public void deleteAllUsers() {
        log.info("Delete all users");
        userClient.deleteAllUsers();
    }

}
