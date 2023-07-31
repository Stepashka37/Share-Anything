package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-id") long userId,
                                        @RequestBody  ItemRequestDtoToCreate itemRequestDtoToCreate) {
        System.out.println(1);
        return itemRequestService.createRequest(userId, itemRequestDtoToCreate);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-id") long userId) {
        System.out.println(2);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(@RequestHeader("X-Sharer-User-id") long userId,
                                                      @RequestParam (value = "from", defaultValue = "0") @Min(0)  Integer from,
                                                      @RequestParam (value = "size", defaultValue = "1") @Min(1)  Integer size) {
        System.out.println(3);
        return itemRequestService.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-id") long userId,
                                         @PathVariable long requestId) {
        System.out.println(4);
        return itemRequestService.getRequestById(userId, requestId);
    }
}
