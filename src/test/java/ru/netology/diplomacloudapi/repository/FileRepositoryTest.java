package ru.netology.diplomacloudapi.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.netology.diplomacloudapi.entity.FileEntity;
import ru.netology.diplomacloudapi.entity.Token;
import ru.netology.diplomacloudapi.entity.User;
import ru.netology.diplomacloudapi.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class FileRepositoryTest {

    @Autowired
    private FileRepository underTest;

    @Autowired
    private UserRepository userRepository;

    private final String fileName = "testFile.txt";

    private int userId;

    private FileEntity fileEntity;

    @BeforeEach
    public void setUp() {
        List<Token> tokens = new ArrayList<>();
        byte[] content = {1, 2, 3};

        User newUser = User.builder()
                .login("TestUser")
                .password("TestUser")
                .role(Role.USER)
                .tokens(tokens)
                .build();

        User user = userRepository.save(newUser);

        userId = user.getId();

        FileEntity newFileEntity = FileEntity.builder()
                .name(fileName)
                .date(LocalDateTime.now())
                .user(user)
                .sizeInBytes(123)
                .contentType("text/plain")
                .content(content)
                .build();

        fileEntity = underTest.save(newFileEntity);
    }

    @AfterEach
    public void tearDown() {
        underTest.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldFindFileEntityByName() {

        FileEntity expected = underTest.findByName(fileName).get();

        assertThat(expected)
                .usingRecursiveComparison()
                .isEqualTo(fileEntity);
    }

    @Test
    void shouldReturnTrueIfFileExistsByName() {

        boolean expected = underTest.existsByName(fileName);

        assertTrue(expected);
    }

    @Test
    void shouldFindAllFilesByUserId() {
        Pageable pageable = PageRequest.ofSize(3);

        List<FileEntity> result = underTest.findAllByUserId(userId, pageable);

        assertThat(result.size()).isEqualTo(1);

        assertThat(result.get(0))
                .usingRecursiveComparison()
                .isEqualTo(fileEntity);
    }

}