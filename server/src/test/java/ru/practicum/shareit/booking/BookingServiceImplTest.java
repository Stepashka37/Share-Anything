package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
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
class BookingServiceImplTest {

    @Captor
    private ArgumentCaptor<Booking> argumentCaptor;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void itShouldCreateBooking() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.WAITING)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(bookingDtoToCreate.getItemId())).thenReturn(Optional.of(itemFromDb));
        when(bookingRepository.save(any())).thenReturn(bookingToBeSaved);

        // When
        BookingDto bookingCreated = underTest.createBooking(booker.getId(), bookingDtoToCreate);
        // Then
        then(bookingRepository).should().save(argumentCaptor.capture());
        assertThat(bookingCreated).isEqualToComparingFieldByField(created);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void itShouldNotCreateBookingWhenStartAndEndAreSame() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.of(2023, 12, 02, 14, 00, 00))
                .end(LocalDateTime.of(2023, 12, 02, 14, 00, 00))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.WAITING)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();

        // When
        // Then
        assertThatThrownBy(() -> underTest.createBooking(booker.getId(), bookingDtoToCreate))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Not valid start and end date");
        then(userRepository).should(never()).findById(any());
        then(bookingRepository).should(never()).save(any());
        then(itemRepository).should(never()).findById(any());
    }

    @Test
    void itShouldNotCreateBookingWhenEndBeforeStart() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.of(2023, 12, 02, 14, 00, 00))
                .end(LocalDateTime.of(2023, 12, 02, 13, 00, 00))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.WAITING)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();

        // When
        // Then
        assertThatThrownBy(() -> underTest.createBooking(booker.getId(), bookingDtoToCreate))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Not valid start and end date");
        then(userRepository).should(never()).findById(any());
        then(bookingRepository).should(never()).save(any());
        then(itemRepository).should(never()).findById(any());
    }

    @Test
    void itShouldNotCreateBookingWhenUserNotFound() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.WAITING)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.createBooking(booker.getId(), bookingDtoToCreate))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
        then(bookingRepository).should(never()).save(any());
        then(itemRepository).should(never()).findById(any());
    }

    @Test
    void itShouldNotCreateBookingWhenItemNotFound() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.WAITING)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.createBooking(booker.getId(), bookingDtoToCreate))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found");
        then(bookingRepository).should(never()).save(any());

    }

    @Test
    void itShouldNotCreateBookingWhenBookerBooksHisItem() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(booker)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.WAITING)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemFromDb));

        // When
        // Then
        assertThatThrownBy(() -> underTest.createBooking(booker.getId(), bookingDtoToCreate))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Current user is item host");
        then(bookingRepository).should(never()).save(any());

    }

    @Test
    void itShouldNotCreateBookingWhenItemIsNotAvailable() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(false)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.WAITING)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemFromDb));

        // When
        // Then
        assertThatThrownBy(() -> underTest.createBooking(booker.getId(), bookingDtoToCreate))
                .isInstanceOf(ItemNotAvailableException.class)
                .hasMessageContaining("Item is not available");
        then(bookingRepository).should(never()).save(any());

    }

    @Test
    void itShouldApproveBooking() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();

        Booking bookingApproved = Booking.builder()
                .id(1L)
                .start(bookingToBeSaved.getStart())
                .end(bookingToBeSaved.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingToBeSaved));
        when(bookingRepository.save(any())).thenReturn(bookingApproved);
        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));

        // When
        BookingDto bookingCreated = underTest.approveBooking(itemOwner.getId(), bookingToBeSaved.getId(), true);
        // Then
        then(bookingRepository).should().save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualToComparingFieldByField(bookingApproved);
        assertThat(bookingCreated.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void itShouldNotApproveBookingWhenUserNotFound() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();

        Booking bookingApproved = Booking.builder()
                .id(1L)
                .start(bookingToBeSaved.getStart())
                .end(bookingToBeSaved.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.approveBooking(itemOwner.getId(), bookingToBeSaved.getId(), true))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
        then(bookingRepository).should(never()).save(any());
    }

    @Test
    void itShouldNotApproveBookingWhenBookingNotFound() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();

        Booking bookingApproved = Booking.builder()
                .id(1L)
                .start(bookingToBeSaved.getStart())
                .end(bookingToBeSaved.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.approveBooking(itemOwner.getId(), bookingToBeSaved.getId(), true))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("Booking not found");
        then(bookingRepository).should(never()).save(any());
    }

    @Test
    void itShouldNotApproveBookingWhenStatusIsAlreadyApproved() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb)
                .booker(booker)
                .build();

        Booking bookingApproved = Booking.builder()
                .id(1L)
                .start(bookingToBeSaved.getStart())
                .end(bookingToBeSaved.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingToBeSaved));

        // When
        // Then
        assertThatThrownBy(() -> underTest.approveBooking(itemOwner.getId(), bookingToBeSaved.getId(), true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Item status is already set");
        then(bookingRepository).should(never()).save(any());
    }

    @Test
    void itShouldNotApproveBookingWhenStatusIsAlreadyRejected() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.REJECTED)
                .item(itemFromDb)
                .booker(booker)
                .build();

        Booking bookingApproved = Booking.builder()
                .id(1L)
                .start(bookingToBeSaved.getStart())
                .end(bookingToBeSaved.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingToBeSaved));

        // When
        // Then
        assertThatThrownBy(() -> underTest.approveBooking(itemOwner.getId(), bookingToBeSaved.getId(), false))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Item status is already set");
        then(bookingRepository).should(never()).save(any());
    }

    @Test
    void itShouldNotApproveBookingWhenUserIsNotItemHost() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();

        Booking bookingApproved = Booking.builder()
                .id(1L)
                .start(bookingToBeSaved.getStart())
                .end(bookingToBeSaved.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingToBeSaved));

        // When
        // Then
        assertThatThrownBy(() -> underTest.approveBooking(booker.getId(), bookingToBeSaved.getId(), true))
                .isInstanceOf(HostNotFoundException.class)
                .hasMessageContaining("User is not the item host");
        then(bookingRepository).should(never()).save(any());
    }

    @Test
    void itShouldApproveBookingWhenUserIsItemHost() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();

        Booking bookingApproved = Booking.builder()
                .id(1L)
                .start(bookingToBeSaved.getStart())
                .end(bookingToBeSaved.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingToBeSaved));
        when(bookingRepository.save(any())).thenReturn(bookingApproved);

        // When
        BookingDto bookingDto = underTest.approveBooking(itemOwner.getId(), bookingToBeSaved.getId(), true);
        // Then
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void itShouldRejectBookingWhenUserIsItemHost() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.WAITING)
                .item(itemFromDb)
                .booker(booker)
                .build();

        Booking bookingRejected = Booking.builder()
                .id(1L)
                .start(bookingToBeSaved.getStart())
                .end(bookingToBeSaved.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.REJECTED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingToBeSaved));
        when(bookingRepository.save(any())).thenReturn(bookingRejected);

        // When
        BookingDto bookingDto = underTest.approveBooking(itemOwner.getId(), bookingToBeSaved.getId(), false);
        // Then
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void itShouldGetBooking() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.APPROVED)
                .item(itemFromDb)
                .booker(booker)
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingToBeSaved));
        // When
        BookingDto bookingDto = underTest.getBooking(2L, 1L);
        // Then
        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto).isEqualToComparingFieldByField(created);
    }

    @Test
    void itShouldNotGetBookingWhenUserNotFound() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.APPROVED)
                .item(itemFromDb)
                .booker(booker)
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getBooking(2L, 1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void itShouldNotGetBookingWhenBookingNotFound() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.APPROVED)
                .item(itemFromDb)
                .booker(booker)
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getBooking(2L, 1L))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    void itShouldNotGetBookingWhenUserIsNotHostOrBooker() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        User notBookerOrOwner = User.builder()
                .id(3L)
                .name("User2")
                .email("Useremail2@yandex.ru")
                .build();

        Item itemFromDb = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        BookingDtoToCreate bookingDtoToCreate = BookingDtoToCreate.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemFromDb.getId())
                .build();

        BookingDto created = BookingDto.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .booker(booker)
                .item(itemFromDb)
                .status(BookingStatus.APPROVED)
                .build();

        Booking bookingToBeSaved = Booking.builder()
                .id(1L)
                .start(bookingDtoToCreate.getStart())
                .end(bookingDtoToCreate.getEnd())
                .status(BookingStatus.APPROVED)
                .item(itemFromDb)
                .booker(booker)
                .build();


        when(userRepository.findById(3L)).thenReturn(Optional.of(notBookerOrOwner));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingToBeSaved));
        // When
        // Then
        assertThatThrownBy(() -> underTest.getBooking(3L, 1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Data is available for booker or " +
                        "for item owner");
    }

    @Test
    void itShouldGetALLUserBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(2L, pageable)).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getUserBookings(2L, "ALL", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetCURRENTUserBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(5))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllCurrentBookingsByUser(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getUserBookings(2L, "CURRENT", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetPASTUserBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusMinutes(40))
                .end(LocalDateTime.now().minusMinutes(10))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(5))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllPastBookingsByUser(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getUserBookings(2L, "PAST", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetFUTUREUserBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllFutureBookingsByUser(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getUserBookings(2L, "FUTURE", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetREJECTEDUserBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.REJECTED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.REJECTED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllRejectedBookingsByUser(anyLong(), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getUserBookings(2L, "REJECTED", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetWAITINGUserBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.WAITING)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.WAITING)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllWaitingBookingsByUser(anyLong(), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getUserBookings(2L, "WAITING", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    /*@Test
    void itShouldNotGetUserBookingsWhenStatusNotSupported() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.WAITING)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.WAITING)
                .item(itemFromDb2)
                .booker(booker)
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        // When
        // Then
        assertThatThrownBy(() -> underTest.getUserBookings(2L, "UNSUPPORTED_STATUS", 0, 2))
                .isInstanceOf(StateValidationException.class)
                .hasMessageContaining("Unknown state: UNSUPPORTED_STATUS");
    }*/

    @Test
    void itShouldNotGetUserBookingsWhenUserNotFound() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.WAITING)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.WAITING)
                .item(itemFromDb2)
                .booker(booker)
                .build();


        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getUserBookings(2L, "UNSUPPORTED_STATUS", 0, 2))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void itShouldGetALLUserItemsBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findAllItemsBookings(any(), any())).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getAllUserItemsBookings(itemOwner.getId(), "ALL", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetCURRENTUserItemsBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(5))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(10))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findAllItemsCurrentBookings(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getAllUserItemsBookings(1L, "CURRENT", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetPASTUserItemsBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusMinutes(40))
                .end(LocalDateTime.now().minusMinutes(10))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusMinutes(30))
                .end(LocalDateTime.now().minusMinutes(5))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findAllItemsPastBookings(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getAllUserItemsBookings(1L, "PAST", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetFUTUREUserItemsBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.APPROVED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findAllItemsFutureBookings(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getAllUserItemsBookings(1L, "FUTURE", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetREJECTEDUserItemsBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.REJECTED)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.REJECTED)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findAllItemsRejectedBookings(anyLong(), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getAllUserItemsBookings(1L, "REJECTED", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    @Test
    void itShouldGetWAITINGUserItemsBookings() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.WAITING)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.WAITING)
                .item(itemFromDb2)
                .booker(booker)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Booking> page = new PageImpl<Booking>(List.of(bookingFromDb1, bookingFromDb2), pageable, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        when(bookingRepository.findAllItemsWaitingBookings(anyLong(), any(Pageable.class))).thenReturn(page);
        // When
        List<BookingDto> userBookings = underTest.getAllUserItemsBookings(1L, "WAITING", 0, 2);
        // Then
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings.get(0).getId()).isEqualTo(bookingFromDb1.getId());
        assertThat(userBookings.get(1).getId()).isEqualTo(bookingFromDb2.getId());
    }

    /*@Test
    void itShouldNotGetUserItemsBookingsWhenStatusNotSupported() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.WAITING)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.WAITING)
                .item(itemFromDb2)
                .booker(booker)
                .build();


        when(userRepository.findById(1L)).thenReturn(Optional.of(itemOwner));
        // When
        // Then
        assertThatThrownBy(() -> underTest.getUserBookings(1L, "UNSUPPORTED_STATUS", 0, 2))
                .isInstanceOf(StateValidationException.class)
                .hasMessageContaining("Unknown state: UNSUPPORTED_STATUS");
    }*/

    @Test
    void itShouldNotGetUserItemsBookingsWhenUserNotFound() {
        // Given
        User itemOwner = User.builder()
                .id(1L)
                .name("User1")
                .email("Useremail@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("User2")
                .email("Useremail1@yandex.ru")
                .build();

        Item itemFromDb1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Item itemFromDb2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 Description")
                .isAvailable(true)
                .owner(itemOwner)
                .build();

        Booking bookingFromDb1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(10))
                .end(LocalDateTime.now().plusMinutes(40))
                .status(BookingStatus.WAITING)
                .item(itemFromDb1)
                .booker(booker)
                .build();

        Booking bookingFromDb2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusMinutes(50))
                .status(BookingStatus.WAITING)
                .item(itemFromDb2)
                .booker(booker)
                .build();


        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getUserBookings(1L, "ALL", 0, 2))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}