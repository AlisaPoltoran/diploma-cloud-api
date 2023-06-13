package ru.netology.diplomacloudapi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.netology.diplomacloudapi.entity.enums.Role;
import ru.netology.diplomacloudapi.entity.User;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository underTest;

    @Test
    void shouldFindUserByLogin() {

        String login = "testUser";
        User user = User.builder()
                .login(login)
                .password("testUser")
                .role(Role.USER)
                .tokens(new ArrayList<>())
                .build();

        underTest.save(user);

        User expected = underTest.findByLogin(login).get();

        assertThat(expected)
                .usingRecursiveComparison()
                .isEqualTo(user);
    }
}