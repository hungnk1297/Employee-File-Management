package com.ef.controller;

import com.ef.model.request.ShareFileRequestDTO;
import com.ef.model.response.FileSharingResponseDTO;
import com.ef.service.FileSharingService;
import com.ef.utils.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/employee/{employeeID}/share-file")
@AllArgsConstructor
public class FileSharingController {

    private final FileSharingService fileSharingService;
    private final Validator validator;

    @GetMapping(path = "/my-sharing")
    public ResponseEntity<List<FileSharingResponseDTO>> findAllSharingFileOfEmployee(
            @RequestHeader(name = "token") String token,
            @PathVariable("employeeID") Long employeeID) {
        if (validator.authorizedEmployeeToken(token, employeeID)) {
            return new ResponseEntity<>(fileSharingService.findAllSharingFileOfEmployee(employeeID), HttpStatus.OK);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping(path = "/sharing-to-me")
    public ResponseEntity<List<FileSharingResponseDTO>> findAllSharedFileForEmployee(
            @RequestHeader(name = "token") String token,
            @PathVariable("employeeID") Long sharedEmployeeID) {
        if (validator.authorizedEmployeeToken(token, sharedEmployeeID)) {
            return new ResponseEntity<>(fileSharingService.findAllSharedFileForEmployee(sharedEmployeeID), HttpStatus.OK);
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<List<FileSharingResponseDTO>> shareFile(
            @RequestHeader(name = "token") String token,
            @PathVariable("employeeID") Long employeeID,
            @RequestBody @Valid ShareFileRequestDTO shareFileRequestDTO) {
        if (validator.authorizedEmployeeToken(token, employeeID) && validator.isOwnerOfFile(employeeID, shareFileRequestDTO.getFileID())) {
            return new ResponseEntity<>(fileSharingService.shareFile(
                    shareFileRequestDTO.getFileID(), shareFileRequestDTO.getSharedEmployeeIDs()), HttpStatus.CREATED);
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping
    public ResponseEntity<List<FileSharingResponseDTO>> stopSharingFile(
            @RequestHeader(name = "token") String token,
            @PathVariable("employeeID") Long employeeID,
            @RequestBody @Valid ShareFileRequestDTO shareFileRequestDTO) {
        if (validator.authorizedEmployeeToken(token, employeeID) && validator.isOwnerOfFile(employeeID, shareFileRequestDTO.getFileID())) {
            return new ResponseEntity<>(fileSharingService.stopSharingFile(
                    shareFileRequestDTO.getFileID(), shareFileRequestDTO.getSharedEmployeeIDs()), HttpStatus.CREATED);
        }

        return ResponseEntity.badRequest().build();
    }
}
