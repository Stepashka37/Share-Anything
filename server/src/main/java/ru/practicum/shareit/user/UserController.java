package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private UserMapper userMapper;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        System.out.println(1);
        UserDto fromService = userService.getUserById(userId);
        return fromService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        System.out.println(2);
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Validated (UserDto.New.class) @RequestBody UserDto userDto) {
        System.out.println(3);
        UserDto userCreated = userService.createNewUser(userDto);
        return userCreated;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        System.out.println(4);
        UserDto userUpdated = userService.updateUser(userId, userDto);
        return userUpdated;
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        System.out.println(5);
        userService.deleteUserById(userId);
    }

    @DeleteMapping
    public void deleteAllUsers() {
        System.out.println(6);
        userService.deleteUsers();
    }

}
