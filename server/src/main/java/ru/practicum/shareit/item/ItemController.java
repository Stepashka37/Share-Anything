package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping("/items")

public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-id") long userId, @Validated(ItemDto.New.class) @RequestBody ItemDto itemDto) {
        ItemDto itemCreated = itemService.createItem(userId, itemDto);
        return itemCreated;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-id") long userId, @PathVariable long itemId,  @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        ItemDto itemUpdated = itemService.updateItem(userId, itemDto);
        return itemUpdated;
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingsAndComments getItemById(@RequestHeader("X-Sharer-User-id") long userId, @PathVariable long itemId) {
        ItemDtoWithBookingsAndComments itemRequested = itemService.getItemById(userId, itemId);
        return itemRequested;
    }

    @GetMapping
    public List<ItemDtoWithBookingsAndComments> getOwnerItems(@RequestHeader("X-Sharer-User-id") long userId,
                                                              @RequestParam (value = "from", defaultValue = "0") @Min(0)  Integer from,
                                                              @RequestParam (value = "size", defaultValue = "10") @Min(1)  Integer size) {
        return itemService.getOwnerItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestHeader("X-Sharer-User-id") long userId, @RequestParam String text,
                                              @RequestParam (value = "from", defaultValue = "0") @Min(0)  Integer from,
                                              @RequestParam (value = "size", defaultValue = "10") @Min(1)  Integer size) {
        return itemService.searchAvailableItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoToReturn addCommentToItem(@RequestHeader("X-Sharer-User-id") long userId, @PathVariable long itemId,
                                               @RequestBody CommentDtoToCreate dtoToCreate) {
        return itemService.addComment(userId, itemId, dtoToCreate);
    }

}
