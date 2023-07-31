package ru.practicum.shareit.request;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Requests", description = "Requests for item requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @Operation(summary = "Create new request")
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-id") long userId,
                                                @Validated @RequestBody  ItemRequestDtoToCreate itemRequestDtoToCreate) {
        log.info("User {} created new request");
        return itemRequestClient.createRequest(userId, itemRequestDtoToCreate);
    }

    @GetMapping
    @Operation(summary = "Get requests")
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Get all request of user {}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    @Operation(summary = "Get requests of other users excluding current one")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader("X-Sharer-User-id") long userId,
                                                      @RequestParam (value = "from", defaultValue = "0") @Min(0)  Integer from,
                                                      @RequestParam (value = "size", defaultValue = "1") @Min(1)  Integer size) {
        log.info("User {} requested all other users requests", userId);
        return itemRequestClient.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "Get request by id")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-id") long userId,
                                         @PathVariable long requestId) {
        log.info("Get request {}", requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
