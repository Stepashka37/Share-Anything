package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoToCreate;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Min;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-id") long userId,
                                             @Validated(ItemDto.New.class) @RequestBody ItemDto itemDto) {
        log.info("User {} created new item", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-id") long userId, @PathVariable long itemId,
                                             @Validated(ItemDto.Update.class) @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        log.info("User {} updated item {}", userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-id") long userId,
                                                      @PathVariable long itemId) {
        log.info("User {} requested information about item {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader("X-Sharer-User-id") long userId,
                                                              @RequestParam (value = "from", defaultValue = "0") @Min(0)  Integer from,
                                                              @RequestParam (value = "size", defaultValue = "10") @Min(1)  Integer size) {
       log.info("Get all items of user {}", userId);
       return itemClient.getOwnerItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItems(@RequestHeader("X-Sharer-User-id") long userId, @RequestParam String text,
                                              @RequestParam (value = "from", defaultValue = "0") @Min(0)  Integer from,
                                              @RequestParam (value = "size", defaultValue = "10") @Min(1)  Integer size) {
       log.info("Search for available items with text {}", text);
       return itemClient.searchAvailableItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@RequestHeader("X-Sharer-User-id") long userId, @PathVariable long itemId,
                                               @Validated @RequestBody CommentDtoToCreate dtoToCreate) {
        log.info("User {} added comment to item {}", userId, itemId);
        return itemClient.addCommentToItem(userId, itemId,dtoToCreate);
    }

}
