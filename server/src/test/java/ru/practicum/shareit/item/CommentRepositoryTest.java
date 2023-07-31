package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles(profiles = {"ci,test"})
@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
@Transactional
class CommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository underTest;

    @Test
    void itShouldBeInitialized() {
        // Given
        // When
        // Then
        assertThat(underTest).isNotNull();

    }

    @Test
    void itShouldSaveComment() {
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

        Comment comment = Comment.builder()
                .text("Comment text")
                .item(item)
                .user(user)
                .created(LocalDateTime.now())
                .build();

        userRepository.save(user);
        itemRepository.save(item);
        // When
        Comment commentCreated = underTest.save(comment);
        // Then
        assertThat(commentCreated).isEqualTo(comment);

    }

    @Test
    void itShouldNotSaveCommentWhenUserNull() {
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

        Comment comment = Comment.builder()
                .text("Comment text")
                .item(item)
                .user(null)
                .created(LocalDateTime.now())
                .build();

        userRepository.save(user);
        itemRepository.save(item);
        // When
        // Then
        assertThatThrownBy(() -> underTest.save(comment))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldNotSaveCommentWhenItemNull() {
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

        Comment comment = Comment.builder()
                .text("Comment text")
                .item(null)
                .user(user)
                .created(LocalDateTime.now())
                .build();

        userRepository.save(user);
        itemRepository.save(item);
        // When
        // Then
        assertThatThrownBy(() -> underTest.save(comment))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldNotSaveCommentWhenTextNull() {
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

        Comment comment = Comment.builder()
                .text(null)
                .item(item)
                .user(user)
                .created(LocalDateTime.now())
                .build();

        userRepository.save(user);
        itemRepository.save(item);
        // When
        // Then
        assertThatThrownBy(() -> underTest.save(comment))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldNotSaveCommentWhenCreatedTimeNull() {
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

        Comment comment = Comment.builder()
                .text("Comment text")
                .item(item)
                .user(user)
                .created(null)
                .build();

        userRepository.save(user);
        itemRepository.save(item);
        // When
        // Then
        assertThatThrownBy(() -> underTest.save(comment))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void itShouldFindAllCommentsByItemId() {
        // Given
        User user1 = User.builder()
                .name("Username1")
                .email("Useremail1@yandex.ru")
                .build();

        User user2 = User.builder()
                .name("Username2")
                .email("Useremail2@yandex.ru")
                .build();


        Item item = Item.builder()
                .name("Item1")
                .description("Item1 description")
                .isAvailable(true)
                .owner(user1)
                .build();


        Comment comment1 = Comment.builder()
                .text("Comment1 text")
                .item(item)
                .user(user1)
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .text("Comment2 text")
                .item(item)
                .user(user2)
                .created(LocalDateTime.now())
                .build();


        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item);
        underTest.save(comment1);
        underTest.save(comment2);
        // When
        // Then
        assertThat(underTest.findAllByItem(item.getId()))
                .hasSize(2)
                .contains(comment1, comment2);

    }


}