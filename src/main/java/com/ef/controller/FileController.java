package com.ef.controller;

import com.ef.model.response.FileResponseDTO;
import com.ef.service.FileService;
import com.ef.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
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
}
