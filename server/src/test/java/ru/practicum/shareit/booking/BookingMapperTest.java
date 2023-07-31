package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.BookingMapper.modelToDto;

class BookingMapperTest {

    private BookingMapper mapper;

    @Test
    void itShouldMapModelToDto() {
        // Given
        User booker = User.builder()
                .id(1L)
                .name("name")
                .email("email@yandex.ru")
                .build();

        User itemOwner = User.builder()
                .id(2L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2023,01,01,10,00,00))
                .end(LocalDateTime.of(2023,01,01,11,00,00))
                .build();
        // When
        BookingDto bookingDto = modelToDto(booking);
        // Then
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getItem()).isEqualTo(booking.getItem());
        assertThat(bookingDto.getBooker()).isEqualTo(booking.getBooker());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void itShouldMapDtoToCreateToModel() {
        // Given
        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023,01,01,10,00,00))
                .end(LocalDateTime.of(2023,01,01,11,00,00))
                .build();

        Booking booking = Booking.builder()
                .start(LocalDateTime.of(2023,01,01,10,00,00))
                .end(LocalDateTime.of(2023,01,01,11,00,00))
                .build();
        // When
        BookingDto bookingDto = modelToDto(booking);
        // Then

        assertThat(bookingDtoToCreate.getStart()).isEqualTo(LocalDateTime.of(2023,01,01,10,00,00));
        assertThat(bookingDtoToCreate.getEnd()).isEqualTo(LocalDateTime.of(2023,01,01,11,00,00));

    }
}