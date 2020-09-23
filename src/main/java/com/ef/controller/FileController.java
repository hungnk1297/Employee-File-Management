package com.ef.controller;

import com.ef.exception.CustomException;
import com.ef.model.response.FileResponseDTO;
import com.ef.service.FileService;
import com.ef.utils.ExceptionGenerator;
import com.ef.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.ef.constant.CommonConstant.FileConstant.*;

//@RestController
@Slf4j
@RequestMapping(path = "/employee/{employeeID}/file")
@AllArgsConstructor
public class FileController {

    private final FileService fileService;
    private final Validator validator;

    @PostMapping
    public ResponseEntity<List<FileResponseDTO>> uploadFile(
            @RequestHeader(name = "token") String token,
            @PathVariable(value = "employeeID") Long employeeID,
            @RequestPart MultipartFile[] files) {
        if (validator.authorizedEmployeeToken(token, employeeID))
            return new ResponseEntity<>(fileService.uploadFile(employeeID, files), HttpStatus.CREATED);

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<FileResponseDTO>> getAllFilesOfEmployee(
            @RequestHeader(name = "token") String token,
            @PathVariable(value = "employeeID") Long employeeID) {
        if (validator.authorizedEmployeeToken(token, employeeID))
            return new ResponseEntity<>(fileService.getAllFilesOfEmployee(employeeID), HttpStatus.OK);

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping
    public ResponseEntity<List<FileResponseDTO>> deleteEmployeeFile(
            @RequestHeader(name = "token") String token,
            @PathVariable(value = "employeeID") Long employeeID,
            @RequestBody Set<Long> fileIDs) {
        if (validator.authorizedEmployeeToken(token, employeeID)) {
            List<FileResponseDTO> deletingFiles = fileService.deleteFileInDB(employeeID, fileIDs);
            deletingFiles = fileService.deleteFileInDirectory(deletingFiles);
            return new ResponseEntity<>(deletingFiles, HttpStatus.OK);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping(path = "/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestHeader(name = "token") String token,
            @PathVariable(value = "employeeID") Long employeeID,
            @RequestParam(value = "fileID") Long fileID,
            HttpServletRequest request) throws IOException {
        if (validator.authorizedEmployeeToken(token, employeeID)
                && validator.authorizedReadPermission(employeeID, fileID)) {
            Resource resource = fileService.downloadFileByFileID(fileID);
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (Exception e) {
                log.error("Could not get the content type!");
            }
            contentType = contentType != null ? contentType : DEFAULT_CONTENT_TYPE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + resource.getFilename() + KEY_SLASH)
                    .body(resource);
        }

        throw new CustomException(ExceptionGenerator.noReadPermission(fileID));
    }

    @GetMapping(path = "/zip-and-download")
    public ResponseEntity<Resource> zipAndDownloadMultipleFiles(
            @RequestHeader(name = "token") String token,
            @PathVariable(value = "employeeID") Long employeeID,
            @RequestBody Set<Long> fileIDs,
            HttpServletRequest request) throws IOException {

        if (validator.authorizedEmployeeToken(token, employeeID)) {
            for (Long fileID : fileIDs) {
                if (!validator.authorizedReadPermission(employeeID, fileID))
                    throw new CustomException(ExceptionGenerator.noReadPermission(fileID));
            }

            Resource resource = fileService.zipAndDownloadFiles(employeeID, fileIDs);
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (Exception e) {
                log.error("Could not get the content type!");
                contentType = DEFAULT_CONTENT_TYPE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + resource.getFilename() + KEY_SLASH)
                    .body(resource);

        }
        return ResponseEntity.badRequest().build();
    }

}
