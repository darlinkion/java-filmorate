package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(JdbcUserRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcUserRepositoryTest {
    private static final int TEST_USER_ID = 6;
    private final JdbcUserRepository jdbcUserRepository;

    static User getTestUser() {
        User user = new User();
        user.setEmail("qwer95@mail.ru");
        user.setLogin("pirog95");
        user.setName("ivan99");
        user.setBirthday(LocalDate.of(2000, 4, 11));
        return user;
    }

    static User getTestUserStandart() {
        User user = new User();
        user.setId(1);
        user.setEmail("qwer@mail.ru");
        user.setLogin("pirog");
        user.setName("ivan");
        user.setBirthday(LocalDate.of(2000, 4, 11));
        return user;
    }

    @Test
    void create() {
        User userForTest = getTestUser();
        User user = jdbcUserRepository.create(userForTest);
        userForTest.setId(TEST_USER_ID);
        assertThat(user)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(userForTest);
    }

    @Test
    void get() {
        User userGet = jdbcUserRepository.get(1);

        User user = getTestUserStandart();

        assertThat(user)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(userGet);

    }

    @Test
    void update() {
        User user = getTestUserStandart();
        user.setName("Alexander");

        User userUpdage = jdbcUserRepository.update(user);
        assertThat(userUpdage)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(user);
    }

    @Test
    void getAll() {
        User user1 = getTestUserStandart();

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("bestuser@yandex.ru");
        user2.setLogin("nagibator");
        user2.setName("alex");
        user2.setBirthday(LocalDate.of(1900, 4, 11));

        User user3 = getTestUserStandart();
        user3.setId(3);
        user3.setEmail("afsafas@gmail.ru");
        user3.setLogin("Lis");
        user3.setName("serj");
        user3.setBirthday(LocalDate.of(2000, 4, 11));

        User user4 = getTestUserStandart();
        user4.setId(4);
        user4.setEmail("fasfar@rambler.ru");
        user4.setLogin("soer");
        user4.setName("nikita");
        user4.setBirthday(LocalDate.of(1990, 4, 1));

        User user5 = getTestUserStandart();
        user5.setId(5);
        user5.setEmail("qaffsdfa@mail.ru");
        user5.setLogin("jekastar");
        user5.setName("val");
        user5.setBirthday(LocalDate.of(2000, 9, 8));

        List<User> usersList = new ArrayList<>();
        usersList.add(user1);
        usersList.add(user2);
        usersList.add(user3);
        usersList.add(user4);
        usersList.add(user5);

        List<User> usersFromDB = jdbcUserRepository.getAll();

        assertThat(usersList)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(usersFromDB);
    }
}