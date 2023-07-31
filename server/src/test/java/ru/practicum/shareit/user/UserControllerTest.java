package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    @Test
    void itShouldGetUserById() {
        // Given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("username1")
                .email("username1@yandex.ru")
                .build();
        // When
        when(userService.getUserById(1L)).thenReturn(userDto);
        // Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void itShouldGetAllUsers() {
        // Given
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("username1")
                .email("username1@yandex.ru")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("username1")
                .email("username2@yandex.ru")
                .build();

        List<UserDto> dtos = List.of(userDto1, userDto2);
        // When
        when(userService.getAllUsers()).thenReturn(dtos);
        // Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(dtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(dtos.get(1).getId()), Long.class));
    }


    @SneakyThrows
    @Test
    void itShouldCreateUser() {
        // Given
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("username1")
                .email("username1@yandex.ru")
                .build();
        // When
        when(userService.createNewUser(any())).thenReturn(userDto1);
        // Then
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @SneakyThrows
    @Test
    void itShouldNotCreateUserWithEmptyFields() {
        // Given
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name(null)
                .email("username1@yandex.ru")
                .build();
        // When
        // Then
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void itShouldUpdateUser() {
        // Given
        UserDto userDto1 = UserDto.builder()
                .name("username1UPD")
                .email("username1UPD@yandex.ru")
                .build();

        UserDto userDtoFromService = UserDto.builder()
                .id(1L)
                .name("username1UPD")
                .email("username1UPD@yandex.ru")
                .build();
        // When
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(userDtoFromService);
        // Then
        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoFromService.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoFromService.getName())))
                .andExpect(jsonPath("$.email", is(userDtoFromService.getEmail())));
    }

    @SneakyThrows
    @Test
    void itShouldDeleteUser() {
        // Given
        UserDto userDto1 = UserDto.builder()
                .name("username1UPD")
                .email("username1UPD@yandex.ru")
                .build();

        UserDto userDtoFromService = UserDto.builder()
                .id(1L)
                .name("username1UPD")
                .email("username1UPD@yandex.ru")
                .build();
        // When
        // Then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void itShouldDeleteAllUsers() {
        // Given
        UserDto userDto1 = UserDto.builder()
                .name("username1UPD")
                .email("username1UPD@yandex.ru")
                .build();

        UserDto userDtoFromService = UserDto.builder()
                .id(1L)
                .name("username1UPD")
                .email("username1UPD@yandex.ru")
                .build();
        // When
        // Then
        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk());
    }


}