package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotFound;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.ItemMapper.modelToDto;
import static ru.practicum.shareit.request.RequestMapper.dtoToModel;
import static ru.practicum.shareit.request.RequestMapper.modelToDto;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ItemRequestDto createRequest(long userId, ItemRequestDtoToCreate itemRequestDtoToCreate) {
        User userFound = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        ItemRequest itemRequestToSave = dtoToModel(itemRequestDtoToCreate);
        itemRequestToSave.setRequestor(userFound);
        ItemRequest requestSaved = itemRequestRepository.save(itemRequestToSave);
        log.info("Создали запрос с id{}", requestSaved.getId());
        return modelToDto(requestSaved);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        User userFound = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<ItemRequestDto> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId)
                .stream()
                .map(x -> modelToDto(x))
                .collect(toList());

        List<Long> requestsIds = itemRequests.stream()
                .map(x -> x.getId())
                        .collect(toList());

        Map<Long, List<ItemDto>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestsIds)
                .stream()
                .map(x -> modelToDto(x))
                        .collect(groupingBy(ItemDto::getRequestId, toList()));

        for (ItemRequestDto itemRequestDto : itemRequests) {
            List<ItemDto> itemDtos = new ArrayList<>();

            if (itemsByRequest.get(itemRequestDto.getId()) != null) {
            itemDtos = itemsByRequest.get(itemRequestDto.getId());
            }
            itemRequestDto.setItems(itemDtos);
        }
        log.info("Получили список всех запросов пользователя с id{}", userId);
        return itemRequests;

    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(long userId, int from, int size) {
        User userFound = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").ascending());
        List<ItemRequestDto> pagedResult = itemRequestRepository.findAllUserRequest(userId, pageable)
                .stream()
                .map(x -> modelToDto(x))
                .collect(toList());

        List<Long> requestsIds = pagedResult.stream()
                .map(x -> x.getId())
                .collect(toList());

        Map<Long, List<ItemDto>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestsIds)
                .stream()
                .map(x -> modelToDto(x))
                .collect(groupingBy(ItemDto::getRequestId, toList()));

        for (ItemRequestDto itemRequestDto : pagedResult) {
            itemRequestDto.setItems(itemsByRequest.get(itemRequestDto.getId()));
        }
        log.info("Получили список всех запросов пользователей начиная со страницы {page}", page);
        return pagedResult;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        User userFound = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFound("Request not found"));
        List<ItemDto> items = itemRepository.findAllByRequestId(itemRequest.getId())
                        .stream()
                        .map(x -> modelToDto(x))
                        .collect(toList());
        ItemRequestDto result = modelToDto(itemRequest);
        result.setItems(items);
        log.info("Получили данные о запросе с id{}", requestId);
        return result;
    }


}
