package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles(profiles = {"ci,test"})
@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
@Transactional
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository underTest;


    @Test
    void itShouldBeInitialized() {
        // Given
        // When
        // Then
        assertThat(underTest).isNotNull();

    }

    @Test
    void itShouldSaveItem() {
        // Given
        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user)
                .build();

        userRepository.save(user);

        // When

        Item itemCreated = underTest.save(item);

        // Then
        assertThat(itemCreated).isEqualTo(item);
    }

    @Test
    void itShouldNotSaveItemWhenNameNull() {
        // Given
        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        Item item = Item.builder()
                .name(null)
                .description("Item1 description")
                .isAvailable(true)
                .owner(user)
                .build();

        userRepository.save(user);

        // When

        // Then
        assertThatThrownBy(() -> underTest.save(item))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveItemWhenAvailableNull() {
        // Given
        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(null)
                .owner(user)
                .build();

        userRepository.save(user);

        // When

        // Then
        assertThatThrownBy(() -> underTest.save(item))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveItemWhenOwnerNull() {
        // Given

        Item item = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(null)
                .build();


        // When

        // Then
        assertThatThrownBy(() -> underTest.save(item))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldFindById() {
        // Given

        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user)
                .build();

        userRepository.save(user);

        underTest.save(item);


        // When

        // Then
        assertThat(underTest.findById(item.getId()))
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).isEqualTo(item);
                });
    }

    @Test
    void itShouldNotFindByIdWhenDoesNotExist() {
        // Given

        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user)
                .build();

        userRepository.save(user);

        underTest.save(item);


        // When

        // Then
        assertThat(underTest.findById(2L)).isNotPresent();

    }


    @Test
    void itShouldReturnTwoItems() {
        // Given

        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user)
                .build();

        Item item2 = Item.builder()
                .name("Item2")
                .description("Item2 description")
                .isAvailable(true)
                .owner(user)
                .build();

        userRepository.save(user);

        underTest.save(item1);
        underTest.save(item2);


        // When

        // Then
        assertThat(underTest.findAll()).hasSize(2)
                .contains(item1, item2);
    }

    @Test
    void itShouldDeleteById() {
        // Given
        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user)
                .build();


        userRepository.save(user);

        underTest.save(item1);
        // When

        underTest.deleteById(item1.getId());
        // Then
        assertThat(underTest.findById(item1.getId())).isNotPresent();

    }

    @Test
    void itShouldThrowWhenIdDoesNotExist() {
        // Given
        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user)
                .build();


        userRepository.save(user);

        underTest.save(item1);
        // When

        underTest.deleteById(item1.getId());
        // Then
        assertThatThrownBy(() -> underTest.deleteById(2L))
                .isInstanceOf(EmptyResultDataAccessException.class);

    }

    @Test
    void itShouldDeleteAll() {
        // Given

        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user)
                .build();

        Item item2 = Item.builder()
                .name("Item2")
                .description("Item2 description")
                .isAvailable(true)
                .owner(user)
                .build();

        userRepository.save(user);

        underTest.save(item1);
        underTest.save(item2);


        // When
        underTest.deleteAll();
        // Then
        assertThat(underTest.findAll()).hasSize(0);
    }

    @Test
    void itShouldFindAllByOwnerId() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        User user2 = User.builder()
                .name("Username")
                .email("Useremail1@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user1)
                .build();

        Item item2 = Item.builder()
                .name("Item2")
                .description("Item2 description")
                .isAvailable(true)
                .owner(user1)
                .build();

        Item item3 = Item.builder()
                .name("Item3")
                .description("Item3 description")
                .isAvailable(true)
                .owner(user2)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        underTest.save(item1);
        underTest.save(item2);
        underTest.save(item3);
        // When
        // Then
        Pageable pageable = PageRequest.of(0, 2);
        assertThat(underTest.findAllByOwnerIdOrderByIdAsc(user1.getId(), pageable))
                .hasSize(2)
                .containsExactly(item1, item2);
    }

    @Test
    void itShouldFindAllContainingText() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        User user2 = User.builder()
                .name("Username")
                .email("Useremail1@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .name("Text")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user1)
                .build();

        Item item2 = Item.builder()
                .name("Item2")
                .description("Text description")
                .isAvailable(true)
                .owner(user1)
                .build();

        Item item3 = Item.builder()
                .name("Item3")
                .description("Item3 description")
                .isAvailable(true)
                .owner(user2)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        underTest.save(item1);
        underTest.save(item2);
        underTest.save(item3);
        // When
        // Then
        Pageable pageable = PageRequest.of(0, 2);
        assertThat(underTest.findAllByText("text", pageable))
                .hasSize(2)
                .contains(item1, item2);

    }
}
