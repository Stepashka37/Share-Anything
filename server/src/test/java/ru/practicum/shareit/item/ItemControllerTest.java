package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    @Test
    void itShouldCreateItem() {
        // Given
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 description")
                .available(true)
                .build();
        // When
        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);
        // Then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void itShouldNotCreateItemWhenNameNull() {
        // Given
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name(null)
                .description("Item1 description")
                .available(true)
                .build();
        // When
        // Then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void itShouldUpdateItem() {
        // Given
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item1UPD")
                .description("Item1 description")
                .available(false)
                .build();

        ItemDto itemDtoUpdated = ItemDto.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 description")
                .available(true)
                .build();
        // When
        when(itemService.updateItem(anyLong(), any(ItemDto.class))).thenReturn(itemDtoUpdated);
        // Then
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdated.getDescription())));
    }

    @SneakyThrows
    @Test
    void itShouldGetItemById() {
        // Given
        ItemDtoWithBookingsAndComments itemDto = ItemDtoWithBookingsAndComments.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 description")
                .available(true)
                .build();
        // When
        when(itemService.getItemById(1L, 1L)).thenReturn(itemDto);
        // Then
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void itShouldGetOwnerItems() {
        // Given
        ItemDtoWithBookingsAndComments itemDto1 = ItemDtoWithBookingsAndComments.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 description")
                .available(true)
                .build();

        ItemDtoWithBookingsAndComments itemDto2 = ItemDtoWithBookingsAndComments.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 description")
                .available(true)
                .build();

        // When
        when(itemService.getOwnerItems(1L, 0, 10)).thenReturn(List.of(itemDto1, itemDto2));
        // Then
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void itShouldSearchAvailable() {
        // Given
        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 description")
                .available(true)
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 description")
                .available(true)
                .build();
        // When
        when(itemService.searchAvailableItems(1L, "item", 0, 10)).thenReturn(List.of(itemDto1, itemDto2));
        // Then
        mockMvc.perform(get("/items/search?text=item")
                        .header("X-Sharer-User-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class));
    }

    @SneakyThrows
    @Test
    void itShouldAddCommentToItem() {
        // Given
        CommentDtoToCreate commentDtoToCreate = CommentDtoToCreate.builder()
                .text("comment")
                .build();

        CommentDtoToReturn commentDtoToReturn = CommentDtoToReturn.builder()
                .id(1L)
                .text("comment")
                .created(LocalDateTime.now())
                .authorName("username")
                .build();

        // When
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDtoToCreate.class))).thenReturn(commentDtoToReturn);
        // Then
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-id", 1)
                        .content(mapper.writeValueAsString(commentDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoToReturn.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoToReturn.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoToReturn.getAuthorName())));

    }
}