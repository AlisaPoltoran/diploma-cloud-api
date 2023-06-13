package ru.netology.diplomacloudapi.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.netology.diplomacloudapi.entity.*;
import ru.netology.diplomacloudapi.entity.enums.Role;
import ru.netology.diplomacloudapi.entity.enums.TokenType;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class TokenRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository underTest;

    int userId;

    List<Token> tokens;

    @BeforeEach
    void setUp() {

        User newUser = User.builder()
                .login("TestUser")
                .password("TestUser")
                .role(Role.USER)
                .build();

        User user = userRepository.save(newUser);

        userId = user.getId();

        Token newTokenFirst = Token.builder()
                .token("token0")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();

        Token newTokenSecond = Token.builder()
                .token("token1")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();

        Token newTokenThird = Token.builder()
                .token("token2")
                .tokenType(TokenType.BEARER)
                .expired(true)
                .revoked(true)
                .user(user)
                .build();

       tokens = List.of(newTokenFirst, newTokenSecond, newTokenThird);

        underTest.saveAll(tokens);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldFindAllValidTokensByUser() {
        List<Token> expected = underTest.findAllValidTokensByUser(userId);

        assertThat(expected.size()).isEqualTo(2);

        assertThat(expected.get(0))
                .usingRecursiveComparison()
                .isEqualTo(tokens.get(0));

        assertThat(expected.get(1))
                .usingRecursiveComparison()
                .isEqualTo(tokens.get(1));
    }

    @Test
    void ShouldFindTokenByToken() {
        String token = "token0";

        Token expected = underTest.findByToken(token).get();

        assertThat(expected)
                .usingRecursiveComparison()
                .isEqualTo(tokens.get(0));
    }
}