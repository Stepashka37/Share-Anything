package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    @SneakyThrows
    @Test
    void itShouldCreateBooking() {
        // Given
        User user = User.builder()
                .id(1L)
                .name("username")
                .email("username@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .isAvailable(true)
                .owner(new User())
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(30))
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .item(new Item())
                .booker(new User())
                .build();

        // When
        when(bookingService.createBooking(anyLong(), any(BookingDtoToCreate.class))).thenReturn(bookingDto);
        // Then
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-id", 1)
                        .content(mapper.writeValueAsString(bookingDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

    }

    @SneakyThrows
    @Test
    void itShouldApproveBooking() {
        // Given
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .isAvailable(true)
                .owner(new User())
                .build();


        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(30))
                .status(BookingStatus.APPROVED)
                .item(new Item())
                .booker(new User())
                .build();

        // When
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);
        // Then
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

    }

    @SneakyThrows
    @Test
    void itShouldGetBookingById() {
        // Given
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(30))
                .status(BookingStatus.APPROVED)
                .item(new Item())
                .booker(new User())
                .build();

        // When
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);
        // Then
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

    }

    @SneakyThrows
    @Test
    void itShouldGetUserBookings() {
        // Given
        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(30))
                .status(BookingStatus.APPROVED)
                .item(new Item())
                .booker(new User())
                .build();

        BookingDto bookingDto2 = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(30))
                .status(BookingStatus.APPROVED)
                .item(new Item())
                .booker(new User())
                .build();

        // When
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto1, bookingDto2));
        // Then
        mockMvc.perform(get("/bookings?state=APPROVED&from=0&size=10")
                        .header("X-Sharer-User-id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void itShouldGetAllUserItemsBookings() {
        // Given

        User user = User.builder()
                .id(1L)
                .name("username")
                .email("username@yadnex.ru")
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 description")
                .isAvailable(true)
                .owner(user)
                .build();

        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(30))
                .status(BookingStatus.APPROVED)
                .item(item1)
                .booker(new User())
                .build();

        BookingDto bookingDto2 = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(30))
                .status(BookingStatus.APPROVED)
                .item(item2)
                .booker(new User())
                .build();

        // When
        when(bookingService.getAllUserItemsBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto1, bookingDto2));
        // Then
        mockMvc.perform(get("/bookings/owner?state=APPROVED&from=0&size=10")
                        .header("X-Sharer-User-id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class));
    }
}