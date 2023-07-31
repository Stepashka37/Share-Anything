package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(profiles = {"ci,test"})
@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
@Transactional
class ItemRequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository underTest;

    @Test
    void itShouldBeInitialized() {
        // Given
        // When
        // Then
        assertThat(underTest).isNotNull();

    }

    @Test
    void itShouldFindAllByRequestorId() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@mail.ru")
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("Item request 1 description")
                .created(LocalDateTime.now())
                .requestor(user1)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("Item request 2 description")
                .created(LocalDateTime.now())
                .requestor(user1)
                .build();


        userRepository.save(user1);
        underTest.save(itemRequest1);
        underTest.save(itemRequest2);

        // When

        List<ItemRequest> itemRequests = underTest.findAllByRequestorIdOrderByCreatedAsc(user1.getId());
        // Then
        assertThat(itemRequests)
                .hasSize(2)
                .containsExactly(itemRequest1, itemRequest2);
    }

    @Test
    void itShouldFindAllUsersRequests() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@mail.ru")
                .build();

        User user2 = User.builder()
                .name("Username")
                .email("Useremail1@mail.ru")
                .build();

        User user3 = User.builder()
                .name("Username")
                .email("Useremail2@mail.ru")
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("Item request 1 description")
                .created(LocalDateTime.now())
                .requestor(user1)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("Item request 2 description")
                .created(LocalDateTime.now())
                .requestor(user2)
                .build();

        ItemRequest itemRequest3 = ItemRequest.builder()
                .description("Item request 3 description")
                .created(LocalDateTime.now())
                .requestor(user3)
                .build();


        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        underTest.save(itemRequest1);
        underTest.save(itemRequest2);
        underTest.save(itemRequest3);

        // When
        Pageable pageable = PageRequest.of(0, 2);
        Page<ItemRequest> itemRequests = underTest.findAllUserRequest(user1.getId(), pageable);
        // Then
        List<ItemRequest> itemRequestList = itemRequests.getContent();
        assertThat(itemRequestList)
                .hasSize(2)
                .contains(itemRequest2, itemRequest3);
    }
}