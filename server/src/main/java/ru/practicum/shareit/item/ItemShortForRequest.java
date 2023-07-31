package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemShortForRequest {

    private long id;

    private String name;

    private long userId;

    public ItemShortForRequest(long id, String name, long userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public ItemShortForRequest() {
    }
}
