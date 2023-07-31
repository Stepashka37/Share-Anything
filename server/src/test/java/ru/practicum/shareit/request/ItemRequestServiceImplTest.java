package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.RequestNotFound;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.RequestMapper.modelToDto;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Captor
    private ArgumentCaptor<Booking> argumentCaptor;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;


    @InjectMocks
    private ItemRequestServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository);
    }

    @Test
    void itShouldCreateRequest() {
        // Given
        User requestor = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        ItemRequestDtoToCreate itemRequestDtoToCreate = ItemRequestDtoToCreate.builder()
                .description("request description")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("request description")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        // When
        ItemRequestDto created = underTest.createRequest(1L, itemRequestDtoToCreate);
        // Then
        verify(itemRequestRepository, times(1)).save(any());
        assertThat(created).isEqualToComparingFieldByField(modelToDto(itemRequest));

    }

    @Test
    void itShouldNotCreateRequestWhenUserNotFound() {
        // Given
        User requestor = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        ItemRequestDtoToCreate itemRequestDtoToCreate = ItemRequestDtoToCreate.builder()
                .description("request description")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("request description")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.createRequest(1L, itemRequestDtoToCreate))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");

    }

    @Test
    void itShouldGetUserRequests() {
        // Given
        User requestor = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User itemOwner = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail2@yandex.ru")
                .build();


        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("request1 description")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("request2 description")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("item 1")
                .description("item 1 description")
                .owner(itemOwner)
                .isAvailable(true)
                .request(itemRequest1)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("item 2")
                .description("item 2 description")
                .owner(itemOwner)
                .isAvailable(true)
                .request(itemRequest2)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(1L)).thenReturn(List.of(itemRequest1, itemRequest2));
       when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(item1, item2));
        // When
        List<ItemRequestDto> userRequests = underTest.getUserRequests(1L);
        // Then
        assertThat(userRequests).hasSize(2);
        assertThat(userRequests.get(0).getDescription()).isEqualTo(itemRequest1.getDescription());
        assertThat(userRequests.get(1).getDescription()).isEqualTo(itemRequest2.getDescription());
        assertThat(userRequests.get(0).getItems().get(0).getId()).isEqualTo(item1.getId());
        assertThat(userRequests.get(1).getItems().get(0).getId()).isEqualTo(item2.getId());
    }

    @Test
    void itShouldNotGetUserRequestsWhenUserNotFound() {
        // Given
        User requestor = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User itemOwner = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail2@yandex.ru")
                .build();


        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("request1 description")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("request2 description")
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("item 1")
                .description("item 1 description")
                .owner(itemOwner)
                .isAvailable(true)
                .request(itemRequest1)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("item 2")
                .description("item 2 description")
                .owner(itemOwner)
                .isAvailable(true)
                .request(itemRequest2)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getUserRequests(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void itShouldGetOtherUsersRequests() {
        // Given
        User user = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail1@yandex.ru")
                .build();

        User requestor1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail2@yandex.ru")
                .build();

        User requestor2 = User.builder()
                .id(3L)
                .name("User3")
                .email("Useremail3@yandex.ru")
                .build();

        User itemOwner = User.builder()
                .id(4L)
                .name("User4")
                .email("Useremail4@yandex.ru")
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("request1 description")
                .created(LocalDateTime.now())
                .requestor(requestor1)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("request1 description")
                .created(LocalDateTime.now())
                .requestor(requestor2)
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("item 1")
                .description("item 1 description")
                .owner(itemOwner)
                .isAvailable(true)
                .request(itemRequest1)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("item 2")
                .description("item 2 description")
                .owner(itemOwner)
                .isAvailable(true)
                .request(itemRequest2)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("created").ascending());
        Page<ItemRequest> page = new PageImpl<ItemRequest>(List.of(itemRequest1, itemRequest2), pageable, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllUserRequest(1L, pageable)).thenReturn(page);
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(item1, item2));
        // When
        List<ItemRequestDto> usersRequests = underTest.getOtherUsersRequests(1L, 0, 2);
        // Then
        assertThat(usersRequests).hasSize(2);
        assertThat(usersRequests.get(0).getDescription()).isEqualTo(itemRequest1.getDescription());
        assertThat(usersRequests.get(1).getDescription()).isEqualTo(itemRequest2.getDescription());
        assertThat(usersRequests.get(0).getItems().get(0).getId()).isEqualTo(item1.getId());
        assertThat(usersRequests.get(1).getItems().get(0).getId()).isEqualTo(item2.getId());
    }

    @Test
    void itShouldNotGetOtherUsersRequestsWhenUserNotFound() {
        // Given
        User user = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail1@yandex.ru")
                .build();

        User requestor1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail2@yandex.ru")
                .build();

        User requestor2 = User.builder()
                .id(3L)
                .name("User3")
                .email("Useremail3@yandex.ru")
                .build();

        User itemOwner = User.builder()
                .id(4L)
                .name("User4")
                .email("Useremail4@yandex.ru")
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("request1 description")
                .created(LocalDateTime.now())
                .requestor(requestor1)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("request1 description")
                .created(LocalDateTime.now())
                .requestor(requestor2)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getOtherUsersRequests(1L, 0, 2))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void itShouldGetRequestById() {
        // Given

        User requestor1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail2@yandex.ru")
                .build();


        User itemOwner = User.builder()
                .id(4L)
                .name("User4")
                .email("Useremail4@yandex.ru")
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("request1 description")
                .created(LocalDateTime.now())
                .requestor(requestor1)
                .build();


        Item item1 = Item.builder()
                .id(1L)
                .name("item 1")
                .description("item 1 description")
                .owner(itemOwner)
                .isAvailable(true)
                .request(itemRequest1)
                .build();


        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item1));
        // When
        ItemRequestDto itemRequestDto = underTest.getRequestById(1L, 1L);
        // Then
        assertThat(itemRequestDto).isNotNull();
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest1.getDescription());
        assertThat(itemRequestDto.getItems()).hasSize(1);
        assertThat(itemRequestDto.getItems().get(0).getId()).isEqualTo(item1.getId());
    }

    @Test
    void itShouldNotGetRequestByIdWhenUserNotFound() {
        // Given

        User requestor1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail2@yandex.ru")
                .build();


        User itemOwner = User.builder()
                .id(4L)
                .name("User4")
                .email("Useremail4@yandex.ru")
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("request1 description")
                .created(LocalDateTime.now())
                .requestor(requestor1)
                .build();


        Item item1 = Item.builder()
                .id(1L)
                .name("item 1")
                .description("item 1 description")
                .owner(itemOwner)
                .isAvailable(true)
                .request(itemRequest1)
                .build();


        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getRequestById(1L, 1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void itShouldNotGetRequestByIdWhenRequestNotFound() {
        // Given

        User requestor1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail2@yandex.ru")
                .build();


        User itemOwner = User.builder()
                .id(4L)
                .name("User4")
                .email("Useremail4@yandex.ru")
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("request1 description")
                .created(LocalDateTime.now())
                .requestor(requestor1)
                .build();


        Item item1 = Item.builder()
                .id(1L)
                .name("item 1")
                .description("item 1 description")
                .owner(itemOwner)
                .isAvailable(true)
                .request(itemRequest1)
                .build();


        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getRequestById(1L, 1L))
                .isInstanceOf(RequestNotFound.class)
                .hasMessageContaining("Request not found");
    }
}