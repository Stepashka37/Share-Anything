package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ShareItAppTest {

    @Test
    void testMain() {
        assertDoesNotThrow(ShareItServer::new);
        assertDoesNotThrow(() -> ShareItServer.main(new String[]{}));
    }

}
