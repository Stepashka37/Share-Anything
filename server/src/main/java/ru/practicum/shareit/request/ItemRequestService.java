package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(long userId, ItemRequestDtoToCreate itemRequestDtoToCreate);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getOtherUsersRequests(long userId, int from, int size);

    ItemRequestDto getRequestById(long userId, long requestId);
}
