package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles(profiles = {"ci,test"})
@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;


    @Test
    void itShouldBeInitialized() {
        // Given
        // When
        // Then
        assertThat(underTest).isNotNull();

    }

    @Test
    void itShouldSaveUser() {
        // Given
        User user = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();
        // When
        User createdUser = underTest.save(user);

        // Then
        assertThat(createdUser).isEqualTo(user);

    }

    @Test
    void itShouldNotSaveUserWhenNameNull() {
        // Given
        User user = User.builder()
                .name(null)
                .email("Useremail@yandex.ru")
                .build();

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(user))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldNotSaveUserWhenEmailNull() {
        // Given
        User user = User.builder()
                .name("Username")
                .email(null)
                .build();

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(user))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldNotSaveUserWhenEmailExists() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        User user2 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        underTest.save(user1);
        // When
        // Then
        assertThatThrownBy(() -> underTest.save(user2))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldFindById() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        underTest.save(user1);
        // When
        // Then

        assertThat(underTest.findById(user1.getId()))
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).isEqualTo(user1);
                });

    }

    @Test
    void itShouldNotFindByIdWhenIdDoesNotExist() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        underTest.save(user1);
        // When
        // Then
        assertThat(underTest.findById(2L)).isNotPresent();

    }

    @Test
    void itShouldReturnTwoUsers() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        underTest.save(user1);

        User user2 = User.builder()
                .name("Username2")
                .email("Useremail2@yandex.ru")
                .build();

        underTest.save(user2);
        // When
        // Then
        assertThat(underTest.findAll()).hasSize(2)
                .contains(user1, user2);

    }

    @Test
    void itShouldDeleteById() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        underTest.save(user1);
        // When
        underTest.deleteById(user1.getId());
        // Then
        assertThat(underTest.findById(user1.getId())).isNotPresent();
    }

    @Test
    void itShouldNotDeleteByIdWhenDoesNotExist() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        underTest.save(user1);
        // When
        // Then
        assertThatThrownBy(() -> underTest.deleteById(2L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void itShouldDeleteAll() {
        // Given
        User user1 = User.builder()
                .name("Username")
                .email("Useremail@yandex.ru")
                .build();

        underTest.save(user1);

        User user2 = User.builder()
                .name("Username2")
                .email("Useremail2@yandex.ru")
                .build();

        underTest.save(user2);
        // When
        underTest.deleteAll();
        // Then
        assertThat(underTest.findAll())
                .isEmpty();
    }
}
