package ru.netology.diplomacloudapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomacloudapi.dto.FileEntityDto;
import ru.netology.diplomacloudapi.dto.NewFileName;
import ru.netology.diplomacloudapi.dto.SuccessfulResponse;
import ru.netology.diplomacloudapi.entity.FileEntity;
import ru.netology.diplomacloudapi.entity.User;
import ru.netology.diplomacloudapi.exception.ErrorInputData;
import ru.netology.diplomacloudapi.exception.InternalServerError;
import ru.netology.diplomacloudapi.repository.FileRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileRepository fileRepository;

    @Value("${my.file.directory}")
    private String directory;

    @Transactional
    public SuccessfulResponse saveFile(MultipartFile file) throws IOException {
        log.info("Request to save a file: {}", file.getOriginalFilename());
        if (fileRepository.existsByName(file.getOriginalFilename())) {
            throw new ErrorInputData("Error input data: the file " + file.getOriginalFilename() +
                    " already exists in the database, please choose another file name");
        }

        if (!new File(directory).exists()) {
            new File(directory).mkdirs();
        }

        String fileName = file.getOriginalFilename();
        int sizeInBytes = Math.toIntExact(file.getSize());

        if (fileName == null) {
            throw new ErrorInputData("Error input data: no file name");
        }

        Path uploadDirectory = Path.of(directory);
        Path filePath = uploadDirectory.resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath);
            log.info("The file {} was successfully saved", file.getOriginalFilename());
        } catch (IOException e) {
            throw new InternalServerError("Internal server error: " + fileName, e);
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        fileRepository.save(FileEntity.builder()
                .name(file.getOriginalFilename())
                .date(LocalDateTime.now())
                .filePath(filePath.toString())
                .sizeInBytes(sizeInBytes)
                .user(user)
                .build());

        return new SuccessfulResponse("The file " + file.getOriginalFilename() + " was uploaded successfully");
    }

    @Transactional
    public SuccessfulResponse deleteFile(String fileName) throws IOException {
        log.info("Request to delete the file: {}", fileName);
        FileEntity fileEntity = fileRepository.findByName(fileName)
                .orElseThrow(() -> new ErrorInputData("Error input data: the file " + fileName +
                        " is not found in the database"));

        Path filePath = Path.of(fileEntity.getFilePath());

        try {
            Files.delete(filePath);
            log.info("The file {} was successfully deleted", fileName);
        } catch (IOException e) {
            throw new InternalServerError("Internal server error: " + fileName, e);
        }

        fileRepository.deleteById(fileEntity.getId());

        return new SuccessfulResponse("The file " + fileName + " was successfully deleted");
    }

    public Resource getFile(String fileName) throws InternalServerError {
        log.info("Request to download the file: {}", fileName);
        FileEntity fileEntity = fileRepository.findByName(fileName)
                .orElseThrow(() -> new ErrorInputData("Error input data: the file " + fileName +
                        " is not found in the database"));

        Path filePath = Path.of(fileEntity.getFilePath());

        Resource resource;

        try {
            resource = new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new InternalServerError("Internal server error: " + fileName, e);
        }

        if (resource.exists()) {
            log.info("The file {} was successfully sent", fileName);
            return resource;
        } else {
            throw new InternalServerError("Internal server error: " + fileName,
                    new IOException("The resource does not exists"));
    }
}

    @Transactional
    public SuccessfulResponse editFile(String fileName, NewFileName newFileName) throws InternalServerError {
        log.info("Request to edit the file: {}", fileName);
        FileEntity fileEntity = fileRepository.findByName(fileName)
                .orElseThrow(() -> new ErrorInputData("Error input data: the file " + fileName +
                        " is not found in the database"));

        if (fileRepository.existsByName(newFileName.getFileName())) {
            throw new ErrorInputData("Error input data: the file " + newFileName.getFileName() +
                    " already exists in the database, please choose another file name");
        }

        Path filePath = Path.of(fileEntity.getFilePath());

        try {
            Files.move(filePath, filePath.resolveSibling(newFileName.getFileName()));
            log.info("The file {} was successfully renamed. The new file name is {}", fileName, newFileName.getFileName());
        } catch (IOException e) {
            throw new InternalServerError("Attempted rename of the file is failed", e);
        }
        fileEntity.setName(newFileName.getFileName());
        fileEntity.setFilePath(directory + System.getProperty("file.separator") + newFileName.getFileName());
        fileRepository.save(fileEntity);

        return new SuccessfulResponse("File was successfully renamed");
    }

    public List<FileEntityDto> getAllFiles(int limit) {
        log.info("Request to show {} file(s)", limit);
        Pageable firstPageWithTwoElements = PageRequest.of(0, limit);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return fileRepository.findAllByUserId(user.getId(), firstPageWithTwoElements).stream()
                .map(FileEntityDto::new)
                .collect(Collectors.toList());
    }
}
