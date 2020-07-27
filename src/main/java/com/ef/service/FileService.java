package com.ef.service;

import com.ef.model.response.FileResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface FileService {

    List<FileResponseDTO> uploadFile(Long employeeID, MultipartFile[] files);

    List<FileResponseDTO> deleteFileInDB(Long employeeID, Set<Long> fileIDs);

    List<FileResponseDTO> deleteFileInDirectory(List<FileResponseDTO> fileResponseDTOList);

    Resource downloadFileByFileID(Long fileID) throws IOException;

    Resource zipAndDownloadFiles(Long employeeID, Set<Long> fileIds) throws IOException;

    List<FileResponseDTO> getAllFilesOfEmployee(Long employeeID);

    List<FileResponseDTO> deleteAllFileOfEmployee(Long employeeID);
}
