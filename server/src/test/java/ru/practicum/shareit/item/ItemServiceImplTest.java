package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void itShouldCreateItem() {
        // Given
        ItemDto itemToSave = ItemDto.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .available(true)
                .build();

        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        Item itemToBeSaved = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(itemRepository.save(any())).thenReturn(itemToBeSaved);

        // When
        ItemDto itemCreated = underTest.createItem(1L, itemToSave);
        // Then
        then(itemRepository).should().save(itemArgumentCaptor.capture());
        assertThat(itemCreated).isEqualToComparingFieldByField(itemToSave);
        assertThat(itemArgumentCaptor.getValue()).isEqualToComparingFieldByField(itemToBeSaved);
        verify(itemRepository, times(1)).save(any());

    }

    @Test
    void itShouldNotCreateItemIfUserDoesNotExist() {
        // Given
        ItemDto itemToSave = ItemDto.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .available(true)
                .build();

        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.createItem(1L, itemToSave))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(itemRepository, times(0)).save(any());

    }

    @Test
    void itShouldUpdateItem() {
        // Given
        ItemDto itemToUpdate = ItemDto.builder()
                .id(1L)
                .name("Item1UPD")
                .description("Item1UPD Description")
                .available(false)
                .build();

        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        Item itemToBeUpdated = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemUpdated = Item.builder()
                .id(1L)
                .name("Item1UPD")
                .description("Item1UPD Description")
                .isAvailable(false)
                .owner(itemOwner)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemToBeUpdated));
        when(itemRepository.save(any(Item.class))).thenReturn(itemUpdated);

        // When
        ItemDto itemUpdatedFromDb = underTest.updateItem(1L, itemToUpdate);
        // Then
        then(itemRepository).should().save(itemArgumentCaptor.capture());
        assertThat(itemArgumentCaptor.getValue().getId()).isEqualTo(1L);
        assertThat(itemArgumentCaptor.getValue().getName()).isEqualTo("Item1UPD");
        assertThat(itemArgumentCaptor.getValue().getDescription()).isEqualTo("Item1UPD Description");
        assertThat(itemArgumentCaptor.getValue().getIsAvailable()).isEqualTo(false);
        assertThat(itemArgumentCaptor.getValue().getOwner()).isEqualTo(itemOwner);

    }

    @Test
    void itShouldNotUpdateItemWhenUserDoesNotExist() {
        // Given
        ItemDto itemToUpdate = ItemDto.builder()
                .id(1L)
                .name("Item1UPD")
                .description("Item1UPD Description")
                .available(false)
                .build();

        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        Item itemToBeUpdated = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemUpdated = Item.builder()
                .id(1L)
                .name("Item1UPD")
                .description("Item1UPD Description")
                .isAvailable(false)
                .owner(itemOwner)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());


        // When
        // Then
        assertThatThrownBy(() -> underTest.updateItem(1L, itemToUpdate))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void itShouldNotUpdateItemWhenItemDoesNotExist() {
        // Given
        ItemDto itemToUpdate = ItemDto.builder()
                .id(1L)
                .name("Item1UPD")
                .description("Item1UPD Description")
                .available(false)
                .build();

        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        Item itemToBeUpdated = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemUpdated = Item.builder()
                .id(1L)
                .name("Item1UPD")
                .description("Item1UPD Description")
                .isAvailable(false)
                .owner(itemOwner)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());


        // When
        // Then
        assertThatThrownBy(() -> underTest.updateItem(1L, itemToUpdate))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found");
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void itShouldGetItemByIdForItemOwner() {
        // Given

        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User commentator1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        User commentator2 = User.builder()
                .id(3L)
                .name("User3")
                .email("Useremail2@yandex.ru")
                .build();

        Item itemToReturn = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .item(itemToReturn)
                .user(commentator1)
                .text("Comment1 text")
                .created(LocalDateTime.now().minusMinutes(10))
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .item(itemToReturn)
                .user(commentator2)
                .text("Comment2 text")
                .created(LocalDateTime.now().minusMinutes(5))
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .item(itemToReturn)
                .booker(commentator1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(10))
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .item(itemToReturn)
                .booker(commentator2)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(15))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemToReturn));
        when(commentRepository.findAllByItem(1L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findAllBookingsByItemId(1L)).thenReturn(List.of(booking1, booking2));
        // When
        ItemDtoWithBookingsAndComments itemFound = underTest.getItemById(1L, 1L);
        // Then
        assertThat(itemFound.getName()).isEqualTo(itemToReturn.getName());
        assertThat(itemFound.getDescription()).isEqualTo(itemToReturn.getDescription());
        assertThat(itemFound.getComments()).hasSize(2);
        assertThat(itemFound.getComments().get(0).getId()).isEqualTo(comment1.getId());
        assertThat(itemFound.getComments().get(1).getId()).isEqualTo(comment2.getId());
        assertThat(itemFound.getLastBooking().getId()).isEqualTo(booking1.getId());
        assertThat(itemFound.getNextBooking().getId()).isEqualTo(booking2.getId());

    }


    @Test
    void itShouldNotGetItemIfUserDoesNotExist() {
        // Given

        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User commentator1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        User commentator2 = User.builder()
                .id(3L)
                .name("User3")
                .email("Useremail2@yandex.ru")
                .build();

        Item itemToReturn = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .item(itemToReturn)
                .user(commentator1)
                .text("Comment1 text")
                .created(LocalDateTime.now().minusMinutes(10))
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .item(itemToReturn)
                .user(commentator2)
                .text("Comment2 text")
                .created(LocalDateTime.now().minusMinutes(5))
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .item(itemToReturn)
                .booker(commentator1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(10))
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .item(itemToReturn)
                .booker(commentator2)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(15))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getItemById(1L, 1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");

    }

    @Test
    void itShouldNotGetItemIfItemDoesNotExist() {
        // Given

        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User commentator1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        User commentator2 = User.builder()
                .id(3L)
                .name("User3")
                .email("Useremail2@yandex.ru")
                .build();

        Item itemToReturn = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .item(itemToReturn)
                .user(commentator1)
                .text("Comment1 text")
                .created(LocalDateTime.now().minusMinutes(10))
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .item(itemToReturn)
                .user(commentator2)
                .text("Comment2 text")
                .created(LocalDateTime.now().minusMinutes(5))
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .item(itemToReturn)
                .booker(commentator1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(10))
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .item(itemToReturn)
                .booker(commentator2)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(15))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getItemById(1L, 1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found");

    }

    @Test
    void itShouldGetItemByIdForRandomUser() {
        // Given

        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User commentator1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        User commentator2 = User.builder()
                .id(3L)
                .name("User3")
                .email("Useremail2@yandex.ru")
                .build();

        Item itemToReturn = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .item(itemToReturn)
                .user(commentator1)
                .text("Comment1 text")
                .created(LocalDateTime.now().minusMinutes(10))
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .item(itemToReturn)
                .user(commentator2)
                .text("Comment2 text")
                .created(LocalDateTime.now().minusMinutes(5))
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.of(commentator2));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemToReturn));
        when(commentRepository.findAllByItem(1L)).thenReturn(List.of(comment1, comment2));
        // When
        ItemDtoWithBookingsAndComments itemFound = underTest.getItemById(2L, 1L);
        // Then
        assertThat(itemFound.getName()).isEqualTo(itemToReturn.getName());
        assertThat(itemFound.getDescription()).isEqualTo(itemToReturn.getDescription());
        assertThat(itemFound.getComments()).hasSize(2);
        assertThat(itemFound.getComments().get(0).getId()).isEqualTo(comment1.getId());
        assertThat(itemFound.getComments().get(1).getId()).isEqualTo(comment2.getId());

    }

    @Test
    void itShouldGetOwnerItems() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User commentator1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        User commentator2 = User.builder()
                .id(3L)
                .name("User3")
                .email("Useremail2@yandex.ru")
                .build();

        Item itemToReturn1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemToReturn2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .item(itemToReturn1)
                .user(commentator1)
                .text("Comment1 text")
                .created(LocalDateTime.now().minusMinutes(10))
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .item(itemToReturn2)
                .user(commentator2)
                .text("Comment2 text")
                .created(LocalDateTime.now().minusMinutes(5))
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .item(itemToReturn1)
                .booker(commentator1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(10))
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .item(itemToReturn2)
                .booker(commentator2)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(15))
                .build();

        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
        Page<Item> page = new PageImpl<Item>(List.of(itemToReturn1, itemToReturn2), pageable, 2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        //when(itemRepository.findAllByOwnerIdOrderByIdAsc(1L, PageRequest.of(0,1))).thenReturn(List.of(itemToReturn1, itemToReturn2));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(1L, pageable)).thenReturn(page);
        when(commentRepository.findAllByItem(1L)).thenReturn(List.of(comment1));
        when(commentRepository.findAllByItem(2L)).thenReturn(List.of(comment2));
        when(bookingRepository.findAllBookingsByItemId(1L)).thenReturn(List.of(booking1));
        when(bookingRepository.findAllBookingsByItemId(2L)).thenReturn(List.of(booking2));
        // When
        List<ItemDtoWithBookingsAndComments> ownerItems = underTest.getOwnerItems(1L, 0, 1);
        // Then
        assertThat(ownerItems)
                .hasSize(2);
        assertThat(ownerItems.get(0).getId()).isEqualTo(itemToReturn1.getId());
        assertThat(ownerItems.get(1).getId()).isEqualTo(itemToReturn2.getId());
        assertThat(ownerItems.get(0).getComments().get(0).getId()).isEqualTo(comment1.getId());
        assertThat(ownerItems.get(1).getComments().get(0).getId()).isEqualTo(comment2.getId());
        assertThat(ownerItems.get(0).getLastBooking().getId()).isEqualTo(booking1.getId());
        assertThat(ownerItems.get(1).getNextBooking().getId()).isEqualTo(booking2.getId());
    }

    @Test
    void itShouldNotGetOwnerItemsWhenUserDoesNotExist() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User commentator1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        User commentator2 = User.builder()
                .id(3L)
                .name("User3")
                .email("Useremail2@yandex.ru")
                .build();

        Item itemToReturn1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemToReturn2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .item(itemToReturn1)
                .user(commentator1)
                .text("Comment1 text")
                .created(LocalDateTime.now().minusMinutes(10))
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .item(itemToReturn2)
                .user(commentator2)
                .text("Comment2 text")
                .created(LocalDateTime.now().minusMinutes(5))
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .item(itemToReturn1)
                .booker(commentator1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(10))
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .item(itemToReturn2)
                .booker(commentator2)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(15))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getOwnerItems(1L, 0, 1))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void itShouldSearchAvailableItems() {
        // Given
        User user = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail2@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(user)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description text")
                .isAvailable(true)
                .owner(user)
                .build();

        Item item3 = Item.builder()
                .id(3L)
                .name("Item3")
                .description("Item2 TEXT")
                .isAvailable(true)
                .owner(user)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Item> page = new PageImpl<Item>(List.of(item2, item3), pageable, 2);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findAllByText("text", pageable)).thenReturn(page);
        // When
        List<ItemDto> availableItems = underTest.searchAvailableItems(2L, "text", 0, 2);
        // Then
        assertThat(availableItems).hasSize(2);
        assertThat(availableItems.get(0).getId()).isEqualTo(item2.getId());
        assertThat(availableItems.get(1).getId()).isEqualTo(item3.getId());
    }

    @Test
    void itShouldAddComment() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User commentator1 = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        CommentDtoToCreate commentToCreate = new CommentDtoToCreate("Good item");

        Comment comment1 = Comment.builder()
                .id(1L)
                .item(item)
                .user(commentator1)
                .text("Good item")
                .created(LocalDateTime.now())
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .item(item)
                .booker(commentator1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(10))
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(commentator1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(any(Long.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking1));
        when(commentRepository.save(any())).thenReturn(comment1);
        // When
        CommentDtoToReturn commentDtoToReturn = underTest.addComment(2L, 1L, commentToCreate);
        // Then
        assertThat(commentDtoToReturn.getAuthorName()).isEqualTo(commentator1.getName());
        assertThat(commentDtoToReturn.getText()).isEqualTo("Good item");

    }
}


