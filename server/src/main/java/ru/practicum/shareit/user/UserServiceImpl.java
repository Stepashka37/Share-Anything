package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.dtoToModel;
import static ru.practicum.shareit.user.UserMapper.modelToDto;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(long userId) {
        User userFound = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return modelToDto(userFound);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers
                .stream()
                .map(x -> modelToDto(x))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createNewUser(UserDto userDto) {
        User userToSave = dtoToModel(userDto);
        User userCreated = userRepository.save(userToSave);
        log.info("Создали пользователя с id{}", userCreated.getId());
        return modelToDto(userCreated);
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        userDto.setId(userId);
        User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            userFromDb.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            userFromDb.setName(userDto.getName());
        }
        User userUpdated = userRepository.save(userFromDb);
        log.info("Обновили данные пользователя с id{}", userUpdated.getId());
        return modelToDto(userUpdated);
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
        log.info("Удалили пользователя с id{}", userId);
    }

    @Override
    public void deleteUsers() {
        userRepository.deleteAll();
        log.info("Удалили всех пользователей");
    }
}
