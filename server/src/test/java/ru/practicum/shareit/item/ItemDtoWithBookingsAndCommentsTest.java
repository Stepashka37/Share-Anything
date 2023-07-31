package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingDtoForItemHost;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithBookingsAndCommentsTest {

    @Autowired
    private JacksonTester<ItemDtoWithBookingsAndComments> json;

    @Test
    void testItemDto() throws Exception {
        BookingDtoForItemHost lastBooking = BookingDtoForItemHost.builder()
                .id(1L)
                .bookerId(1L)
                .start(LocalDateTime.of(2023, 01, 01, 10, 00, 00))
                .end(LocalDateTime.of(2023, 01, 01, 11, 00, 00))
                .build();

        BookingDtoForItemHost nextBooking = BookingDtoForItemHost.builder()
                .id(2L)
                .bookerId(2L)
                .start(LocalDateTime.of(2023, 01, 01, 11, 00, 00))
                .end(LocalDateTime.of(2023, 01, 01, 12, 00, 00))
                .build();

        CommentDtoToReturn comment1 = CommentDtoToReturn.builder()
                .id(1L)
                .authorName("username1")
                .text("text")
                .created(LocalDateTime.of(2023, 01, 01, 10, 00, 00))
                .build();

        CommentDtoToReturn comment2 = CommentDtoToReturn.builder()
                .id(2L)
                .authorName("username1")
                .text("text")
                .created(LocalDateTime.of(2023, 01, 01, 9, 00, 00))
                .build();


        ItemDtoWithBookingsAndComments itemDto = ItemDtoWithBookingsAndComments.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(comment1, comment2))
                .build();

        JsonContent<ItemDtoWithBookingsAndComments> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(2);

    }

}