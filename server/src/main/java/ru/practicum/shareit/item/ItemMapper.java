package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto modelToDto(Item item) {
        ItemDto result = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
        if (item.getRequest() != null) {
            result.setRequestId(item.getRequest().getId());
        }
        return result;
    }

    public static Item dtoToModel(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .build();
    }

    public static ItemDtoWithBookingsAndComments modelToDtoWithBookings(Item item) {
        return ItemDtoWithBookingsAndComments.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();

    }

    public static ItemShortForRequest modelToShortDto(Item item) {
        return ItemShortForRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .userId(item.getOwner().getId())
                .build();
    }

}
