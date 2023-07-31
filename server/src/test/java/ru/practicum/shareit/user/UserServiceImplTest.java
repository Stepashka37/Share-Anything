package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.UserMapper.dtoToModel;
import static ru.practicum.shareit.user.UserMapper.modelToDto;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl underTest;


    @BeforeEach
    void setUp() {
        underTest = new UserServiceImpl(userRepository);
    }

    @Test
    void itShouldGetUserById() {
        // Given
        User userToSave = User.builder()
                .id(1L)
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();
        // When
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userToSave));

        UserDto userFound = underTest.getUserById(1L);
        // Then

        assertThat(userFound).isEqualToComparingFieldByField(modelToDto(userToSave));
    }

    @Test
    void itShouldThrowIfUserDoesNotExist() {
        // Given
        User userToSave = User.builder()
                .id(1L)
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();
        // When
        doThrow(new UserNotFoundException("User not found")).when(userRepository).findById(1L);

        // Then

        assertThatThrownBy(() -> underTest.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void itShouldGetAllUsers() {
        // Given
        User user1 = User.builder()
                .id(1L)
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Username")
                .email("Useremail1@yandex.ru")
                .build();
        // When
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> usersReturn = underTest.getAllUsers();
        // Then

        assertThat(usersReturn).hasSize(2);
        assertThat(usersReturn.get(0).getId()).isEqualTo(user1.getId());
        assertThat(usersReturn.get(1).getId()).isEqualTo(user2.getId());
    }

    @Test
    void itShouldUpdateUserEmail() {
        // Given
        User userToBeUpdated = User.builder()
                .id(1L)
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        User userUpdated = User.builder()
                .id(1L)
                .name("Username")
                .email("UseremailUPD@yandex.ru")
                .build();

        UserDto userDtoForUpdate = UserDto.builder()
                .email("UseremailUPD@yandex.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userToBeUpdated));
        when(userRepository.save(any())).thenReturn(userUpdated);

        UserDto userUpdatedFromDb = underTest.updateUser(1L, userDtoForUpdate);
        // When
        // Then

        assertThat(userUpdatedFromDb.getId()).isEqualTo(1L);
        assertThat(userUpdatedFromDb.getName()).isEqualTo("Username");
        assertThat(userUpdatedFromDb.getEmail()).isEqualTo("UseremailUPD@yandex.ru");
    }

    @Test
    void itShouldUpdateUserName() {
        // Given
        User userToBeUpdated = User.builder()
                .id(1L)
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        User userUpdated = User.builder()
                .id(1L)
                .name("UsernameUPD")
                .email("Useremail@yandex.ru")
                .build();

        UserDto userDtoForUpdate = UserDto.builder()
                .name("UsernameUPD")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userToBeUpdated));
        when(userRepository.save(any())).thenReturn(userUpdated);

        UserDto userUpdatedFromDb = underTest.updateUser(1L, userDtoForUpdate);
        // When
        // Then

        assertThat(userUpdatedFromDb.getId()).isEqualTo(1L);
        assertThat(userUpdatedFromDb.getName()).isEqualTo("UsernameUPD");
        assertThat(userUpdatedFromDb.getEmail()).isEqualTo("Useremail@yandex.ru");
    }

    @Test
    void itShouldNotUpdateIfUserDoesNotExist() {
        // Given
        User userToSave = User.builder()
                .id(1L)
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();
        // When
        doThrow(new UserNotFoundException("User not found")).when(userRepository).findById(1L);

        // Then

        assertThatThrownBy(() -> underTest.updateUser(1L, userDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void itShouldNotUpdateIfNameIsBlank() {
        // Given
        User userFromDb = User.builder()
                .id(1L)
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name(null)
                .email("UseremailUPD@yandex.ru")
                .build();
        // When
        when(userRepository.findById(1L)).thenReturn(Optional.of(userFromDb));
        when(userRepository.save(userFromDb)).thenReturn(userFromDb);
        // Then
        UserDto userUpdated = underTest.updateUser(1L, userDto);
        assertThat(userUpdated.getName()).isEqualTo("Username");
        assertThat(userUpdated.getEmail()).isEqualTo("UseremailUPD@yandex.ru");
    }


    @Test
    void itShouldCreateNewUser() {
        // Given
        UserDto userToSave = UserDto.builder()
                .id(1L)
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();
        // When
        when(userRepository.save(any(User.class)))
                .thenReturn(dtoToModel(userToSave));

        UserDto actualUser = underTest.createNewUser(userToSave);
        // Then
        assertThat(actualUser).isEqualToComparingFieldByField(userToSave);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void itShouldDeleteUserById() {
        // Given
        // When
        underTest.deleteUserById(1L);
        // Then
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void itShouldDeleteAllUsers() {
        // Given
        // When
        underTest.deleteUsers();
        // Then
        verify(userRepository, times(1)).deleteAll();
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = UserDto.builder()
                .name(name)
                .email(email)
                .build();
        return dto;
    }
}