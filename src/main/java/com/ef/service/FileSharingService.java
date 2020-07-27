package com.ef.service;

import com.ef.model.response.FileSharingResponseDTO;

import java.util.List;
import java.util.Set;

public interface FileSharingService {

    List<FileSharingResponseDTO> findAllSharingFileOfEmployee(Long employeeID);

    List<FileSharingResponseDTO> findAllSharedFileForEmployee(Long sharedEmployeeID);

    List<FileSharingResponseDTO> shareFile(Long fileID, Set<Long> sharedEmployeeIDs);

    List<FileSharingResponseDTO> stopSharingFile(Long fileID, Set<Long> sharedEmployeeIDs);

    List<FileSharingResponseDTO> stopSharingFileWhenDeleteEmployee(Long employeeID);
}
