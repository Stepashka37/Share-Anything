package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoToReturnTest {

    @Autowired
    private JacksonTester<CommentDtoToReturn> json;

    @Test
    void testCommentDto() throws Exception {
        CommentDtoToReturn commentDtoToReturn = CommentDtoToReturn.builder()
                .id(1L)
                .text("comment text")
                .authorName("name")
                .created(LocalDateTime.of(2023, 01, 01, 10, 00, 00))
                .build();

        JsonContent<CommentDtoToReturn> result = json.write(commentDtoToReturn);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-01T10:00:00");
    }
}