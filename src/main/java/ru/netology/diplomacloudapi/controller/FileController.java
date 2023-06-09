package ru.netology.diplomacloudapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomacloudapi.dto.FileEntityDto;
import ru.netology.diplomacloudapi.dto.NewFileName;
import ru.netology.diplomacloudapi.dto.Response;
import ru.netology.diplomacloudapi.dto.SuccessfulResponse;
import ru.netology.diplomacloudapi.service.FileService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * A method that receives MultipartFile, parses it to a FileEntity, and saves the FileEntity to a database
     *
     * @param file an uploaded file received in a multipart request
     * @return a String message (an object of a SuccessfulResponse class) in case of successful uploading of the file
     * @throws IOException in case of access errors (if the temporary store fails)
     */
    @PostMapping("/file")
    public ResponseEntity<Response> saveFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.saveFile(file));
    }

    /**
     * A method that receives String filename in query of a request and deletes the file with
     * the filename from a database
     *
     * @param fileName a String that contains a file name to be deleted
     * @return a String message (an object of a SuccessfulResponse class) in case of successful uploading of the file
     */
    @DeleteMapping("/file")
    public ResponseEntity<SuccessfulResponse> deleteFile(@RequestParam("filename") String fileName) {
        return ResponseEntity.ok(fileService.deleteFile(fileName));
    }

    /**
     * A method that receives a String file name and returns a Resource instance of the file with the specified file name
     *
     * @param fileName a String that contains a file name to be downloaded
     * @return an instance of Resource which is an object representation of the file to be downloaded by a user
     */
    @GetMapping("file")
    public ResponseEntity<Resource> getFile(@RequestParam("filename") String fileName) {
        return fileService.getFile(fileName);
    }

    /**
     * A method that receives a filename in a query and an object NewFileName in the body of
     * a request and renames the file stored in a DB under the filename using a new file name
     *
     * @param fileName    a String contains a file name to be edited
     * @param newFileName an object of a NewFileName class with a single field that contains
     * a new file name
     * @return a String message (an object of a SuccessfulResponse class) in case of successful uploading
     * of the file
     */
    @PutMapping("/file")
    public ResponseEntity<SuccessfulResponse> editFile(@RequestParam("filename") String fileName, @RequestBody NewFileName newFileName) {
        return ResponseEntity.ok(fileService.editFile(fileName, newFileName));
    }

    /**
     * A method that receives in query of a request a number of files to be shown
     * and returns a list of FileEntityDto in corresponding numbers
     *
     * @param limit an int contains a number of requested files
     * @return a list that contains a number of FileEntityDto (String fileName and int sizeInBytes)
     * specified in the limit
     */
    @GetMapping("/list")
    public ResponseEntity<List<FileEntityDto>> getAllFiles(@RequestParam("limit") int limit) {
        return ResponseEntity.ok(fileService.getAllFiles(limit));
    }

}
