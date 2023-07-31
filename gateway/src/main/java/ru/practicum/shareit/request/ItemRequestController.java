package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoToCreate;

import javax.validation.constraints.Min;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-id") long userId,
                                                @Validated @RequestBody  ItemRequestDtoToCreate itemRequestDtoToCreate) {
        log.info("User {} created new request");
        return itemRequestClient.createRequest(userId, itemRequestDtoToCreate);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Get all request of user {}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader("X-Sharer-User-id") long userId,
                                                      @RequestParam (value = "from", defaultValue = "0") @Min(0)  Integer from,
                                                      @RequestParam (value = "size", defaultValue = "1") @Min(1)  Integer size) {
        log.info("User {} requested all other users requests", userId);
        return itemRequestClient.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-id") long userId,
                                         @PathVariable long requestId) {
        log.info("Get request {}", requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
