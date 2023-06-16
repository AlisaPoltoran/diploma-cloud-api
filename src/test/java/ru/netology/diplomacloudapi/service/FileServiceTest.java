package ru.netology.diplomacloudapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomacloudapi.dto.NewFileName;
import ru.netology.diplomacloudapi.dto.SuccessfulResponse;
import ru.netology.diplomacloudapi.entity.FileEntity;
import ru.netology.diplomacloudapi.entity.User;
import ru.netology.diplomacloudapi.entity.enums.Role;
import ru.netology.diplomacloudapi.exception.ErrorInputData;
import ru.netology.diplomacloudapi.repository.FileRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    private FileService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new FileService(fileRepository);
    }

    @Test
    void shouldSaveFileToDB() throws IOException {

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .thenReturn(new User(1,
                        "testUser",
                        "testUser",
                        Role.USER,
                        new ArrayList<>()));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        underTest.saveFile(file);

        int sizeInBytes = (int) file.getSize();
        FileEntity fileEntity = FileEntity.builder()
                .name(file.getOriginalFilename())
                .date(LocalDateTime.now())
                .sizeInBytes(sizeInBytes)
                .user(user)
                .content(file.getBytes())
                .contentType(file.getContentType())
                .build();

        ArgumentCaptor<FileEntity> fileEntityArgumentCaptor = ArgumentCaptor.forClass(FileEntity.class);

        verify(fileRepository).save(fileEntityArgumentCaptor.capture());

        FileEntity capturedFileEntity = fileEntityArgumentCaptor.getValue();

        assertThat(capturedFileEntity)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(fileEntity);
    }

    @Test
    void shouldThrowErrorInputDataExceptionInSaveFileWhenFileNameIsTaken() {

        MultipartFile file = Mockito.mock(MultipartFile.class);

        given(fileRepository.existsByName(file.getOriginalFilename())).willReturn(true);

        assertThatThrownBy(() -> underTest.saveFile(file))
                .isInstanceOf(ErrorInputData.class)
                .hasMessage("Error input data: the file " + file.getOriginalFilename() +
                        " already exists in the database, please choose another file name");

        verify(fileRepository, never()).save(any());
    }

    @Test
    void shouldThrowErrorInputDataExceptionInSaveFileWhenFileNameIsEmpty() {

        MultipartFile file = Mockito.mock(MultipartFile.class);

        given(file.getOriginalFilename()).willReturn(null);

        assertThatThrownBy(() -> underTest.saveFile(file))
                .isInstanceOf(ErrorInputData.class)
                .hasMessage("Error input data: no file name");

        verify(fileRepository, never()).save(any());
    }

    @Test
    void shouldDeleteFile() {

        String fileName = "test.txt";

        FileEntity fileEntity = FileEntity.builder().id(1).build();

        when(fileRepository.findByName(fileName)).thenReturn(Optional.of(fileEntity));

        SuccessfulResponse response = underTest.deleteFile(fileName);

        ArgumentCaptor<Integer> idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(fileRepository, times(1)).findByName(fileName);
        verify(fileRepository, times(1)).deleteById(fileEntity.getId());
        verify(fileRepository).deleteById(idArgumentCaptor.capture());
        assertThat(idArgumentCaptor.getValue()).isEqualTo(fileEntity.getId());
        assertEquals("The file " + fileName + " was successfully deleted", response.getMsg());
    }

    @Test
    void shouldThrowErrorInputDataExceptionInDeleteFileWhenFileIsNoInDB() {

        String fileName = "test.txt";

        given(fileRepository.findByName(fileName)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteFile(fileName))
                .isInstanceOf(ErrorInputData.class)
                .hasMessage("Error input data: the file " + fileName + " is not found in the database");

        verify(fileRepository, never()).deleteById(any());
    }

    @Test
    void getFile() {

        String fileName = "test.txt";
        byte[] fileContent = "test".getBytes();
        String fileContentType = "text/plain";
        FileEntity fileEntity = FileEntity.builder()
                .contentType(fileContentType)
                .content(fileContent)
                .build();

        when(fileRepository.findByName(fileName)).thenReturn(Optional.of(fileEntity));

        ResponseEntity<Resource> response = underTest.getFile(fileName);

        verify(fileRepository, times(1)).findByName(fileName);
        assertSame(fileContent, ((ByteArrayResource) response.getBody()).getByteArray());
        assertThat(response.getHeaders().getContentType())
                .usingRecursiveComparison()
                .isEqualTo(MediaType.valueOf(fileContentType));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldThrowErrorInputDataExceptionInGetFileWhenFileIsNoInDB() {

        String fileName = "test.txt";

        given(fileRepository.findByName(fileName)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteFile(fileName))
                .isInstanceOf(ErrorInputData.class)
                .hasMessage("Error input data: the file " + fileName + " is not found in the database");
    }

    @Test
    void shouldEditFileName() {

        String fileName = "test.txt";
        NewFileName newFileName = new NewFileName();
        newFileName.setFileName("newTest.txt");
        FileEntity fileEntity = FileEntity.builder().name(fileName).build();

        when(fileRepository.findByName(fileName)).thenReturn(Optional.of(fileEntity));
        when(fileRepository.existsByName(newFileName.getFileName())).thenReturn(false);

        SuccessfulResponse response = underTest.editFile(fileName, newFileName);

        ArgumentCaptor<FileEntity> fileEntityArgumentCaptor = ArgumentCaptor.forClass(FileEntity.class);

        verify(fileRepository, times(1)).findByName(fileName);
        verify(fileRepository, times(1)).existsByName(newFileName.getFileName());
        verify(fileRepository, times(1)).save(fileEntity);
        verify(fileRepository).save(fileEntityArgumentCaptor.capture());
        assertThat(fileEntityArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .comparingOnlyFields("name")
                .isEqualTo(fileEntity);
        assertEquals("File was successfully renamed", response.getMsg());
    }

    @Test
    void shouldThrowErrorInputDataExceptionInEditFileWhenNewFileNameIsInDB() {

        String fileName = "test.txt";
        NewFileName newFileName = new NewFileName();
        newFileName.setFileName("newTest.txt");

        when(fileRepository.findByName(fileName)).thenReturn(Optional.of(new FileEntity()));
        when(fileRepository.existsByName(newFileName.getFileName())).thenReturn(true);

        assertThatThrownBy(() -> underTest.editFile(fileName, newFileName))
                .isInstanceOf(ErrorInputData.class)
                .hasMessage("Error input data: the file " + newFileName.getFileName() +
                        " already exists in the database, please choose another file name");
        verify(fileRepository, never()).save(any());
    }

    @Test
    void shouldGetAllFiles() {

        int limit = 3;
        int userId = 1;

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .thenReturn(new User(1,
                        "testUser",
                        "testUser",
                        Role.USER,
                        new ArrayList<>()));

        underTest.getAllFiles(limit);

        verify(fileRepository).findAllByUserId(userId, Pageable.ofSize(3));
    }
}