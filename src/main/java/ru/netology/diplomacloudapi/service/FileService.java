package ru.netology.diplomacloudapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomacloudapi.dto.FileEntityDto;
import ru.netology.diplomacloudapi.dto.NewFileName;
import ru.netology.diplomacloudapi.dto.SuccessfulResponse;
import ru.netology.diplomacloudapi.entity.FileEntity;
import ru.netology.diplomacloudapi.entity.User;
import ru.netology.diplomacloudapi.exception.ErrorInputData;
import ru.netology.diplomacloudapi.repository.FileRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileRepository fileRepository;

    @Transactional
    public SuccessfulResponse saveFile(MultipartFile file) throws IOException {
        log.info("Request to save a file: {}", file.getOriginalFilename());
        if (fileRepository.existsByName(file.getOriginalFilename())) {
            throw new ErrorInputData("Error input data: the file " + file.getOriginalFilename() +
                    " already exists in the database, please choose another file name");
        }

        String fileName = file.getOriginalFilename();
        int sizeInBytes = Math.toIntExact(file.getSize());

        if (fileName == null) {
            throw new ErrorInputData("Error input data: no file name");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FileEntity fileEntity = FileEntity.builder()
                .name(fileName)
                .date(LocalDateTime.now())
                .sizeInBytes(sizeInBytes)
                .content(file.getBytes())
                .contentType(file.getContentType())
                .user(user)
                .build();

        fileRepository.save(fileEntity);

        return new SuccessfulResponse("The file " + file.getOriginalFilename() + " was uploaded successfully");
    }

    @Transactional
    public SuccessfulResponse deleteFile(String fileName) {
        log.info("Request to delete the file: {}", fileName);
        FileEntity fileEntity = fileRepository.findByName(fileName)
                .orElseThrow(() -> new ErrorInputData("Error input data: the file " + fileName +
                        " is not found in the database"));

        fileRepository.deleteById(fileEntity.getId());

        return new SuccessfulResponse("The file " + fileName + " was successfully deleted");
    }

    @Transactional
    public ResponseEntity<Resource> getFile(String fileName) {
        log.info("Request to download the file: {}", fileName);
        FileEntity fileEntity = fileRepository.findByName(fileName)
                .orElseThrow(() -> new ErrorInputData("Error input data: the file " + fileName +
                        " is not found in the database"));

        byte[] content = fileEntity.getContent();

        Resource resource = new ByteArrayResource(content);
        MediaType contentType = MediaType.valueOf(fileEntity.getContentType());

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
    }

    @Transactional
    public SuccessfulResponse editFile(String fileName, NewFileName newFileName) {
        log.info("Request to edit the file: {}", fileName);
        FileEntity fileEntity = fileRepository.findByName(fileName)
                .orElseThrow(() -> new ErrorInputData("Error input data: the file " + fileName +
                        " is not found in the database"));

        if (fileRepository.existsByName(newFileName.getFileName())) {
            throw new ErrorInputData("Error input data: the file " + newFileName.getFileName() +
                    " already exists in the database, please choose another file name");
        }

        fileEntity.setName(newFileName.getFileName());
        fileRepository.save(fileEntity);

        return new SuccessfulResponse("File was successfully renamed");
    }

    @Transactional
    public List<FileEntityDto> getAllFiles(int limit) {
        log.info("Request to show {} file(s)", limit);
        Pageable firstPageWithTwoElements = PageRequest.of(0, limit);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return fileRepository.findAllByUserId(user.getId(), firstPageWithTwoElements).stream()
                .map(FileEntityDto::new)
                .collect(Collectors.toList());
    }
}
