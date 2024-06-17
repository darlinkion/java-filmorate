package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(JdbcUserRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcUserRepositoryTest {
private final JdbcUserRepository jdbcUserRepository;

private static final int TEST_USER_ID=1;
static User getTestUser(){
    User user=new User();
    user.setId(TEST_USER_ID);
    user.setEmail("da@mail.ru");
    return  user;
}
    @Test
    void create() {

        User userOptional=jdbcUserRepository.get(TEST_USER_ID);
        assertThat(userOptional)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestUser());
    }

    @Test
    void update() {

    }

    @Test
    void getAll() {
    }

    @Test
    void get() {
    }
}