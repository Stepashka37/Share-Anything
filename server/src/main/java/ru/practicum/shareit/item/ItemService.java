package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto);

    ItemDtoWithBookingsAndComments getItemById(long userId, long itemId);

    List<ItemDtoWithBookingsAndComments> getOwnerItems(long userId, Integer from, Integer size);

    List<ItemDto> searchAvailableItems(long userId, String text, Integer from, Integer size);

    CommentDtoToReturn addComment(long userId, long itemId, CommentDtoToCreate dtoToCreate);

}
