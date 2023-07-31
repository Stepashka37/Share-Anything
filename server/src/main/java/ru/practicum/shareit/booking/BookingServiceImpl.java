package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.dtoToCreateToModel;
import static ru.practicum.shareit.booking.BookingMapper.modelToDto;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingDtoToCreate bookingDto) {
        if (bookingDto.getStart().isEqual(bookingDto.getEnd()) ||
                bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Not valid start and end date");
        }
        User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Item itemFromDb = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
        if (itemFromDb.getOwner().getId() == userId) throw new UserNotFoundException("Current user is item host");
        if (!itemFromDb.getIsAvailable()) throw new ItemNotAvailableException("Item is not available");
        Booking bookingToCreate = dtoToCreateToModel(bookingDto);
        bookingToCreate.setBooker(userFromDb);
        bookingToCreate.setStatus(BookingStatus.WAITING);
        bookingToCreate.setItem(itemFromDb);
        Booking bookingCreated = bookingRepository.save(bookingToCreate);
        log.info("Создали бронирование с id{}", bookingCreated.getId());
        return modelToDto(bookingCreated);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        if ((booking.getStatus() == BookingStatus.APPROVED && approved == true)
                || (booking.getStatus() == BookingStatus.REJECTED && approved == false)) {
            throw new ValidationException("Item status is already set");
        }
        if (booking.getItem().getOwner().getId() == userId) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
                booking = bookingRepository.save(booking);
                log.info("Пользователь с id{} одобрил бронирование с id{}", userId, booking.getId());
            } else {
                booking.setStatus(BookingStatus.REJECTED);
                booking = bookingRepository.save(booking);
                log.info("Пользователь с id{} не одобрил бронирование с id{}", userId, booking.getId());
            }
        } else {
            throw new HostNotFoundException("User is not the item host");
        }
        return modelToDto(booking);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new UserNotFoundException("Data is available for booker or " +
                    "for item owner");
        }
        log.info("Пользователь с id{} получил данные бронирования с id{}", userId, bookingId);
        return modelToDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, String stateAsString, Integer from, Integer size) {
        User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        State state  = State.valueOf(stateAsString);
       /* try {
            state = State.valueOf(stateAsString);
        } catch (Exception e) {
            throw new StateValidationException("Unknown state: UNSUPPORTED_STATUS");
        }*/
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findAllCurrentBookingsByUser(userId, LocalDateTime.now(), pageable);
                break;
            case PAST:
                result = bookingRepository.findAllPastBookingsByUser(userId, LocalDateTime.now(), pageable);
                break;
            case REJECTED:
                result = bookingRepository.findAllRejectedBookingsByUser(userId, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findAllFutureBookingsByUser(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                result = bookingRepository.findAllWaitingBookingsByUser(userId, pageable);
                break;
            default:
                result = Page.empty();
        }
        log.info("Получили все бронирования пользователя с id{}", userId);
        return result.stream()
                .map(x -> modelToDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllUserItemsBookings(long userId, String stateAsString, Integer from, Integer size) {
        User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        int page = from / size;
        State state  = State.valueOf(stateAsString);
        /*try {
             state = State.valueOf(stateAsString);
        } catch (Exception e) {
            throw new StateValidationException("Unknown state: UNSUPPORTED_STATUS");
        }*/

        Page<Booking> result;
        Pageable pageable2 = PageRequest.of(page, size, Sort.by("start").ascending());
        switch (state) {
            case ALL:
                result = bookingRepository.findAllItemsBookings(userId, pageable2);
                break;
            case CURRENT:
                result = bookingRepository.findAllItemsCurrentBookings(userId, LocalDateTime.now(), pageable2);
                break;
            case PAST:
                result = bookingRepository.findAllItemsPastBookings(userId, LocalDateTime.now(), pageable2);
                break;
            case REJECTED:
                result = bookingRepository.findAllItemsRejectedBookings(userId, pageable2);
                break;
            case FUTURE:
                result = bookingRepository.findAllItemsFutureBookings(userId, LocalDateTime.now(), pageable2);
                break;
            case WAITING:
                result = bookingRepository.findAllItemsWaitingBookings(userId, pageable2);
                break;
            default:
                result = Page.empty();
        }
        log.info("Получили список бронирований всех предметов пользователя с id{}", userId);
        return result.stream()
                .map(x -> modelToDto(x))
                .collect(Collectors.toList());
    }
}
