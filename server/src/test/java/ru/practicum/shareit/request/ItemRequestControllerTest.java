package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    @SneakyThrows
    @Test
    void itShouldCreateRequest() {
        // Given
        ItemRequestDtoToCreate itemRequestDtoToCreate = ItemRequestDtoToCreate.builder()
                .description("description")
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("description")
                .build();
        // When
        when(itemRequestService.createRequest(anyLong(), any(ItemRequestDtoToCreate.class))).thenReturn(itemRequestDto);
        // Then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-id", 1)
                        .content(mapper.writeValueAsString(itemRequestDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void itShouldGetRequests() {
        // Given
        ItemRequestDtoToCreate itemRequestDtoToCreate = ItemRequestDtoToCreate.builder()
                .description("description")
                .build();

        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("description1")
                .build();

        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .id(2L)
                .created(LocalDateTime.now())
                .description("description2")
                .build();
        // When
        when(itemRequestService.getUserRequests(anyLong())).thenReturn(List.of(itemRequestDto1, itemRequestDto2));
        // Then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void itShouldGetOtherUsersRequests() {
        // Given

        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("description1")
                .build();

        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .id(2L)
                .created(LocalDateTime.now())
                .description("description2")
                .build();

        // When
        when(itemRequestService.getOtherUsersRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto1, itemRequestDto2));
        // Then
        mockMvc.perform(get("/requests/all?from=0&size=2")
                        .header("X-Sharer-User-id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void itShouldGetRequestById() {
        // Given

        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("description1")
                .build();

        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .id(2L)
                .created(LocalDateTime.now())
                .description("description2")
                .build();

        // When
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto1);
        // Then
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())));
    }
}